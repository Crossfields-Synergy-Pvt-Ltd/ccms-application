# User Information Page — Bug Report

> Generated from code review on 2026-07-21
> Scope: Full User Information stack (AngularJS frontend + Java Spring backend + MongoDB)
> Covers: User List page (`#/user`), Add User page (`#/user_add`), Edit User page (`#/user_edit`), Login page, and user CRUD operations

## Severity Legend

| Severity | Meaning |
|---|---|
| 🔴 Critical | Breaks core functionality, causes data loss, or makes the page non-functional |
| 🟠 Major | Causes incorrect behavior, silent failures, or data inconsistency |
| 🟡 Minor | Code quality, maintainability, or UX polish |

---

## Files Examined

| # | File Path | Role |
|---|---|---|
| 1 | `CCMS_UI/.../app/user/user-list.html` | User list page template (123 lines) |
| 2 | `CCMS_UI/.../app/user/user-add.html` | Add user form template (416 lines) |
| 3 | `CCMS_UI/.../app/user/user-update.html` | Edit user form template (571 lines) |
| 4 | `CCMS_UI/.../app/user/user-controllers.js` | All user CRUD controllers (151 lines) |
| 5 | `CCMS_UI/.../app/user/user-factory.js` | User API factory (42 lines) |
| 6 | `CCMS_UI/.../app/app.js` | State definitions |
| 7 | `CCMS_UI/.../app/navi/leftnavi.html` | Navigation menu (lines 224-226, 369-371) |
| 8 | `CCMS_UI/.../controller/UserController.java` | User CRUD + login REST controller (198 lines) |
| 9 | `CCMS_UI/.../dao/UserDaoImpl.java` | MongoDB DAO (68 lines) |
| 10 | `CCMS_UI/.../pojo/User.java` | User MongoDB POJO (311 lines) |
| 11 | `CCMS_UI/.../services/UserServices.java` | Service interface |
| 12 | `CCMS_UI/.../services/UserServicesImpl.java` | Service implementation |
| 13 | `SERVER/.../model/User.java` | SERVER-side user POJO (separate model) |
| 14 | `db/seeds/seed.sh` | Seed script inserting users from USERS_JSON |
| 15 | `scripts/api-smoke-test.sh` | API smoke tests including login/create |

---

## 🔴 Critical Bugs

### 1. `@Id` annotation on `firstName` instead of `email` — MongoDB identity collision

**File:**
- `User.java:9-10`

**Description:**
```java
@Id
private String firstName;
```

The `@Id` annotation marks `firstName` as MongoDB's `_id` field. This means `mongoTemplate.save()` uses `firstName` as the document identifier. Two users with the same first name **overwrite each other** silently in the database.

The seed script (`seed.sh`) explicitly sets `_id: u.email`, creating a conflict: seed-created users have `_id = email`, but UI-created users have `_id = firstName`. Queries by email via `Criteria.where("email")` still work because `email` is a separate field, but `save()` with duplicate `firstName` is destructive.

**Fix:**
Move `@Id` to the `email` field:
```java
@Id
private String email;
```

---

### 2. Update calls `add()` — no update endpoint exists

**Files:**
- `user-controllers.js:127-133` (update controller)
- `UserController.java` (no update endpoint)

**Description:**
The Edit page's `update()` function calls the same `add()` method used for creating users:
```javascript
// user-controllers.js line 130
userFactory.add($scope.user);  // POST /superadmin/user/create
```

There is no `PUT` or `PATCH` endpoint in `UserController.java`. The backend's `add()` method saves the object as-is. If the `_id` matches an existing document (which it won't reliably, given bug #1), `mongoTemplate.save()` would overwrite it. But the combination of:
- `_id = firstName` (bug #1)
- Edit page may change `firstName`

...means the saved document often creates a **new** document with a different `_id` rather than updating the original.

**Fix:**
Add a dedicated `PUT /superadmin/user/update` endpoint in `UserController.java` and a corresponding `userFactory.update()` method.

---

### 3. Delete passes `undefined` — field name typo in template

**File:**
- `user-list.html:109`

**Description:**
```html
<md-icon class="material-icons" ng-click="deleteconf(obj.firstname);"
    style="color:#F44336;font-size: 20px; height: 20px">delete</md-icon>
```

The template uses `obj.firstname` (all lowercase `n`), but the POJO field is `firstName` (camelCase `N`). JavaScript property access is case-sensitive. `obj.firstname` resolves to `undefined`.

The delete confirmation modal receives `undefined` as the `id` parameter, then calls:
```javascript
userFactory.delete(undefined);  // GET /superadmin/user/delete/undefined
```

**Fix:**
Change to `obj.firstName` (capital N):
```html
ng-click="deleteconf(obj.firstName);"
```

---

### 4. Delete DAO queries by nonexistent `name` field

**File:**
- `UserDaoImpl.java:60-66`

**Description:**
```java
public boolean deleteEntity(String id) throws Exception {
    Query query = new Query();
    query.addCriteria(Criteria.where("name").is(id));  // no "name" field exists
    mongoTemplate.remove(query, User.class);
    return true;
}
```

The `User` POJO has no `name` field. The fields are `firstName`, `lastName`, and `email`. Combined with bug #3 (delete passes `undefined`), the query becomes `{name: undefined}` — which matches no document. The delete always succeeds silently but never removes anything.

**Fix:**
Query by the correct field — either `email` or `_id`:
```java
query.addCriteria(Criteria.where("email").is(id));
```

---

### 5. Hardcoded login — database users cannot authenticate

**File:**
- `UserController.java:67-94`

**Description:**
The login endpoint only matches two hardcoded credentials:
```java
private User authenticateHardcoded(String name, String password) {
    if ("admin@example.com".equals(name) && "admin123".equals(password)) {
        return createHardcodedUser("admin@example.com", "Admin", "User", "ADMIN", true);
    }
    if ("user@example.com".equals(name) && "user123".equals(password)) {
        return createHardcodedUser("user@example.com", "Regular", "User", "USER", false);
    }
    return null;
}
```

Users created through the UI or inserted via `USERS_JSON` / `seed.sh` into the `ccms_user_details` collection **cannot log in**. The login endpoint does not query MongoDB at all. If the hardcoded passwords are changed in `USERS_JSON`, the login still uses the compiled defaults.

**Fix:**
Query the `ccms_user_details` collection to authenticate against database users:
```java
User user = userServices.getEntityById(name);  // query by email
if (user != null && user.getPassword().equals(password)) {
    return user;
}
```

---

### 6. Login credentials sent as GET query parameters in plaintext

**File:**
- `UserController.java:67-69`

**Description:**
```java
@RequestMapping(value = "/login", method = RequestMethod.GET)
public @ResponseBody User login(@RequestParam("name") String name,
        @RequestParam("password") String password) {
    System.out.println("LOGIN REQUEST RECIVED : " + name + " | " + password);
```

Credentials are transmitted as URL query parameters:
- `GET /superadmin/user/login?name=admin@example.com&password=admin123`
- Logged in plaintext to stdout (line 71)
- Visible in browser history, server logs, and proxy logs
- Exposed via HTTP Referer headers

Additionally, the password is stored and transmitted in plaintext throughout the system (no hashing).

**Fix:**
Change to `POST` with credentials in the request body. Use HTTPS. Hash passwords server-side.

---

### 7. `getEntityById()` has no null check — crashes on empty result

**File:**
- `UserDaoImpl.java:43-51`

**Description:**
```java
public User getEntityById(String id) throws Exception {
    Query query = new Query();
    query.addCriteria(Criteria.where("email").is(id));
    List<User> list = mongoTemplate.find(query, User.class);
    return list.get(0);  // IndexOutOfBoundsException if list is empty
}
```

If no user matches the given email, `list.get(0)` throws `IndexOutOfBoundsException`. This crashes any caller that queries a non-existent user.

**Fix:**
```java
return list.isEmpty() ? null : list.get(0);
```

---

## 🟠 Major Bugs

### 8. Password stored and transmitted in plaintext

**Files:**
- `User.java:14`
- `UserController.java`
- `seed.sh`

**Description:**
The `User` POJO stores the password as a plain `String`:
```java
private String password;
```

The seed script (`seed.sh`) also stores passwords as plaintext. There is no hashing (bcrypt, scrypt, or any other) anywhere in the user management stack. The login endpoint compares passwords with `equals()` on raw strings.

**Fix:**
Use Spring Security's `BCryptPasswordEncoder` or similar to hash passwords on create/update and verify on login.

---

### 9. `ng-change="user.fistName"` typo creates spurious property

**Files:**
- `user-add.html:26`
- `user-update.html:36`

**Description:**
```html
<input ... ng-model="user.firstName" ng-change="user.fistName" ...>
```

`ng-change="user.fistName"` assigns the value to `user.fistName` (missing 'r') each time the field changes. This creates an unintended `fistName` property on the user object that gets saved to MongoDB as an extra field. The correct expression should be the identity assignment `user.firstName` or omitted entirely.

**Fix:**
Remove `ng-change` or fix to `ng-change="user.firstName"`.

---

### 10. Pagination references undefined `todos` array

**File:**
- `user-list.html:119-121`

**Description:**
```html
<pagination boundary-links="true" max-size="3"
    items-per-page="itemsPerPage" total-items="todos.length"
    ng-model="currentPage" ng-change="pageChanged()"></pagination>
```

`total-items="todos.length"` references `$scope.todos`, which is never defined in the controller — only `$scope.listData` is set. Additionally, `itemsPerPage`, `currentPage`, and `pageChanged()` are never defined. Pagination is completely non-functional.

**Fix:**
Set `total-items="listData.length"` and implement the missing pagination variables.

---

### 11. No duplicate email check — `save()` silently overwrites

**File:**
- `UserDaoImpl.java:37-39`

**Description:**
```java
mongoTemplate.save(user, "ccms_user_details");
```

`mongoTemplate.save()` uses MongoDB's `save()` semantics: if a document with the same `_id` exists, it is overwritten. Since `_id = firstName` (bug #1), two users with the same first name silently overwrite each other. Even if `_id` were fixed to `email`, there is no check for existing email before creating a new user.

**Fix:**
Query for existing email before saving. Return an error if the email is already registered.

---

### 12. No input validation in controller

**File:**
- `UserController.java:32-43`

**Description:**
```java
public @ResponseBody Status add(@RequestBody User obj) {
    try {
        userServices.addEntity(obj);
        return new Status(200, "Success");
    } catch (Exception e) {
        return new Status(0, e.toString());
    }
}
```

The controller accepts any JSON without validation — empty objects, null fields, invalid email formats, missing required fields. If `obj` is `{}`, the DAO saves a mostly-null document.

**Fix:**
Add `@Valid` annotations or manual field validation before saving.

---

### 13. Role dropdown only has ADMIN/USER — cannot create SUPER ADMIN

**Files:**
- `user-add.html:82-84`
- `user-update.html:141-142`

**Description:**
The role dropdown only offers two options:
```html
<option>ADMIN</option>
<option>USER</option>
```

The seed script and `.env.example` reference `SUPER ADMIN` as a role with all privileges. There is no way to create a SUPER ADMIN user through the UI. To grant all privileges, an ADMIN must manually check every privilege checkbox (13 checkboxes).

**Fix:**
Add `SUPER ADMIN` to the role dropdown, or implement a "Select All" toggle for privileges.

---

### 14. Mobile Number field is commented out on the edit page

**File:**
- `user-update.html:98-105`

**Description:**
```html
<!-- <div class="form-group">
    <label class="col-md-4 control-label">Mobile Number:</label>
    <div class="col-md-8">
        <input type="text" class="form-control" ... ng-model="user.mobnum1">
    </div>
</div> -->
```

The Mobile Number field is wrapped in HTML comments on the edit page. Users cannot edit their mobile number when updating their profile. The add page (lines 64-71) has the field active.

**Fix:**
Uncomment the Mobile Number field on the edit page.

---

### 15. Duplicate state "Andra Pradesh" with two different misspellings

**Files:**
- `user-add.html:118,122`
- `user-update.html:227,231`

**Description:**
The state dropdown contains both `Andra Pradesh` and `Aandra Pradesh` (both misspellings of `Andhra Pradesh`). This means the same state appears twice with different misspellings, and users can accidentally select either one, creating data inconsistency.

**Fix:**
Replace both with the correct spelling `Andhra Pradesh`.

---

### 16. `ng-disabled` references undefined `myForm`

**File:**
- `user-add.html:393-395`

**Description:**
```html
<button ng-click="ok()"
    ng-disabled="myForm.$invalid || myForm.$pending"
    class="btn btn-info btn-sm ">ADD</button>
```

`myForm` is never defined in the template — there is no `<form name="myForm">` anywhere on the page. `myForm.$invalid` evaluates to `undefined` (falsy), so the button is never disabled. Users can submit even with invalid/missing fields.

**Fix:**
Add a `name` attribute to the form and reference it correctly, or use a different form name that matches an existing `form` element.

---

### 17. No `.catch()` on any factory API call

**File:**
- `user-factory.js`

**Description:**
None of the factory methods have error handling:
- `getAll()` — no `.catch()`
- `getByID()` — no `.catch()`
- `add()` — has `.then()` but no `.catch()`
- `delete()` — has `.then()` but no `.catch()`
- `getByMandal()` — no `.catch()`
- `getByGp()` — no `.catch()`
- `getByVillage()` — no `.catch()`

All API errors are silently swallowed. The controllers never receive error feedback.

**Fix:**
Add `.catch()` handlers to all factory methods.

---

## 🟡 Minor / Code Quality Issues

### 18. "SI NO" header (should be "SL NO")

**File:**
- `user-list.html:46`

```html
<td align="center"><a>SI NO</a></td>
```

Should be `SL NO` (serial number) or `#`.

---

### 19. "Mondal" label typo

**File:**
- `user-add.html:163`

```html
<label class="col-md-4" style="padding-right: 80px" align="left">Mondal</label>
```

Label says "Mondal" but the model is `user.mandal`. Should be "Mandal".

---

### 20. "Associate Features to an Users" grammar

**Files:**
- `user-add.html:194-195`
- `user-update.html:340-341`

```html
Associate Features to an Users
```

"an Users" should be "Users" (no article) or "a User".

---

### 21. Sort header has leading space `' email'`

**File:**
- `user-list.html:68`

```html
ng-show="sortType == ' email' && sortReverse"
```

The `sortType` comparison uses `' email'` (with a leading space) while the actual `ng-click` sets `sortType = 'email'` (no space). The sort icon never shows for the Email column.

---

### 22. State names have multiple typos

**Files:**
- `user-add.html` / `user-update.html` state dropdown

| Current | Correct |
|---|---|
| Telagana | Telangana |
| Uttaranchal | Uttarakhand |
| Madya Pradesh | Madhya Pradesh |
| Karnatka | Karnataka |
| Orissa | Odisha |

---

### 23. Unused `getByID()` factory method

**File:**
- `user-factory.js:11-13`

```javascript
obj.getByID = function(customerID){
    return $http.get(serviceBase + '/superadmin/user/list/' + customerID);
}
```

This method is never called from any controller. Dead code.

---

### 24. No loading indicator on any CRUD operation

**Files:**
- All user templates

None of the CRUD operations (list, save, update, delete) show a spinner or progress indicator. The page appears unresponsive during API calls.

---

### 25. Hardcoded AngularJS runtime CSS classes in static HTML

**Files:**
- `user-add.html:24, 77` (and many others throughout)

```html
<input type="text"
    class="form-control ng-pristine ng-untouched ng-invalid ng-invalid-required ng-valid-pattern ng-valid-maxlength"
    ...>
```

AngularJS classes like `ng-pristine`, `ng-untouched`, `ng-invalid`, etc. are hardcoded in the static HTML source. These are runtime classes applied by AngularJS and should not be in the source template. They may conflict with Angular's actual runtime class application.

---

### 26. `$state.reload()` called before async operations complete

**Files:**
- `user-controllers.js:79-80` (add `ok()`)
- `user-controllers.js:131-132` (update)
- `user-controllers.js:145` (delete modal)

All three operations fire `$state.reload()` synchronously after queuing an async HTTP request:
```javascript
userFactory.add($scope.user);
$state.reload();
$state.go('dashboard.user');
```

The page reloads with old data before the save/update/delete completes.

**Fix:**
Chain navigation in the `.then()` callback:
```javascript
userFactory.add($scope.user).then(function() {
    $state.go('dashboard.user');
});
```

---

## Summary

| Severity | Count | Items |
|---|---|---|
| 🔴 Critical | 7 | `@Id` on firstName, update calls add, delete passes undefined (typo), delete queries `name` field, hardcoded login, GET query auth with plaintext, `getEntityById` crashes on empty |
| 🟠 Major | 10 | Plaintext passwords, `fistName` typo, broken pagination, no duplicate email check, no input validation, role dropdown limited, mobile field commented out, duplicate state misspellings, undefined form ref, no `.catch()` on API calls |
| 🟡 Minor | 9 | "SI NO" header, "Mondal" typo, "an Users" grammar, sort space typo, state name typos, unused getByID, no loading indicators, hardcoded CSS classes, reload before async |

### Recommended Fix Priority

1. **Critical #5 (hardcoded login)** — DB users cannot log in; auth is completely broken
2. **Critical #1 (`@Id` on firstName)** — Users silently overwrite each other in MongoDB
3. **Critical #3+#4 (delete chain)** — Delete passes `undefined` and queries non-existent field
4. **Critical #2 (update calls add)** — Cannot update users; edit page creates duplicates
5. **Critical #6 (GET query auth)** — Credentials exposed in plaintext via URL and stdout
6. **Critical #7 (null crash)** — `getEntityById` throws on any lookup miss
7. **Major #8 (plaintext passwords)** — Security vulnerability

### Related Bugs

This page's **no `.catch()`** pattern matches the same issue in Events, History, Monitor & Control, Switch Point, Operational Hours, Light Status, Schedules, and DCU/RTU Config pages — a system-wide pattern of silent error swallowing.

The **`$state.reload()` before async completes** pattern (bug #26) matches the same bug in Schedules page bug #3 and DCU/RTU Config page bug #2.

# DCU Addition Flow and Button Issues

## Summary

The DCU/RTU addition flow reaches the expected `/dcu/create` endpoint, but the form does not provide a consistent gateway identity, does not enforce validation, and treats business failures as successful HTTP responses. As a result, a manually added DCU can appear to save while remaining unusable in the list, delete, handshake, and configuration workflows.

## Expected flow

1. The user clicks **Add DCU/RTU** from the DCU list.
2. The Add form collects the DCU identity and metadata.
3. The user clicks **Add**.
4. AngularJS sends `POST /dcu/create`.
5. The backend stores the record in MongoDB collection `handshake_info`.
6. The record appears in the DCU list using `gateway_serial_number`.
7. Update, delete, meter, and configuration actions use the same gateway identity.

## Findings

### 1. Add form uses the wrong identity field

The Add form binds the entered serial number to `dcu.serial_number`:

- `CCMS_UI/STARTUP/ccms_ui/src/main/webapp/app/dcu/dcu-add.html:42-50`

The list, handshake processing, configuration actions, and delete flow use `gateway_serial_number`:

- `CCMS_UI/STARTUP/ccms_ui/src/main/webapp/app/dcu/dcu-list.html:87`
- `CCMS_UI/STARTUP/ccms_ui/src/main/webapp/app/dcu/dcu-controllers.js:98-116`
- `CCMS_UI/STARTUP/ccms_ui/src/main/webapp/app/dcu/dcu-factory.js:47-50`

The backend saves the submitted `HandShake` object directly into `handshake_info`:

- `CCMS_UI/STARTUP/ccms_ui/src/main/java/com/vnetsoft/ccms/controller/DCUController.java:45-58`
- `CCMS_UI/STARTUP/ccms_ui/src/main/java/com/vnetsoft/ccms/dao/DCUDaoImpl.java:104-107`

Because the Add form does not populate `gateway_serial_number` or an explicit `id`, MongoDB may create a generated `_id` while the record has no usable gateway serial. The record can then be missing from normal DCU searches and cannot be reliably used by delete/configuration operations.

### 2. Add validation is not enforced

The Add view contains required attributes and validation-related markup, but the forms are unnamed and the Add action is an anchor that directly invokes `ok()`:

- `CCMS_UI/STARTUP/ccms_ui/src/main/webapp/app/dcu/dcu-add.html:8-9`
- `CCMS_UI/STARTUP/ccms_ui/src/main/webapp/app/dcu/dcu-add.html:355-357`
- `CCMS_UI/STARTUP/ccms_ui/src/main/webapp/app/dcu/dcu-controllers.js:281-288`

There is no reliable form-validity check before the request. Empty or malformed DCU metadata can therefore be submitted.

### 3. Add reports success for backend business failures

The backend catches exceptions and returns `Status.code = 0`, but still responds with HTTP 200:

- `CCMS_UI/STARTUP/ccms_ui/src/main/java/com/vnetsoft/ccms/controller/DCUController.java:45-58`

The frontend treats every resolved HTTP request as success and immediately shows “DCU saved successfully”:

- `CCMS_UI/STARTUP/ccms_ui/src/main/webapp/app/dcu/dcu-controllers.js:281-288`

The response body is not checked for `code == 0`. A failed save can therefore be presented as successful and the user is redirected to the list.

### 4. Locate on map is unreliable

The map implementation has several defects:

- `initialize()` is invoked immediately rather than passed as a load callback.
- The drag handler writes to `#latlong`, but the form fields are `#lat` and `#long`.
- The handler references `infoWindow`, which is never declared.
- The marker movement does not reliably update `dcu.lat` and `dcu.lang` through AngularJS.

Relevant code:

- `CCMS_UI/STARTUP/ccms_ui/src/main/webapp/app/dcu/dcu-controllers.js:251-278`
- `CCMS_UI/STARTUP/ccms_ui/src/main/webapp/app/dcu/dcu-add.html:308-331`

### 5. Update form submit handler does not match the controller

The Update form declares `ng-submit="ok()"`:

- `CCMS_UI/STARTUP/ccms_ui/src/main/webapp/app/dcu/dcu-update.html:6`

The controller defines `update()`, not `ok()`:

- `CCMS_UI/STARTUP/ccms_ui/src/main/webapp/app/dcu/dcu-controllers.js:378-385`

Clicking the Save button invokes `update()`, but pressing Enter to submit the form can call an undefined handler.

### 6. Update Save button contains a static disabled attribute

The Save button has both `ng-disabled` and a literal `disabled="disabled"`:

- `CCMS_UI/STARTUP/ccms_ui/src/main/webapp/app/dcu/dcu-update.html:341-343`

Depending on AngularJS directive processing and form state, the static attribute can leave the button disabled or make its behavior difficult to reason about.

### 7. Delete does not wait for the API result

The delete modal invokes the delete request, closes immediately, and reloads the state without handling success or failure:

- `CCMS_UI/STARTUP/ccms_ui/src/main/webapp/app/dcu/dcu-controllers.js:432-442`

The backend deletes by Mongo `_id`:

- `CCMS_UI/STARTUP/ccms_ui/src/main/java/com/vnetsoft/ccms/dao/DCUDaoImpl.java:63-68`

The list passes `gateway_serial_number` as the ID:

- `CCMS_UI/STARTUP/ccms_ui/src/main/webapp/app/dcu/dcu-list.html:96-98`

This works for automatically registered handshake documents whose `_id` is the gateway serial, but can fail for manually added records with generated Mongo IDs.

### 8. Configuration actions depend on the same identity mismatch

The Settings button loads configuration using the gateway serial:

- `CCMS_UI/STARTUP/ccms_ui/src/main/webapp/app/dcu/dcu-controllers.js:108-110`
- `CCMS_UI/STARTUP/ccms_ui/src/main/webapp/app/dcu/dcu-factory.js:26-30`

If a manually added record only contains `serial_number`, configuration lookup will not necessarily find the corresponding DCU.

## Button behavior summary

| Button | Current behavior | Result |
|---|---|---|
| Add DCU/RTU | Navigates to Add route | Works |
| Add | Posts to `/dcu/create` | Request works, but identity and validation are unsafe; failures may appear successful |
| Cancel | Returns to DCU list | Works; no unsaved-change warning |
| Locate on map | Initializes map and marker | Unreliable; coordinate/model update defects |
| Edit | Opens Update route with selected object | Route works |
| Save / Update | Calls `update()` on click | Enter-submit mismatch and possible static disabled state |
| Delete | Sends delete request and immediately reloads | No result handling; may fail for generated Mongo IDs |
| Modify configuration | Loads and saves DCU configuration | Depends on valid gateway identity |
| Sync node/scheduler | Calls configuration sync endpoints | Uses expected gateway serial path |

## Recommended corrective actions

1. Use one canonical identity field, preferably `gateway_serial_number`, throughout the Add form, backend, list, delete, and configuration flows.
2. Set the Mongo document ID consistently when creating a DCU, or delete by the actual Mongo `_id` returned by the API.
3. Give the Add and Update forms explicit AngularJS form names and validate them before submission.
4. Make the backend return appropriate HTTP error statuses, or make the frontend inspect `Status.code` before showing success.
5. Fix the map callback, coordinate element IDs, Angular model updates, and undefined `infoWindow` reference.
6. Change the Update form submit handler to call the existing controller method.
7. Remove the static `disabled="disabled"` attribute and rely on a tested `ng-disabled` expression.
8. Wait for delete completion and display a failure notification when the API rejects the request.

## Verification performed

- Static inspection covered the DCU list, Add, Update, Modify Configuration, Delete, map action, AngularJS factory, controller, Spring controller, service, DAO, model, and route definitions.
- The DCU Maven module previously compiled successfully with `mvn test -DskipTests`.
- No source files were changed during this inspection.

# User Page CRUD and Authentication Fixes

## Scope

Fixed the user management stack in `CCMS_UI/STARTUP/ccms_ui`. The separate
`SERVER` user-settings model was not involved in these defects.

## Backend changes

- Made `email` the MongoDB identifier for `ccms_user_details` and added null-safe lookups.
- Added a dedicated `PUT /superadmin/user/update/{email}` endpoint.
- Added duplicate-email and required-field validation.
- Corrected deletion to query by email and return whether a document was removed.
- Replaced hardcoded login with database-backed authentication.
- Changed login from GET query parameters to POST JSON credentials.
- Added PBKDF2 password hashing for newly created and changed passwords.
- Existing seed-created plaintext passwords remain temporarily compatible; a successful
  legacy login upgrades the stored password to PBKDF2.
- Passwords are removed from list/login responses and redacted from `User.toString()`.

## Frontend changes

- Added separate update API usage and fixed add/update/delete navigation to wait for HTTP completion.
- Fixed deletion to pass the user email.
- Changed login to POST credentials in the request body.
- Added user API error handling and loading state behavior.
- Repaired pagination source and email sort indicator.
- Fixed form/model typos, enabled mobile editing, removed hardcoded Angular state classes,
  enabled `SUPER ADMIN`, and normalized state names and labels.

## Data and deployment notes

- Do not modify `db/seeds/seed.sh` or deployment files for this change.
- Existing documents with first-name-based `_id` values should be audited before deployment.
  The seed format already uses email as `_id`.
- Production login must continue to be served through HTTPS.
- Existing `USERS_JSON` passwords can be used for the first compatibility login; they are
  upgraded when that user authenticates. New application-created users are hashed immediately.

## Verification

- Java sources and user controller tests compile with Java 8 source/target settings using
  the repository dependency cache.
- A direct JUnit attempt was blocked by the repository's legacy Mockito/Spring logging
  dependency mix on the installed JDK (`NoSuchMethodError` in SLF4J after adding the
  project's required module-open flags); this is an environment/dependency issue rather
  than an assertion failure.
- AngularJS tests were updated for POST login requests.
- The repository has no Maven executable or wrapper in this environment, so `mvn test` could
  not be invoked directly.
- `npm test` could not start Karma in the current environment; the installed Puppeteer/Chrome
  launcher exits with `{ inspect: [Function: inspect] }` before executing tests. Re-run in an
  environment with a compatible Karma/Chrome combination.
- Run the full checks when dependencies are available:

```bash
mvn test
npm test
bash scripts/api-smoke-test.sh
```

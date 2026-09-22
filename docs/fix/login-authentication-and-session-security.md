# Login Authentication and Session Security Fix

## Summary

The login flow now uses a server-issued, signed authentication token instead of a Base64-encoded identity string. The UI protects dashboard routes, supports session-only and remembered sessions, and clears the active authorization header on logout.

## Implemented changes

### Backend

- Added `AuthTokenService` using HMAC-SHA256 tokens with an eight-hour expiry.
- Added `AuthInterceptor` and registered it in both Spring configurations used by local and Docker builds.
- Protected application API requests centrally and preserved unauthenticated public monitor endpoints.
- Changed invalid credentials to return HTTP `401 Unauthorized`.
- Changed authentication/database failures to return HTTP `500 Internal Server Error`.
- Kept legacy password migration, but a migration-write failure no longer causes a valid login to fail.
- Marked the response-only token field as non-persistent in MongoDB.

### Frontend

- Replaced the anchor-based sign-in action with a validated `ng-submit` form.
- Added `username` and `current-password` autocomplete hints and accessible label associations.
- Prevented duplicate submissions while a login request is pending.
- Added a centralized `authService` for storing, restoring, and clearing sessions.
- Uses `sessionStorage` by default and `localStorage` only when Remember Me is selected.
- Added dashboard route protection and return-to-route behavior.
- Fixed logout to clear storage, in-memory privilege data, and the default HTTP authorization header.
- Removed the non-functional forgot-password action text.

## Tests added or updated

- Token issuance, parsing, and tamper rejection.
- Authentication interceptor rejection of missing tokens.
- Authentication interceptor acceptance of valid tokens.
- Public monitor endpoint access without a token.
- Login success response token presence.
- Invalid credentials returning `401`.
- Authentication service failure returning `500`.
- Updated AngularJS login tests for token-backed sessions and Remember Me behavior.

## Operational notes

The signing secret is generated securely when the UI application starts. This intentionally invalidates all active UI sessions whenever the application restarts. A future deployment hardening change should externalize a shared signing secret through the deployment environment if multiple UI instances are introduced.

The public monitor API paths are explicitly allow-listed in `AuthInterceptor`. Any new unauthenticated API must be reviewed and added deliberately rather than being exposed by default.

## Verification

- Backend: `mvn test -q` — 83 tests passed.
- Frontend: `npm test -- --single-run` — 74 tests passed.

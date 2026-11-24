# DCU form validation, accessibility, and mixed-content fix

## Problem

The DCU add page could raise an Angular date-picker exception because its
`dcu.date` model was not initialized. The same page submitted the date-picker
value and numeric text fields without normalising them first, which could make
Jackson reject the `/dcu/create` request with HTTP 400.

The monitor page also emitted Angular Material accessibility warnings because
its checkboxes had visible labels but no direct `aria-label` attributes. The
HTTPS application additionally referenced a Selectize stylesheet over HTTP,
which browsers blocked as mixed content.

## Changes

- Initialize the DCU add model with a date field.
- Normalize Moment date values to `YYYY-MM-DD` before submission.
- Convert connected-load and light-count values to integer numbers before submission.
- Reject nonnumeric and decimal values before the request is sent.
- Add integer input patterns to the DCU form.
- Add explicit ARIA labels to all monitor checkboxes.
- Remove the redundant HTTP Selectize CDN stylesheet; the local HTTPS-safe
  stylesheet remains in use.

The backend continues to accept the existing `HandShake` payload and retains
its existing update behavior that only changes UI-owned fields, preserving
Netty/protocol fields.

## Verification

- Angular controller tests verify model initialization.
- Angular controller tests verify date and numeric payload normalization.
- Controller tests verify malformed numeric JSON remains a 4xx response.
- Manual browser verification should confirm no date-picker exception, no
  mixed-content warning, and no missing-ARIA warnings on the affected pages.

## Deployment note

The UI image must be rebuilt and redeployed for the template and JavaScript
changes to be served. No Docker, NPM, port, context-path, or deployment
configuration was changed.

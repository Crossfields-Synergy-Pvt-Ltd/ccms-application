# Schedules Page Fixes

## Scope

This change addresses the schedule list, create, edit, delete, persistence, and
automatic DCU synchronization issues documented in
docs/bugs/schedules-page-bugs.md.

## Implemented

- Standardized schedule lookups to prefer the numeric MongoDB _id, while
  retaining schedule-name lookup for existing handshake records.
- Made schedule deletion safe for both numeric and string identifiers.
- Added a dedicated PUT /scheduler/update endpoint.
- Changed the edit page to use the update endpoint and wait for the HTTP
  promise before navigating.
- Made create and delete navigation wait for completion instead of reloading
  before the request finishes.
- Corrected edit-page time-slot indexing, timezone binding, fault-detection
  defaults, pagination, and malformed sunrise-offset bindings.
- Removed the non-functional Apply-to-Light and Apply-to-RTU/DCU links.
- Added null/error responses for failed schedule synchronization requests.
- Moved schedule auto-sync work off the schedule-save request thread and added
  per-DCU error logging.
- Added an in-process monotonic ID source to avoid same-millisecond ID
  collisions.
- Added frontend regression tests for schedule lookup, update, and delete
  requests.

## Communication impact

The existing UI-to-SERVER REST endpoint
/user/push/sync_scheduler_conf and the SERVER-to-DCU Netty schedule packet
format were preserved. The backend changes alter request timing and failure
reporting, not the device wire protocol. Schedule synchronization now happens
asynchronously after the schedule is persisted.

## Verification


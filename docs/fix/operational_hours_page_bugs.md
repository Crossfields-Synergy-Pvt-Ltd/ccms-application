# Operational Hours Page Bug Fixes

## Scope

This fix covers the Operational Hours and Modified Operational Hours pages:

- AngularJS controllers, templates, and factories
- IO timestamp conversion in the Spring controller
- CSV operational-hours parsing and sorting metadata
- Regression tests for frontend behavior and backend helpers

## Fixed issues

- Removed request-shared IO operation state from `IOController`.
- Restored Mongo timestamp conversion to India-local date, hour, and minute values.
- Removed undefined View-button arguments and added DCU selection validation.
- Prevented CSV export responses from replacing the table data.
- Replaced deprecated AngularJS `.success()`/`.error()` usage with promise handling.
- Added loading and error feedback for data, filter, and export operations.
- Corrected date presets, including Today and the previous calendar month.
- Moved pagination functions out of API callbacks.
- Removed the hardcoded DCU initialization request and unused factory method.
- Added Village filtering and cleared dependent cascade selections.
- Fixed malformed labels, hardcoded date-picker positioning, heading mismatch, and the cumulative spelling error.
- Added numeric minute sort keys while preserving the displayed `hours:minutes` values.
- Replaced hardcoded phase text with optional backend-provided node data.

## Compatibility notes

The existing `opration_*` field names were retained because they are part of the current MongoDB/Java/Angular data contract. Renaming them requires a coordinated data migration and API compatibility plan.

Existing CSV files remain compatible. Newly generated CSV rows now include the raw IO node identifier as an optional ninth column; older files without that column display an empty node value rather than inventing a phase.

## Verification

The following regression coverage was added:

- `IOControllerTest`: timestamp conversion and request-state isolation.
- `FileUtilTest`: duration-to-minute sorting conversion.
- `operationalhours-controller.test.js`: DCU validation, loading, pagination, export isolation, date presets, and Modified Operational Hours validation.

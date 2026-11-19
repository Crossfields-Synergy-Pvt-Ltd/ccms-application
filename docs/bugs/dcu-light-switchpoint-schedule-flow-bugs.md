# DCU Configuration, Light Control, Switch Point, and Schedule Flow Bugs

## Scope

This document records the investigation of four reported issues:

1. DCU configuration edits cannot reliably be saved unless the mobile number is changed.
2. Light ON/OFF behavior does not provide a reliable end-to-end result.
3. The Switch Point Summary page can appear empty when loading or searching.
4. Schedule ON/OFF/DIM actions cannot be selected and represented reliably.

This is an investigation report only. No implementation changes are included.

## 1. DCU configuration Save & Proceed

### Observed behavior

The Save & Proceed button is disabled based on the complete Angular form validity:

```html
ng-disabled="newMeterForm.$invalid || submitting"
```

The edit form marks several fields as required, including panel ID, DCU name,
serial number, model, mobile number, total lights, total load, and installation
date. The mobile-number field also has restrictive pattern and length rules.

Existing DCU records can therefore be invalid when loaded into the edit form.
Changing the mobile number can make the form valid, which explains why the
button appears to become enabled only after that field is changed.

### Save path

The form is connected to `update()` through `ng-submit`. When enabled, the
controller posts the object to:

```text
POST /dcu/create
```

The endpoint is an upsert, despite the `create` name. The backend updates the
UI-owned fields and preserves protocol fields written by the Netty server.

### Root cause

The primary failure is frontend validation and incomplete normalization of
existing DCU data, not a missing submit handler. The “no action” symptom occurs
when the button remains disabled. If the request is sent, the controller and
backend have a response path.

### Required correction

- Validate only fields that are genuinely mandatory.
- Normalize existing values before validation.
- Revisit the mobile-number pattern and minimum length.
- Ensure all required edit fields are populated from the stored record.
- Clarify the create/upsert endpoint behavior or provide a dedicated update path.
- Add tests for editing each field without changing the mobile number.

## 2. Light ON/OFF flow

### Current flow

```text
Switch Point page
  -> GET /dashboard/instant_data_id/{gateway_serial_number}
  -> display light_status
  -> click light icon
  -> GET /device_conf/lights_on or /device_conf/lights_off
  -> UI backend loads the DCU handshake
  -> calls SERVER /user/push/manuval_on or /user/push/manuval_off
  -> Netty locates the DCU channel
  -> binary command is written to the device channel
```

### Confirmed problems

1. The UI sends `serial_number` as `device_identifier`, but the UI backend
   validates that value and then obtains the actual gateway identifier from the
   handshake. The identifier contract is therefore inconsistent.

2. The Netty method returns no delivery result. If the DCU is offline it logs
   an error, but the upper layers can still return HTTP success.

3. The server-side command endpoint returns `DONE` after attempting the send,
   not after confirming delivery or device acknowledgement.

4. The UI flips its local `light_status` after an HTTP success response. This
   can show ON or OFF even when the DCU never received the command.

5. The switch point page has no command-busy guard, so repeated clicks can
   issue duplicate commands.

6. There is no device acknowledgement or status refresh after the command.

### Required correction

- Return actual channel/delivery status from Netty.
- Propagate offline and connection failures to the UI.
- Disable the control while a command is pending.
- Refresh status after a successful command or process a device acknowledgement.
- Use one consistent identifier contract.
- Add tests for online, offline, invalid identifier, backend failure, and
  repeated-click scenarios.

## 3. Switch Point Summary page

### Current flow

The page loads the DCU list through:

```text
GET /dcu/dcu_name_list?district=...&mandal=...&gp=...
```

After selection, it loads the summary through:

```text
GET /dashboard/instant_data_id/{gateway_serial_number}
```

The search control filters the already-loaded list in the browser; it is not a
server-side search.

### Problems found

1. The list request depends on district, mandal, and GP privilege values. The
   controller captures those values when it starts. If privileges are not yet
   initialized, the request can use incorrect filters.

2. The backend catches database and filter exceptions and returns an empty list
   instead of an HTTP error. The UI then appears blank.

3. `ui-select` tagging is enabled. A user can enter an arbitrary value that is
   not a real DCU and therefore has no `gateway_identifier`. The View function
   then returns silently without loading data.

4. There is no clear distinction between “no DCUs found” and “the API failed.”

### Required correction

- Load privileges before constructing the DCU query.
- Disable tagging or reject selections without `gateway_identifier`.
- Display a clear empty-result message.
- Return proper HTTP errors instead of silently converting backend failures to
  empty lists.
- Add server-side search if the list can be large.
- Add tests for empty results, invalid selections, privilege filters, and
  navigation from Monitor & Control.

## 4. Schedule ON/OFF/DIM actions

### Current behavior

The add and update templates contain click handlers for ON, OFF, and DIM. The
handlers assign values to `handle_0_status` through `handle_4_status`.

### Confirmed problems

1. The new-schedule object does not initialize the handle status fields.
   Statuses remain undefined until a button is clicked.

2. The selected button styling is hard-coded with Bootstrap `active` classes.
   Clicking a different action changes the model but does not update the visible
   selected state.

3. `ng-model` is attached to `<a>` elements. It does not provide selection
   behavior for these action buttons.

4. The schedule list always displays the first action as OFF and the second as
   ON, regardless of the saved statuses.

5. The backend command generator only distinguishes ON from everything else.
   DIM is therefore transmitted as OFF, and the dim-value fields are ignored.

6. Inactive slots are initialized with zero-like times and are not consistently
   excluded using the `time_slot_1` through `time_slot_5` flags.

7. A slot with a time but no status can cause a null-status failure when the
   backend calls `equalsIgnoreCase()`.

### Consequence

The UI can assign `ON` to the first handle and `OFF` to the second in memory,
but the visual selection, saved summary, and device protocol output do not
consistently reflect that choice. DIM is not implemented end-to-end.

### Required correction

- Initialize every action status explicitly.
- Use one action model per slot: `ON`, `OFF`, or `DIM`.
- Bind button styling to the selected action.
- Respect enabled/disabled schedule slots.
- Safely handle missing statuses in the backend.
- Implement DIM protocol generation and dim values.
- Render schedule summaries from saved statuses instead of hard-coded labels.
- Add tests for OFF-to-ON, ON-to-OFF, DIM, disabled slots, missing status, and
  schedule synchronization.

## Affected files

- `CCMS_UI/STARTUP/ccms_ui/src/main/webapp/app/dcu/dcu-update.html`
- `CCMS_UI/STARTUP/ccms_ui/src/main/webapp/app/dcu/dcu-controllers.js`
- `CCMS_UI/STARTUP/ccms_ui/src/main/java/com/vnetsoft/ccms/controller/DCUController.java`
- `CCMS_UI/STARTUP/ccms_ui/src/main/java/com/vnetsoft/ccms/dao/DCUDaoImpl.java`
- `CCMS_UI/STARTUP/ccms_ui/src/main/webapp/app/switchpoint/switchpoint-controllers.js`
- `CCMS_UI/STARTUP/ccms_ui/src/main/webapp/app/switchpoint/switchpoint-list.html`
- `CCMS_UI/STARTUP/ccms_ui/src/main/webapp/app/switchpoint/switchpoint-factory.js`
- `CCMS_UI/STARTUP/ccms_ui/src/main/java/com/vnetsoft/ccms/controller/DeviceConfigurationController.java`
- `SERVER/ccms/src/main/java/com/vetsoft/ccms/controller/UIConfController.java`
- `SERVER/ccms/src/main/java/com/vetsoft/ccms/netty/server/ServerHandler.java`
- `CCMS_UI/STARTUP/ccms_ui/src/main/webapp/app/schedules/schedules-add.html`
- `CCMS_UI/STARTUP/ccms_ui/src/main/webapp/app/schedules/schedules-update.html`
- `CCMS_UI/STARTUP/ccms_ui/src/main/webapp/app/schedules/schedules-controllers.js`
- `CCMS_UI/STARTUP/ccms_ui/src/main/webapp/app/schedules/schedules-list.html`


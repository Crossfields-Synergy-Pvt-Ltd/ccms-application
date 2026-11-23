# Isolate user synchronization from data seeding

## Problem

Previously, `SEED_DATA=false` caused `seed.sh` to exit before synchronizing
`USERS_JSON`. A fresh deployment therefore had no `ccms_user_details` user,
so login failed. Because the application APIs require an authentication token,
DCU creation failed as a consequence.

## Change

User synchronization is now controlled independently by `SYNC_USERS`:

| Variable | Responsibility | Default |
|---|---|---|
| `SEED_DATA` | Restore the Mongo archive and apply the MySQL schema | `true` |
| `SYNC_USERS` | Insert missing users from `USERS_JSON` | `true` |

The user sync remains skip-if-exists. Existing users and passwords are not
overwritten.

## Fresh deployment without application seed data

Use:

```text
SEED_DATA=false
SYNC_USERS=true
USERS_JSON=[{"email":"admin@example.com","password":"CHANGE_ME","role":"SUPER ADMIN","first_name":"Admin","last_name":"User","all_privileges":true}]
```

This creates the configured user and allows login and authenticated DCU
creation, but does not restore schedules, hierarchy, historical DCUs, or other
application data from `mongo.archive`. Those records must be restored or
created separately.

## Disabling user synchronization

Set `SYNC_USERS=false` only when users are provisioned by another process or
restored from a separate backup. With no users in MongoDB, login is expected to
fail.

## Verification

1. Start with empty MongoDB/MySQL volumes and `SEED_DATA=false`,
   `SYNC_USERS=true`.
2. Confirm the seed logs show user synchronization but no archive restore or
   MySQL schema application.
3. Log in using the configured `USERS_JSON` credentials.
4. Confirm `POST /dcu/create` succeeds after login.
5. Repeat with `SYNC_USERS=false` and confirm that login fails when no user is
   provisioned separately.

---
name: conventional-commit
description: Use when writing commit messages or asking about commit conventions. Guides commit message format per https://www.conventionalcommits.org/
---

# Conventional Commits

Follow the [Conventional Commits v1.0.0](https://www.conventionalcommits.org/en/v1.0.0/) specification.

## Format

```
<type>(<scope>): <description>

[optional body]

[optional footer(s)]
```

## Types

| Type     | Usage                                      |
| -------- | ------------------------------------------ |
| `feat`   | A new feature                              |
| `fix`    | A bug fix                                  |
| `refactor` | Code change that neither fixes a bug nor adds a feature |
| `chore`  | Build, CI, dependencies, tooling            |
| `docs`   | Documentation only                          |
| `style`  | Formatting, missing semicolons, etc.        |
| `test`   | Adding or fixing tests                      |
| `perf`   | Performance improvement                     |
| `ci`     | CI/CD configuration and scripts             |

## Scopes

Use lowercase, hyphen-separated scopes relevant to the change area:

- `public-monitor` — map-view, map-controller, public dashboard
- `server` — Spring Boot backend
- `ui` — CCMS_UI AngularJS views/controllers
- `auth` — login, authentication, user management
- `docker` — docker-compose, Dockerfiles, container config
- `db` — database schemas, seeds, migrations
- `ci` — GitHub Actions workflows
- `deploy` — deployment config, nginx, NPM

## Rules

1. **Always use lowercase** for type and scope.
2. **Scope is optional**, but preferred when the change is limited to one area.
3. **Description** is in imperative present tense: "update", "add", "fix", "remove" — NOT "updated", "fixes", "adding".
4. **No period** at the end of the description line.
5. **Breaking changes** append `!` after type/scope: `feat(api)!: remove deprecated endpoint`.
6. **Body** explains *why* and *what*, not *how*. Wrap at 72 characters.
7. **Footer** for `BREAKING CHANGE:` or issue references (`Closes #123`).

## Examples

```
fix(public-monitor): update lock icon to redirect to internal login route
feat(auth): add role-based access control for dashboard pages
refactor(server): extract alert calculation into separate service
chore(docker): upgrade Tomcat base image to 9-jre11
ci: add PR validation workflow for Java tests
```

## References

- [Conventional Commits v1.0.0](https://www.conventionalcommits.org/en/v1.0.0/)
- This project's commit history: `git log --oneline --format="%s"`

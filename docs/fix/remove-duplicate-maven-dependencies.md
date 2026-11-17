# Remove duplicate Maven dependencies

## Problem

The CCMS UI Docker build failed during Maven project validation because `pom.xml` declared the same dependency coordinates more than once:

- `org.springframework:spring-jdbc:${spring.version}`
- `net.sf.supercsv:super-csv:2.1.0`

Maven stops before compilation when dependency coordinates are not unique.

## Change

Removed the duplicate declarations from [`CCMS_UI/STARTUP/ccms_ui/pom.xml`](../../CCMS_UI/STARTUP/ccms_ui/pom.xml). One declaration of each dependency remains with its original version.

No application code, endpoint, communication format, dependency version, or runtime configuration was changed.

## Verification

- Maven POM validation passed after the duplicate declarations were removed.
- `mvn -q test` passed: 93 tests, 0 failures, 0 errors.
- The Docker-equivalent `mvn clean package -DskipTests` passed POM model validation but could not complete in this sandbox because Maven needed to download `maven-clean-plugin:2.5` and the local `.m2` cache is not writable here. The original duplicate-dependency error is resolved.

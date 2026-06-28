#!/bin/sh
# docker-entrypoint.sh
# Convert .env-style environment variables into JVM -D system properties
# so Spring XML ${...} placeholders can resolve them.
set -e

PROPS=""
add_prop() {
    name="$1"
    value="$2"
    if [ -n "$value" ]; then
        PROPS="$PROPS -D$name=$value"
    fi
}

add_prop "ccms.server.host" "$CCMS_SERVER_HOST"
add_prop "mongodb.host"      "$MONGODB_HOST"
add_prop "mongodb.port"      "$MONGODB_PORT"
add_prop "mongodb.database"  "$MONGODB_DATABASE"
add_prop "mongodb.username"  "$MONGODB_USERNAME"
add_prop "mongodb.password"  "$MONGODB_PASSWORD"
add_prop "mysql.host"        "$MYSQL_HOST"
add_prop "mysql.port"        "$MYSQL_PORT"
add_prop "mysql.database"    "$MYSQL_DATABASE"
add_prop "mysql.username"    "$MYSQL_USERNAME"
add_prop "mysql.password"    "$MYSQL_PASSWORD"
add_prop "server.port"       "$SERVER_PORT"
add_prop "backend.http.port"    "$BACKEND_HTTP_PORT"
add_prop "backend.http.context" "$BACKEND_HTTP_CONTEXT"

export CATALINA_OPTS="$CATALINA_OPTS $PROPS"
export JAVA_OPTS="$JAVA_OPTS $PROPS"

exec "$@"

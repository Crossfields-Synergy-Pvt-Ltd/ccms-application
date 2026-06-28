#!/bin/sh
# Render nginx.conf.template to nginx.conf using envsubst, then exec the CMD.
set -e
: "${PUBLIC_DOMAIN:=ccms.example.com}"
: "${LETSENCRYPT_LIVE_DIR:=/etc/letsencrypt/live/ccms.example.com}"

export PUBLIC_DOMAIN LETSENCRYPT_LIVE_DIR

envsubst '${PUBLIC_DOMAIN} ${LETSENCRYPT_LIVE_DIR}' \
    < /etc/nginx/nginx.conf.template \
    > /etc/nginx/nginx.conf

exec "$@"

#!/bin/sh
# Render nginx.conf.template to nginx.conf using envsubst, then exec the CMD.
set -e
: "${PUBLIC_DOMAIN:=ccms.example.com}"

export PUBLIC_DOMAIN

mkdir -p /var/www/html

envsubst '${PUBLIC_DOMAIN}' \
    < /etc/nginx/nginx.conf.template \
    > /etc/nginx/nginx.conf

exec "$@"

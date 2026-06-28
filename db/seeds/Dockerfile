FROM mongo:3.4

# bash for /dev/tcp in seed.sh wait_for(); mysql-client for the MySQL
# schema step.  The mongo shell + mongorestore come from the base image.
#
# The mongo:3.4 image is built on Ubuntu 16.04 with an apt repo for
# MongoDB 3.4 packages whose GPG key has expired.  Remove that repo
# before running apt-get update.
RUN rm -f /etc/apt/sources.list.d/mongodb-org.list \
 && apt-get update \
 && apt-get install -y --no-install-recommends bash mysql-client \
 && rm -rf /var/lib/apt/lists/*

WORKDIR /

COPY seed.sh /seed.sh
RUN chmod +x /seed.sh

# Bake seed data into the image (no bind mount needed for YAML-only paste)
COPY mongo.archive /seeds/mongo.archive
COPY mysql/ /seeds/mysql/

ENTRYPOINT ["/seed.sh"]

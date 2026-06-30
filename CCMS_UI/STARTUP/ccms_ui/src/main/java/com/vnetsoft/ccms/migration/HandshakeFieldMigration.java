package com.vnetsoft.ccms.migration;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import com.mongodb.BasicDBObject;

@Component
public class HandshakeFieldMigration {

    private static final Logger logger = Logger.getLogger(HandshakeFieldMigration.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    @PostConstruct
    public void migrateFields() {
        logger.info("Checking for typo'd field names in MongoDB...");
        migrateHandshakeInfo();
        migrateDcuDetails();
        migrateHerarchyDetails();
        logger.info("Field name migration complete");
    }

    private void migrateHandshakeInfo() {
        renameField("handshake_info", "distict", "district");
        renameField("handshake_info", "mondal", "mandal");
    }

    private void renameField(String collectionName, String oldField, String newField) {
        BasicDBObject query = new BasicDBObject(oldField, new BasicDBObject("$exists", true));
        long count = mongoTemplate.getCollection(collectionName).count(query);
        if (count == 0) return;
        mongoTemplate.getCollection(collectionName).updateMulti(
            query,
            new BasicDBObject("$rename", new BasicDBObject(oldField, newField))
        );
        logger.info("Renamed " + oldField + " -> " + newField + " in " + count + " " + collectionName + " documents");
    }

    private void migrateDcuDetails() {
        setFieldDefault("dcu_details", "distict", "district", "Unknown");
        setFieldDefault("dcu_details", "mondal", "mandal", "Unknown");
    }

    private void setFieldDefault(String collectionName, String oldField, String newField, String defaultValue) {
        BasicDBObject query = new BasicDBObject(oldField, new BasicDBObject("$exists", true));
        long count = mongoTemplate.getCollection(collectionName).count(query);
        if (count == 0) return;
        BasicDBObject update = new BasicDBObject("$unset", new BasicDBObject(oldField, ""))
            .append("$set", new BasicDBObject(newField, defaultValue));
        mongoTemplate.getCollection(collectionName).updateMulti(query, update);
        logger.info("Migrated " + oldField + " -> " + newField + " (" + defaultValue + ") in " + count + " " + collectionName + " documents");
    }

    private void migrateHerarchyDetails() {
        BasicDBObject query = new BasicDBObject("district", "Kadapa");
        long count = mongoTemplate.getCollection("herarchy_details").count(query);
        if (count == 0) return;
        mongoTemplate.getCollection("herarchy_details").updateMulti(
            query,
            new BasicDBObject("$set", new BasicDBObject("district", "YSR Kadapa"))
        );
        logger.info("Updated Kadapa -> YSR Kadapa in " + count + " herarchy_details documents");
    }
}

package com.vetsoft.ccms.netty.migration;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import com.mongodb.BasicDBObject;
import com.vetsoft.ccms.MainBootApp;

@Component
public class HandshakeFieldMigration {

    private static final Logger logger = LoggerFactory.getLogger(HandshakeFieldMigration.class);

    @PostConstruct
    public void migrateFields() {
        MongoTemplate mongoTemplate = MainBootApp.context.getBean("mongoTemplate", MongoTemplate.class);
        logger.info("Checking for typo'd field names in MongoDB...");
        migrateHandshakeInfo(mongoTemplate);
        migrateDcuDetails(mongoTemplate);
        migrateHerarchyDetails(mongoTemplate);
        logger.info("Field name migration complete");
    }

    private void migrateHandshakeInfo(MongoTemplate mongoTemplate) {
        renameField(mongoTemplate, "handshake_info", "distict", "district");
        renameField(mongoTemplate, "handshake_info", "mondal", "mandal");
    }

    private void renameField(MongoTemplate mongoTemplate, String collectionName, String oldField, String newField) {
        BasicDBObject query = new BasicDBObject(oldField, new BasicDBObject("$exists", true));
        long count = mongoTemplate.getCollection(collectionName).count(query);
        if (count == 0) return;
        mongoTemplate.getCollection(collectionName).updateMulti(
            query,
            new BasicDBObject("$rename", new BasicDBObject(oldField, newField))
        );
        logger.info("Renamed " + oldField + " -> " + newField + " in " + count + " " + collectionName + " documents");
    }

    private void migrateDcuDetails(MongoTemplate mongoTemplate) {
        setFieldDefault(mongoTemplate, "dcu_details", "distict", "district", "Unknown");
        setFieldDefault(mongoTemplate, "dcu_details", "mondal", "mandal", "Unknown");
    }

    private void setFieldDefault(MongoTemplate mongoTemplate, String collectionName, String oldField, String newField, String defaultValue) {
        BasicDBObject query = new BasicDBObject(oldField, new BasicDBObject("$exists", true));
        long count = mongoTemplate.getCollection(collectionName).count(query);
        if (count == 0) return;
        BasicDBObject update = new BasicDBObject("$unset", new BasicDBObject(oldField, ""))
            .append("$set", new BasicDBObject(newField, defaultValue));
        mongoTemplate.getCollection(collectionName).updateMulti(query, update);
        logger.info("Migrated " + oldField + " -> " + newField + " (" + defaultValue + ") in " + count + " " + collectionName + " documents");
    }

    private void migrateHerarchyDetails(MongoTemplate mongoTemplate) {
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

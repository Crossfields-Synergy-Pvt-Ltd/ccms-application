package com.vnetsoft.ccms.services;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

/** Clears application data while preserving accounts and saved schedules. */
public class DatabaseClearService {

    private static final Logger logger = Logger.getLogger(DatabaseClearService.class);

    private static final Set<String> PRESERVED_COLLECTIONS = Collections.unmodifiableSet(
            new HashSet<String>(Arrays.asList(
                    "ccms_user_details",
                    "scheduler_details",
                    "schedule_conf_data")));

    @Autowired
    private MongoTemplate mongoTemplate;

    public Set<String> clearApplicationData() {
        Set<String> clearedCollections = new TreeSet<String>();
        for (String collectionName : mongoTemplate.getCollectionNames()) {
            // Mongo-managed collections are not application data and must not be touched.
            if (collectionName.startsWith("system.") || PRESERVED_COLLECTIONS.contains(collectionName)) {
                continue;
            }
            mongoTemplate.remove(new Query(), collectionName);
            clearedCollections.add(collectionName);
        }
        logger.warn("Cleared MongoDB collections: " + clearedCollections);
        return clearedCollections;
    }

    static Set<String> getPreservedCollections() {
        return PRESERVED_COLLECTIONS;
    }
}

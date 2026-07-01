package com.vnetsoft.ccms.migration;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.mongodb.BasicDBObject;

@Component
public class InstallationDateMigration {

    private static final Logger logger = Logger.getLogger(InstallationDateMigration.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    @PostConstruct
    public void migrate() {
        logger.info("Checking installation_date field in handshake_info...");

        try {
            Query query = new Query();
            query.addCriteria(Criteria.where("installation_date").exists(false));
            query.addCriteria(Criteria.where("date").exists(true));
            query.addCriteria(Criteria.where("date").ne(""));
            query.addCriteria(Criteria.where("date").ne(null));

            long pendingCount = mongoTemplate.count(query, "handshake_info");

            if (pendingCount == 0) {
                logger.info("installation_date already present on all documents, skipping migration");
                return;
            }

            logger.info("Found " + pendingCount + " documents missing installation_date, migrating...");

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            int successCount = 0;
            int failCount = 0;

            BasicDBObject findQuery = new BasicDBObject("installation_date", new BasicDBObject("$exists", false));
            findQuery.append("date", new BasicDBObject("$exists", true));

            for (BasicDBObject doc : mongoTemplate.getCollection("handshake_info").find(findQuery)) {
                String dateStr = doc.getString("date");
                if (dateStr == null || dateStr.isEmpty()) continue;

                try {
                    Date parsedDate = sdf.parse(dateStr);
                    BasicDBObject docQuery = new BasicDBObject("_id", doc.getString("_id"));
                    BasicDBObject update = new BasicDBObject("$set", new BasicDBObject("installation_date", parsedDate));
                    mongoTemplate.getCollection("handshake_info").updateOne(docQuery, update);
                    successCount++;
                } catch (Exception e) {
                    logger.warn("Failed to parse date: '" + dateStr + "' for document " + doc.getString("_id"));
                    failCount++;
                }
            }

            logger.info("Installation date migration complete: " + successCount + " migrated, " + failCount + " failed");
        } catch (Exception e) {
            logger.error("Installation date migration failed: " + e.getMessage(), e);
        }
    }
}

package com.vnetsoft.ccms.migration;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

@Component
public class InstallationDateMigration {

    private static final Logger logger = Logger.getLogger(InstallationDateMigration.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    @PostConstruct
    public void migrate() {
        logger.info("Checking installation_date field in handshake_info...");

        try {
            Query checkQuery = new Query();
            checkQuery.addCriteria(Criteria.where("installation_date").exists(false));
            checkQuery.addCriteria(Criteria.where("date").exists(true).ne("").ne(null));

            long pendingCount = mongoTemplate.count(checkQuery, "handshake_info");

            if (pendingCount == 0) {
                logger.info("installation_date already present on all documents, skipping migration");
                return;
            }

            logger.info("Found " + pendingCount + " documents missing installation_date, migrating...");

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            int successCount = 0;
            int failCount = 0;

            Query fetchQuery = new Query();
            fetchQuery.addCriteria(Criteria.where("installation_date").exists(false));
            fetchQuery.addCriteria(Criteria.where("date").exists(true).ne("").ne(null));
            fetchQuery.fields().include("_id").include("date");

            List<com.vnetsoft.ccms.pojo.HandShake> docs = mongoTemplate.find(fetchQuery, com.vnetsoft.ccms.pojo.HandShake.class, "handshake_info");

            for (com.vnetsoft.ccms.pojo.HandShake doc : docs) {
                String dateStr = doc.getDate();
                if (dateStr == null || dateStr.isEmpty()) continue;

                try {
                    Date parsedDate = sdf.parse(dateStr);
                    Query docQuery = new Query(Criteria.where("_id").is(doc.getId()));
                    Update update = new Update().set("installation_date", parsedDate);
                    mongoTemplate.updateFirst(docQuery, update, "handshake_info");
                    successCount++;
                } catch (Exception e) {
                    logger.warn("Failed to parse date: '" + dateStr + "' for document " + doc.getId());
                    failCount++;
                }
            }

            logger.info("Installation date migration complete: " + successCount + " migrated, " + failCount + " failed");
        } catch (Exception e) {
            logger.error("Installation date migration failed: " + e.getMessage(), e);
        }
    }
}

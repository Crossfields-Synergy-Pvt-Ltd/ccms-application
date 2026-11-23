package com.vetsoft.ccms.netty.pojo;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;

public class PanelIdTypeTest {

    @Test
    public void handshakePreservesAlphanumericPanelId() {
        HandShake handshake = new HandShake();

        handshake.setPanel_unique_id("CSA9388");

        assertEquals("CSA9388", handshake.getPanel_unique_id());
    }

    @Test
    public void dcuPreservesNumericPanelIdAsText() {
        DCU dcu = new DCU();

        dcu.setPanel_unique_id("9388");

        assertEquals("9388", dcu.getPanel_unique_id());
    }

    @Test
    public void mongoNumericLegacyPanelIdIsReadableAsText() throws Exception {
        MongoMappingContext context = new MongoMappingContext();
        context.afterPropertiesSet();
        MappingMongoConverter converter = new MappingMongoConverter(
                new SimpleMongoDbFactory(new MongoClient(), "test"), context);

        BasicDBObject document = new BasicDBObject("_id", "1903SO1P1C014877")
                .append("panel_unique_id", 9388);

        HandShake handshake = converter.read(HandShake.class, document);

        assertEquals("9388", handshake.getPanel_unique_id());
    }
}

package com.vetsoft.ccms.netty.repos;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.vetsoft.ccms.netty.pojo.HandShake;

public class DeviceRequestDataRepositoryTest {

    private MongoTemplate mongoTemplate;
    private DeviceRequestDataRepository repository;

    @Before
    public void setUp() throws Exception {
        mongoTemplate = mock(MongoTemplate.class);
        repository = new DeviceRequestDataRepository();

        java.lang.reflect.Field field = DeviceRequestDataRepository.class
                .getDeclaredField("mongoTemplate");
        field.setAccessible(true);
        field.set(repository, mongoTemplate);
    }

    @Test
    public void handshakeUpdateDoesNotOverwriteUiOwnedPanelId() {
        HandShake handshake = new HandShake();
        handshake.setId("1903SO1P1C014877");
        handshake.setGateway_serial_number("1903SO1P1C014877");
        handshake.setPanel_unique_id("CSA9388");
        when(mongoTemplate.findOne(any(Query.class), eq(HandShake.class)))
                .thenReturn(handshake);

        HandShake result = repository.addOrUpdateHandShake(handshake);

        assertSame(handshake, result);
        ArgumentCaptor<Update> updateCaptor = ArgumentCaptor.forClass(Update.class);
        verify(mongoTemplate).upsert(any(Query.class), updateCaptor.capture(), eq(HandShake.class));
        assertFalse(updateCaptor.getValue().getUpdateObject().containsField("panel_unique_id"));
    }
}

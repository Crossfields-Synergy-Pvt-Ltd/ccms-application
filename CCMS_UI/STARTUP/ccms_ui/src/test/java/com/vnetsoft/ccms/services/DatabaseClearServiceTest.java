package com.vnetsoft.ccms.services;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DatabaseClearServiceTest {

    @Mock
    private MongoTemplate mongoTemplate;

    private DatabaseClearService service;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        service = new DatabaseClearService();
        java.lang.reflect.Field field = DatabaseClearService.class.getDeclaredField("mongoTemplate");
        field.setAccessible(true);
        field.set(service, mongoTemplate);
    }

    @Test
    public void clearsEveryApplicationCollectionExceptUsersAndSchedules() {
        when(mongoTemplate.getCollectionNames()).thenReturn(new HashSet<String>(Arrays.asList(
                "ccms_user_details", "scheduler_details", "schedule_conf_data",
                "dcu_details", "handshake_info", "system.indexes")));

        Set<String> cleared = service.clearApplicationData();

        assertEquals(new HashSet<String>(Arrays.asList("dcu_details", "handshake_info")), cleared);
        verify(mongoTemplate).remove(any(Query.class), org.mockito.Matchers.eq("dcu_details"));
        verify(mongoTemplate).remove(any(Query.class), org.mockito.Matchers.eq("handshake_info"));
        verify(mongoTemplate, never()).remove(any(Query.class), org.mockito.Matchers.eq("ccms_user_details"));
        verify(mongoTemplate, never()).remove(any(Query.class), org.mockito.Matchers.eq("scheduler_details"));
        verify(mongoTemplate, never()).remove(any(Query.class), org.mockito.Matchers.eq("schedule_conf_data"));
        verify(mongoTemplate, never()).remove(any(Query.class), org.mockito.Matchers.eq("system.indexes"));
    }
}

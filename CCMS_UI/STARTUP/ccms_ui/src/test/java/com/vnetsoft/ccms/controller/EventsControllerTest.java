package com.vnetsoft.ccms.controller;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.vnetsoft.ccms.services.DCUServices;
import com.vnetsoft.ccms.pojo.HandShake;

public class EventsControllerTest extends AbstractControllerTest {

    @Mock
    private DCUServices userServices;

    @InjectMocks
    private EventsController controller;

    @Before
    @Override
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        configureController(controller);
    }

    @Test
    public void testGetAllEventsCounts_ReturnsCounts() throws Exception {
        performGet("/events/event_counts").andExpect(status().isOk());
    }

    @Test
    public void testGetEventsBetweenDates_ValidParams_ReturnsEvents() throws Exception {
        when(userServices.getHandShakeByID("DCU001")).thenReturn(new HandShake());
        performGet("/events/events_between_date?id=DCU001&start_date=01/01/2024&end_date=02/01/2024")
            .andExpect(status().isOk());
    }

    @Test
    public void testGetEventsBetweenDates_UnknownDcu_ReturnsNotFound() throws Exception {
        performGet("/events/events_between_date?id=NONEXISTENT&start_date=01/01/2020&end_date=02/01/2020")
            .andExpect(status().isNotFound());
    }
    @Test
    public void testGetEventsBetweenDates_EndBeforeStart_ReturnsEmpty() throws Exception {
        when(userServices.getHandShakeByID("DCU001")).thenReturn(new HandShake());
        performGet("/events/events_between_date?id=DCU001&start_date=31/12/2024&end_date=01/01/2024")
            .andExpect(status().isOk());
    }

    @Test
    public void testGetEventsBetweenDates_InvalidId_ReturnsBadRequest() throws Exception {
        performGet("/events/events_between_date?id=../etc&start_date=01/01/2024&end_date=02/01/2024")
            .andExpect(status().isBadRequest());
    }


    @Test
    public void testGetEventsBetweenDates_MissingId_Returns4xx() throws Exception {
        performGet("/events/events_between_date?start_date=01/01/2024&end_date=02/01/2024")
            .andExpect(status().is4xxClientError());
    }

    @Test
    public void testExportEvents_EmptyResult_ReturnsNoContent() throws Exception {
        when(userServices.getHandShakeByID("DCU001")).thenReturn(new HandShake());
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                .get("/events/export_events?id=DCU001&start_date=01/01/2020&end_date=02/01/2020"))
            .andExpect(status().isNoContent());
    }

    @Test
    public void testExportEvents_MissingDates_Returns4xx() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                .get("/events/export_events?id=DCU001"))
            .andExpect(status().is4xxClientError());
    }
}

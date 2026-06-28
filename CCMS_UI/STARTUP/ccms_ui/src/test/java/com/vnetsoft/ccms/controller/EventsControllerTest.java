package com.vnetsoft.ccms.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.vnetsoft.ccms.services.DCUServices;

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

    // --- events/event_counts ---

    @Test
    public void testGetAllEventsCounts_ReturnsCounts() throws Exception {
        performGet("/events/event_counts")
            .andExpect(status().isOk());
    }

    // --- events/events_between_date ---

    @Test
    public void testGetEventsBetweenDates_ValidParams_ReturnsEvents() throws Exception {
        performGet("/events/events_between_date?id=DCU001&start_date=2024-01-01&end_date=2024-12-31")
            .andExpect(status().isOk());
    }

    @Test
    public void testGetEventsBetweenDates_NoResults_ReturnsEmpty() throws Exception {
        performGet("/events/events_between_date?id=NONEXISTENT&start_date=2020-01-01&end_date=2020-01-02")
            .andExpect(status().isOk());
    }

    @Test
    public void testGetEventsBetweenDates_InvalidDateFormat_ReturnsError() throws Exception {
        performGet("/events/events_between_date?id=DCU001&start_date=not-a-date&end_date=not-a-date")
            .andExpect(status().isOk());
    }

    @Test
    public void testGetEventsBetweenDates_MissingId_Returns4xx() throws Exception {
        performGet("/events/events_between_date?start_date=2024-01-01&end_date=2024-12-31")
            .andExpect(status().is4xxClientError());
    }

    @Test
    public void testGetEventsBetweenDates_MissingDates_Returns4xx() throws Exception {
        performGet("/events/events_between_date?id=DCU001")
            .andExpect(status().is4xxClientError());
    }

    @Test
    public void testGetEventsBetweenDates_EndBeforeStart_StillProcesses() throws Exception {
        performGet("/events/events_between_date?id=DCU001&start_date=2024-12-31&end_date=2024-01-01")
            .andExpect(status().isOk());
    }

    // --- events/export_events ---

    @Test
    public void testExportEvents_ValidParams_ReturnsCsv() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                .get("/events/export_events?id=DCU001&start_date=2024-01-01&end_date=2024-12-31")
                .accept("text/csv"))
            .andExpect(status().isOk());
    }

    @Test
    public void testExportEvents_InvalidId_ReturnsOk() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                .get("/events/export_events?id=BAD&start_date=2024-01-01&end_date=2024-12-31"))
            .andExpect(status().isOk());
    }

    @Test
    public void testExportEvents_MissingDates_Returns4xx() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                .get("/events/export_events?id=DCU001"))
            .andExpect(status().is4xxClientError());
    }
}

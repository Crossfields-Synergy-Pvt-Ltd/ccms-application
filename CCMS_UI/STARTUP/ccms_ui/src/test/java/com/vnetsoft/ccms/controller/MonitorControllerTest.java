package com.vnetsoft.ccms.controller;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.vnetsoft.ccms.pojo.HandShake;
import com.vnetsoft.ccms.pojo.server.InstantMeterData;
import com.vnetsoft.ccms.pojo.MonitorControlCount;
import com.vnetsoft.ccms.pojo.DCUInstantData;
import com.vnetsoft.ccms.pojo.ui.MapData;
import com.vnetsoft.ccms.services.DashBoardServices;

public class MonitorControllerTest extends AbstractControllerTest {

    @Mock
    private DashBoardServices dashBpardService;

    @InjectMocks
    private MonitorController controller;

    @Before
    @Override
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        configureController(controller);
    }

    // --- dashboard/count ---

    @Test
    public void testGetDashBoardCounts_ReturnsCounts() throws Exception {
        MonitorControlCount counts = new MonitorControlCount();
        counts.total_devices = 100;
        counts.total_lights_connected = 500;
        counts.mcb_trip_count = 5;
        counts.light_on = 400;
        counts.light_off = 100;
        counts.online_ccms = 80;
        counts.offline_ccms = 20;

        when(dashBpardService.getDahsBoardCountstats("ALL", "ALL", "ALL", null, null)).thenReturn(counts);

        performGet("/dashboard/count?district=ALL&mandal=ALL&gp=ALL")
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.total_devices", is(100)))
            .andExpect(jsonPath("$.total_lights_connected", is(500)))
            .andExpect(jsonPath("$.mcb_trip_count", is(5)))
            .andExpect(jsonPath("$.light_on", is(400)))
            .andExpect(jsonPath("$.online_ccms", is(80)))
            .andExpect(jsonPath("$.offline_ccms", is(20)));
    }

    @Test
    public void testGetDashBoardCounts_EmptyFilter_ReturnsNull() throws Exception {
        when(dashBpardService.getDahsBoardCountstats("", "", "", null, null)).thenReturn(null);

        performGet("/dashboard/count?district=&mandal=&gp=")
            .andExpect(status().isOk());
    }

    @Test
    public void testGetDashBoardCounts_ServiceThrows_ReturnsNull() throws Exception {
        when(dashBpardService.getDahsBoardCountstats("ALL", "ALL", "ALL", null, null))
            .thenThrow(new RuntimeException("DB Error"));

        performGet("/dashboard/count?district=ALL&mandal=ALL&gp=ALL")
            .andExpect(status().isOk());
    }

    @Test
    public void testGetDashBoardCounts_AllCountsZero() throws Exception {
        MonitorControlCount counts = new MonitorControlCount();

        when(dashBpardService.getDahsBoardCountstats("ALL", "ALL", "ALL", null, null)).thenReturn(counts);

        performGet("/dashboard/count?district=ALL&mandal=ALL&gp=ALL")
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.total_devices", is(0)));
    }

    // --- dashboard/map_data ---

    @Test
    public void testGetDashBoardMapData_ReturnsMapDataList() throws Exception {
        MapData md1 = new MapData();
        md1.lat = "16.4792";
        md1.lang = "80.5469";
        md1.light_status = 1;
        md1.info_details = "DCU-001";

        MapData md2 = new MapData();
        md2.lat = "16.5000";
        md2.lang = "80.6000";
        md2.light_status = 0;
        md2.info_details = "DCU-002";

        HandShake hs1 = new HandShake();
        hs1.setLat("16.4792");
        hs1.setLang("80.5469");
        hs1.setLight_status(1);

        HandShake hs2 = new HandShake();
        hs2.setLat("16.5000");
        hs2.setLang("80.6000");
        hs2.setLight_status(0);

        when(dashBpardService.getMapData("ALL", "ALL", "ALL", null, null))
            .thenReturn(Arrays.asList(hs1, hs2));

        performGet("/dashboard/map_data?district=ALL&mandal=ALL&gp=ALL")
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", not(empty())));
    }

    @Test
    public void testGetDashBoardMapData_EmptyList_ReturnsEmptyArray() throws Exception {
        when(dashBpardService.getMapData("ALL", "ALL", "ALL", null, null))
            .thenReturn(Collections.emptyList());

        performGet("/dashboard/map_data?district=ALL&mandal=ALL&gp=ALL")
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void testGetDashBoardMapData_ServiceThrows_ReturnsEmptyArray() throws Exception {
        when(dashBpardService.getMapData("ALL", "ALL", "ALL", null, null))
            .thenThrow(new RuntimeException("DB Error"));

        performGet("/dashboard/map_data?district=ALL&mandal=ALL&gp=ALL")
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    // --- dashboard/instant_data_id/{id} ---

    @Test
    public void testGetInstantDataById_ValidId_ReturnsData() throws Exception {
        HandShake hs = new HandShake();
        hs.setId("DCU001");
        hs.setGateway_serial_number("1905HY1P1C009534");
        hs.setLight_status(1);
        hs.setLat("16.4792");
        hs.setLang("80.5469");
        hs.setHs_time_stamp(String.valueOf(System.currentTimeMillis() / 1000));

        InstantMeterData meterData = new InstantMeterData();
        meterData.setId("METER001");

        when(dashBpardService.getHandShakeByID("DCU001")).thenReturn(hs);
        when(dashBpardService.getInstantMeterData("1905HY1P1C009534")).thenReturn(meterData);

        performGet("/dashboard/instant_data_id/DCU001")
            .andExpect(status().isOk());
    }

    @Test
    public void testGetInstantDataById_NonexistentId_ReturnsNull() throws Exception {
        when(dashBpardService.getHandShakeByID("NONEXISTENT")).thenReturn(null);

        performGet("/dashboard/instant_data_id/NONEXISTENT")
            .andExpect(status().isOk());
    }

    @Test
    public void testGetInstantDataById_EmptyId() throws Exception {
        performGet("/dashboard/instant_data_id/")
            .andExpect(status().is4xxClientError());
    }

    // --- dashboard/instant_data_filter ---

    @Test
    public void testGetInstantDataFilter_ValidParams() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                .post("/dashboard/instant_data_filter?district=ALL&mandal=ALL&gp=ALL&page=1&size=10")
                .header("Authorization", "dGVzdDp0ZXN0"))
            .andExpect(status().isOk());
    }

    @Test
    public void testGetInstantDataFilter_MissingPage_Defaults() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                .post("/dashboard/instant_data_filter?district=ALL&mandal=ALL&gp=ALL")
                .header("Authorization", "dGVzdDp0ZXN0"))
            .andExpect(status().isOk());
    }
}

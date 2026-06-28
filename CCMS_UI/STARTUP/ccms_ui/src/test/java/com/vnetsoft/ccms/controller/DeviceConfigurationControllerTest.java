package com.vnetsoft.ccms.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import com.vnetsoft.ccms.pojo.DCUConfiguration;
import com.vnetsoft.ccms.pojo.HandShake;
import com.vnetsoft.ccms.pojo.Status;
import com.vnetsoft.ccms.services.DCUServices;
import com.vnetsoft.ccms.services.NodeServices;

public class DeviceConfigurationControllerTest extends AbstractControllerTest {

    @Mock
    private DCUServices userServices;

    @Mock
    private NodeServices nodeServices;

    @InjectMocks
    private DeviceConfigurationController controller;

    @Before
    @Override
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(controller, "serverHost", "localhost");
        configureController(controller);
    }

    // --- lights_on ---

    @Test
    public void testTurnOnLights_ValidParams_ReturnsStatus200() throws Exception {
        performGet("/device_conf/lights_on?device_serial_number=1905HY1P1C009534&device_identifier=2043")
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(200)))
            .andExpect(jsonPath("$.message", is("success")));
    }

    @Test
    public void testTurnOnLights_EmptySerial_ReturnsStatus200() throws Exception {
        performGet("/device_conf/lights_on?device_serial_number=&device_identifier=2043")
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(200)));
    }

    @Test
    public void testTurnOnLights_EmptyIdentifier_ReturnsStatus200() throws Exception {
        performGet("/device_conf/lights_on?device_serial_number=TEST1234&device_identifier=")
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(200)));
    }

    // --- lights_off ---

    @Test
    public void testTurnOffLights_ValidParams_ReturnsStatus200() throws Exception {
        performGet("/device_conf/lights_off?device_serial_number=1905HY1P1C009534&device_identifier=2043")
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(200)))
            .andExpect(jsonPath("$.message", is("success")));
    }

    @Test
    public void testTurnOffLights_EmptyParams_ReturnsStatus200() throws Exception {
        performGet("/device_conf/lights_off?device_serial_number=&device_identifier=")
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(200)));
    }

    // --- sync_dcu_configuration ---

    @Test
    public void testSyncDcuConfig_ValidConfig_ReturnsStatus200() throws Exception {
        DCUConfiguration config = new DCUConfiguration();
        config.setDcu_id("DCU001");
        config.setApn_name("test.apn");

        HandShake mockHs = new HandShake();
        mockHs.setGateway_identifier(2043);
        mockHs.setGateway_serial_number("1905HY1P1C009534");

        when(userServices.getHandShakeByID("DCU001")).thenReturn(mockHs);
        when(userServices.addDCUConfiguration(any(DCUConfiguration.class))).thenReturn(true);

        performPost("/device_conf/sync_dcu_configuration", toJson(config))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(200)))
            .andExpect(jsonPath("$.message", is("Success")));
    }

    @Test
    public void testSyncDcuConfig_HandShakeNotFound_ReturnsError() throws Exception {
        DCUConfiguration config = new DCUConfiguration();
        config.setDcu_id("NONEXISTENT");

        when(userServices.getHandShakeByID("NONEXISTENT")).thenThrow(new RuntimeException("Not found"));

        performPost("/device_conf/sync_dcu_configuration", toJson(config))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)));
    }

    @Test
    public void testSyncDcuConfig_EmptyBody_ReturnsError() throws Exception {
        performPost("/device_conf/sync_dcu_configuration", "")
            .andExpect(status().is4xxClientError());
    }

    // --- sync_node_conf ---

    @Test
    public void testSyncNodeConf_ValidId_ReturnsStatus200() throws Exception {
        HandShake mockHs = new HandShake();
        mockHs.setGateway_identifier(2043);

        when(userServices.getHandShakeByID("DCU001")).thenReturn(mockHs);
        when(nodeServices.getEntityList()).thenReturn(java.util.Collections.emptyList());

        performGet("/device_conf/sync_node_conf/DCU001")
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(200)));
    }

    @Test
    public void testSyncNodeConf_NonexistentId_ReturnsStatus200() throws Exception {
        when(userServices.getHandShakeByID("BAD-ID")).thenThrow(new RuntimeException("Not found"));

        performGet("/device_conf/sync_node_conf/BAD-ID")
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(200)));
    }

    // --- sync_schduler_conf ---

    @Test
    public void testSyncSchedulerConf_ValidId_ReturnsStatus200() throws Exception {
        HandShake mockHs = new HandShake();
        mockHs.setGateway_identifier(2043);

        when(userServices.getHandShakeByID("DCU001")).thenReturn(mockHs);

        performGet("/device_conf/sync_schduler_conf?id=DCU001")
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(200)));
    }

    @Test
    public void testSyncSchedulerConf_NonexistentId_ReturnsStatus200() throws Exception {
        when(userServices.getHandShakeByID("BAD-ID")).thenThrow(new RuntimeException("Not found"));

        performGet("/device_conf/sync_schduler_conf?id=BAD-ID")
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(200)));
    }
}

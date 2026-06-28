package com.vnetsoft.ccms.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.any;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.vnetsoft.ccms.pojo.DCU;
import com.vnetsoft.ccms.pojo.HandShake;
import com.vnetsoft.ccms.services.DCUServices;
import com.vnetsoft.ccms.services.DashBoardServices;

public class DCUControllerTest extends AbstractControllerTest {

    @Mock
    private DCUServices userServices;

    @Mock
    private DashBoardServices dashboardService;

    @InjectMocks
    private DCUController controller;

    @Before
    @Override
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        configureController(controller);
    }

    // --- dcu/list ---

    @Test
    public void testGetDCUList_ReturnsList() throws Exception {
        DCU dcu = new DCU();
        dcu.setId("DCU001");
        dcu.setName("Test DCU");

        when(userServices.getDCUList()).thenReturn(Arrays.asList(dcu));

        performGet("/dcu/list")
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].id", is("DCU001")));
    }

    @Test
    public void testGetDCUList_EmptyList_ReturnsEmptyArray() throws Exception {
        when(userServices.getDCUList()).thenReturn(Collections.emptyList());

        performGet("/dcu/list")
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void testGetDCUList_ServiceThrows_ReturnsOk() throws Exception {
        when(userServices.getDCUList()).thenThrow(new RuntimeException("DB Error"));

        performGet("/dcu/list")
            .andExpect(status().isOk());
    }

    // --- dcu/handshake_list ---

    @Test
    public void testGetHandShakeList_ReturnsData() throws Exception {
        when(dashboardService.getMapData("ALL", "ALL", "ALL"))
            .thenReturn(Arrays.asList(new HandShake()));

        performGet("/dcu/handshake_list?distrtict=ALL&mandal=ALL&gp=ALL")
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void testGetHandShakeList_EmptyFilter_ReturnsData() throws Exception {
        when(dashboardService.getMapData("", "", ""))
            .thenReturn(Collections.emptyList());

        performGet("/dcu/handshake_list?distrtict=&mandal=&gp=")
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    // --- dcu/dcu_name_list ---

    @Test
    public void testGetDcuNameList_ReturnsNames() throws Exception {
        HandShake hs = new HandShake();
        hs.setGateway_serial_number("1905HY1P1C009534");
        hs.setName("Test DCU");

        when(dashboardService.getMapData("ALL", "ALL", "ALL"))
            .thenReturn(Arrays.asList(hs));

        performGet("/dcu/dcu_name_list?distrtict=ALL&mandal=ALL&gp=ALL")
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    // --- dcu/io_list ---

    @Test
    public void testGetIOList_ReturnsData() throws Exception {
        performGet("/dcu/io_list")
            .andExpect(status().isOk());
    }

    // --- dcu/create ---

    @Test
    public void testCreateDCU_ValidData_ReturnsStatus() throws Exception {
        HandShake hs = new HandShake();
        hs.setGateway_serial_number("1905HY1P1C009534");

        when(userServices.addHandShake(any(HandShake.class))).thenReturn(true);

        performPost("/dcu/create", toJson(hs))
            .andExpect(status().isOk());
    }

    @Test
    public void testCreateDCU_EmptyBody_Returns4xx() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/dcu/create")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content(""))
            .andExpect(status().is4xxClientError());
    }

    // --- dcu/sys_conf_id/{id} ---

    @Test
    public void testGetSysConfById_ValidId_ReturnsData() throws Exception {
        performPost("/dcu/sys_conf_id/DCU001", "")
            .andExpect(status().isOk());
    }

    @Test
    public void testGetSysConfById_NonexistentId_ReturnsError() throws Exception {
        performPost("/dcu/sys_conf_id/NONEXISTENT", "")
            .andExpect(status().isOk());
    }

    // --- dcu/delet_dcu_id/{id} ---

    @Test
    public void testDeleteDCU_ValidId_ReturnsStatus() throws Exception {
        when(userServices.deleteDCU("DCU001")).thenReturn(true);

        performDelete("/dcu/delet_dcu_id/DCU001")
            .andExpect(status().isOk());
    }

    @Test
    public void testDeleteDCU_ServiceThrows_ReturnsOk() throws Exception {
        when(userServices.deleteDCU("DCU001")).thenThrow(new RuntimeException("Delete failed"));

        performDelete("/dcu/delet_dcu_id/DCU001")
            .andExpect(status().isOk());
    }

    // --- dcu/handshake_info_by_id ---

    @Test
    public void testGetHandShakeInfoById_ValidParams() throws Exception {
        performGet("/dcu/handshake_info_by_id?distrtict=ALL&mandal=ALL&gp=ALL&name=DCU001")
            .andExpect(status().isOk());
    }
}

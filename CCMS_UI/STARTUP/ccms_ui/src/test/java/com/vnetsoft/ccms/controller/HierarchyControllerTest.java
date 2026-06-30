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

import com.vnetsoft.ccms.pojo.HierarchyDetails;
import com.vnetsoft.ccms.services.DCUServices;

public class HierarchyControllerTest extends AbstractControllerTest {

    @Mock
    private DCUServices userServices;

    @InjectMocks
    private HierarchyController hierarchyController;

    @Before
    @Override
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        configureController(hierarchyController);
    }

    // --- filter/list ---

    @Test
    public void testGetFilterList_ReturnsList() throws Exception {
        HierarchyDetails hd = new HierarchyDetails();
        hd.setDistrict("Guntur");
        hd.setMandal("Tenali");

        when(userServices.getHierarchyDetailsList()).thenReturn(Arrays.asList(hd));

        performGet("/filter/list")
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].district", is("Guntur")));
    }

    @Test
    public void testGetFilterList_Empty_ReturnsEmptyArray() throws Exception {
        when(userServices.getHierarchyDetailsList()).thenReturn(Collections.emptyList());

        performGet("/filter/list")
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    // --- filter/get_mandal ---

    @Test
    public void testGetMandal_ValidDistrict_ReturnsList() throws Exception {
        when(userServices.getMondalByDistrict("Guntur")).thenReturn(Arrays.asList("Tenali", "Guntur Rural"));

        performGet("/filter/get_mandal?district=Guntur")
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void testGetMandal_EmptyDistrict_ReturnsEmptyList() throws Exception {
        when(userServices.getMondalByDistrict("")).thenReturn(Collections.emptyList());

        performGet("/filter/get_mandal?district=")
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void testGetMandal_NonexistentDistrict_ReturnsEmpty() throws Exception {
        when(userServices.getMondalByDistrict("NONEXISTENT")).thenReturn(Collections.emptyList());

        performGet("/filter/get_mandal?district=NONEXISTENT")
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void testGetMandal_MissingParam_Returns4xx() throws Exception {
        performGet("/filter/get_mandal")
            .andExpect(status().is4xxClientError());
    }

    // --- filter/get_gp ---

    @Test
    public void testGetGp_ValidMandal_ReturnsList() throws Exception {
        when(userServices.getGPByMandal("Tenali")).thenReturn(Arrays.asList("GP1", "GP2"));

        performGet("/filter/get_gp?mandal=Tenali")
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void testGetGp_EmptyMandal_ReturnsEmptyList() throws Exception {
        when(userServices.getGPByMandal("")).thenReturn(Collections.emptyList());

        performGet("/filter/get_gp?mandal=")
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void testGetGp_MissingParam_Returns4xx() throws Exception {
        performGet("/filter/get_gp")
            .andExpect(status().is4xxClientError());
    }

    // --- filter/get_vilage ---

    @Test
    public void testGetVilage_ValidGp_ReturnsList() throws Exception {
        when(userServices.getVilageByGP("GP1")).thenReturn(Arrays.asList("Village1"));

        performGet("/filter/get_vilage?gp=GP1")
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void testGetVilage_EmptyGp_ReturnsEmpty() throws Exception {
        when(userServices.getVilageByGP("")).thenReturn(Collections.emptyList());

        performGet("/filter/get_vilage?gp=")
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    // --- filter/create ---

    @Test
    public void testCreateFilter_ValidData_ReturnsStatus() throws Exception {
        HierarchyDetails hd = new HierarchyDetails();
        hd.setDistrict("Guntur");
        hd.setMandal("Tenali");
        hd.setGp("GP1");

        when(userServices.addHierarchy(any(HierarchyDetails.class))).thenReturn(true);

        performPost("/filter/create", toJson(hd))
            .andExpect(status().isOk());
    }

    @Test
    public void testCreateFilter_EmptyBody_Returns4xx() throws Exception {
        performPost("/filter/create", "")
            .andExpect(status().is4xxClientError());
    }

    // --- filter/generate_db ---

    @Test
    public void testGenerateDb_ReturnsStatus() throws Exception {
        performGet("/filter/generate_db")
            .andExpect(status().isOk());
    }
}

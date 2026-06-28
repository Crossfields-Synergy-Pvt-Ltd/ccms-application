package com.vetsoft.ccms.controller;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.vetsoft.ccms.dal.UserDAL;
import com.vetsoft.ccms.dal.UserRepository;
import com.vetsoft.ccms.netty.server.ServerHandler;

public class UIConfControllerTest {

    private UserRepository userRepository;
    private UserDAL userDAL;
    private ServerHandler serverHandler;
    private UIConfController controller;

    @Before
    public void setUp() {
        userRepository = mock(UserRepository.class);
        userDAL = mock(UserDAL.class);
        serverHandler = mock(ServerHandler.class);
        controller = new UIConfController(userRepository, userDAL);
        controller.serverHandler = serverHandler;
    }

    @After
    public void tearDown() {
        reset(serverHandler);
    }

    // --- manuvalOn ---

    @Test
    public void testManuvalOn_ValidParams_ReturnsDONE() {
        String result = controller.manuvalOn("1905HY1P1C009534", 2043);
        assertEquals("DONE", result);
        verify(serverHandler, times(1)).sendManuvalOnOffRequest(anyString(), eq("1905HY1P1C009534"));
    }

    @Test
    public void testManuvalOn_WithZeroIdentifier_ReturnsDONE() {
        String result = controller.manuvalOn("TEST1234", 0);
        assertEquals("DONE", result);
        verify(serverHandler, times(1)).sendManuvalOnOffRequest(anyString(), eq("TEST1234"));
    }

    @Test
    public void testManuvalOn_WithEmptySerial_ReturnsDONE() {
        String result = controller.manuvalOn("", 2043);
        assertEquals("DONE", result);
        verify(serverHandler, times(1)).sendManuvalOnOffRequest(anyString(), eq(""));
    }

    @Test
    public void testManuvalOn_CallsServerHandler() {
        controller.manuvalOn("1905HY1P1C009534", 2043);
        verify(serverHandler).sendManuvalOnOffRequest(anyString(), eq("1905HY1P1C009534"));
    }

    // --- manuvalOff ---

    @Test
    public void testManuvalOff_ValidParams_ReturnsDONE() {
        String result = controller.manuvalOff("1905HY1P1C009534", 2043);
        assertEquals("DONE", result);
        verify(serverHandler, times(1)).sendManuvalOnOffRequest(anyString(), eq("1905HY1P1C009534"));
    }

    @Test
    public void testManuvalOff_WithZeroIdentifier_ReturnsDONE() {
        String result = controller.manuvalOff("TEST1234", 0);
        assertEquals("DONE", result);
    }

    @Test
    public void testManuvalOff_WithEmptySerial_ReturnsDONE() {
        String result = controller.manuvalOff("", 2043);
        assertEquals("DONE", result);
    }

    @Test
    public void testManuvalOff_CallsServerHandler() {
        controller.manuvalOff("1905HY1P1C009534", 2043);
        verify(serverHandler).sendManuvalOnOffRequest(anyString(), eq("1905HY1P1C009534"));
    }

    @Test
    public void testManuvalOnOff_ProduceDifferentCalls() {
        controller.manuvalOn("DCU001", 100);
        controller.manuvalOff("DCU001", 100);
        verify(serverHandler, times(2)).sendManuvalOnOffRequest(anyString(), eq("DCU001"));
    }

    // --- syncSysConf ---

    @Test
    public void testSyncSysConf_ValidParams_ReturnsOK() {
        String result = controller.syncSysConf("DCU001", "55AA55AA0100000000013E0200060101A1B2C3D4",
                "2043", "node_data_here", "FILE123");
        assertEquals("OK", result);
        verify(serverHandler, times(1)).sendDcuConfSyncMesageToClient(
                anyString(), eq("DCU001"), eq("FILE123"), eq("node_data_here"), eq("2043"));
    }

    @Test
    public void testSyncSysConf_WithEmptyBuffer_DoesNotThrow() {
        String result = controller.syncSysConf("DCU001", "", "2043", "", "FILE123");
        assertEquals("OK", result);
    }

    @Test
    public void testSyncSysConf_WithNullBuffer_DoesNotThrow() {
        String result = controller.syncSysConf("DCU001", null, "2043", null, "FILE123");
        assertEquals("OK", result);
    }

    // --- syncNodeConf ---

    @Test
    public void testSyncNodeConfFromUI_ValidParams_ReturnsOK() {
        String result = controller.syncNodeConfFromUI("DCU001", "2043", "node_data", "A1B2C3D4");
        assertEquals("OK", result);
        verify(serverHandler, times(1)).sendNodeConfigurationChangeRequestToClient(
                anyString(), eq("DCU001"), eq("A1B2C3D4"), eq("node_data"), eq("2043"));
    }

    @Test
    public void testSyncNodeConfFromUI_WithEmptyParams_DoesNotThrow() {
        String result = controller.syncNodeConfFromUI("", "", "", "");
        assertEquals("OK", result);
    }

    // --- syncSchedulerConf ---

    @Test
    public void testSyncSchedulerConfFromUI_ValidParams_ReturnsOK() {
        String result = controller.syncSchedulerConfFromUI("DCU001", "2043", "sched_data", "A1B2C3D4");
        assertEquals("OK", result);
        verify(serverHandler, times(1)).sendSchedulerConfigurationChangeRequestToClient(
                anyString(), eq("DCU001"), eq("A1B2C3D4"), eq("sched_data"), eq("2043"));
    }

    @Test
    public void testSyncSchedulerConfFromUI_WithEmptyParams_DoesNotThrow() {
        String result = controller.syncSchedulerConfFromUI("", "", "", "");
        assertEquals("OK", result);
    }

    // --- clearHafeOpenedConnections ---

    @Test
    public void testClearHafeOpenedConnections_ReturnsDONE() {
        String result = controller.clearHafeOpenedConnections();
        assertEquals("DONE", result);
    }
}

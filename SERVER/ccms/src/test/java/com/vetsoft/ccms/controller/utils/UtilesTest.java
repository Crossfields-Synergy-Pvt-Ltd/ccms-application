package com.vetsoft.ccms.controller.utils;

import static org.junit.Assert.*;

import org.junit.Test;

public class UtilesTest {

    private static final String FILE_ID = "5A1B2C3D";
    private static final String DCU_ID = "000007FB";

    @Test
    public void testGetNodeConfInformationExchangePacket_StartsWith55AA55AA() {
        String result = Utiles.getNodeConfInformationExchangePacket(FILE_ID, DCU_ID);
        assertTrue(result.toUpperCase().startsWith("55AA55AA"));
    }

    @Test
    public void testGetNodeConfInformationExchangePacket_ContainsFileType03() {
        String result = Utiles.getNodeConfInformationExchangePacket(FILE_ID, DCU_ID);
        assertTrue("Node conf should contain identifier 03",
                result.toUpperCase().contains("03"));
    }

    @Test
    public void testGetNodeConfInformationExchangePacket_ContainsFileIdentifier() {
        String result = Utiles.getNodeConfInformationExchangePacket(FILE_ID, DCU_ID);
        assertTrue(result.toUpperCase().contains(FILE_ID.toUpperCase()));
    }

    @Test
    public void testGetNodeConfInformationExchangePacket_ContainsDcuIdentifier() {
        String result = Utiles.getNodeConfInformationExchangePacket(FILE_ID, DCU_ID);
        assertTrue(result.toUpperCase().contains(DCU_ID.toUpperCase()));
    }

    @Test
    public void testGetNodeConfPacket_HasPayloadLen0006() {
        String result = Utiles.getNodeConfInformationExchangePacket(FILE_ID, DCU_ID);
        assertTrue(result.toUpperCase().contains("0006"));
    }

    @Test
    public void testGetScheduleConfInformationExchangePacket_StartsWith55AA55AA() {
        String result = Utiles.getScheduleConfInformationExchangePacket(FILE_ID, DCU_ID);
        assertTrue(result.toUpperCase().startsWith("55AA55AA"));
    }

    @Test
    public void testGetScheduleConfInformationExchangePacket_ContainsFileType04() {
        String result = Utiles.getScheduleConfInformationExchangePacket(FILE_ID, DCU_ID);
        assertTrue("Schedule conf should contain identifier 04",
                result.toUpperCase().contains("04"));
    }

    @Test
    public void testGetScheduleConfInformationExchangePacket_ContainsFileIdentifier() {
        String result = Utiles.getScheduleConfInformationExchangePacket(FILE_ID, DCU_ID);
        assertTrue(result.toUpperCase().contains(FILE_ID.toUpperCase()));
    }

    @Test
    public void testGetScheduleConfInformationExchangePacket_ContainsDcuIdentifier() {
        String result = Utiles.getScheduleConfInformationExchangePacket(FILE_ID, DCU_ID);
        assertTrue(result.toUpperCase().contains(DCU_ID.toUpperCase()));
    }

    @Test
    public void testGetScheduleConfPacket_HasPayloadLen0006() {
        String result = Utiles.getScheduleConfInformationExchangePacket(FILE_ID, DCU_ID);
        assertTrue(result.toUpperCase().contains("0006"));
    }

    @Test
    public void testNodeAndSchedule_ProduceDifferentPackets() {
        String node = Utiles.getNodeConfInformationExchangePacket(FILE_ID, DCU_ID);
        String sched = Utiles.getScheduleConfInformationExchangePacket(FILE_ID, DCU_ID);
        assertNotEquals("Node and schedule packets should differ", node, sched);
    }

    @Test
    public void testNodeConfWithEmptyFileId_DoesNotThrow() {
        String result = Utiles.getNodeConfInformationExchangePacket("", DCU_ID);
        assertNotNull(result);
    }

    @Test
    public void testScheduleConfWithEmptyDcuId_DoesNotThrow() {
        String result = Utiles.getScheduleConfInformationExchangePacket(FILE_ID, "");
        assertNotNull(result);
    }

    @Test
    public void testNodeConf_ReturnsNonEmpty() {
        String result = Utiles.getNodeConfInformationExchangePacket(FILE_ID, DCU_ID);
        assertFalse(result.isEmpty());
    }

    @Test
    public void testScheduleConf_ReturnsNonEmpty() {
        String result = Utiles.getScheduleConfInformationExchangePacket(FILE_ID, DCU_ID);
        assertFalse(result.isEmpty());
    }
}

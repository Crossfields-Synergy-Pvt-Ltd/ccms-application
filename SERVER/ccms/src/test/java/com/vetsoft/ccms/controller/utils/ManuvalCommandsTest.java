package com.vetsoft.ccms.controller.utils;

import static org.junit.Assert.*;

import org.junit.Test;

public class ManuvalCommandsTest {

    private static final String KNOWN_SERIAL = "1905HY1P1C009534";
    private static final int KNOWN_IDENTIFIER = 2043;

    @Test
    public void testTurnOnLight_WithValidParams_ReturnsNonEmptyHex() {
        String result = ManuvalCommands.turnOnLight(KNOWN_SERIAL, KNOWN_IDENTIFIER);
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void testTurnOnLight_PacketStartsWith55AA55AA() {
        String result = ManuvalCommands.turnOnLight(KNOWN_SERIAL, KNOWN_IDENTIFIER);
        assertTrue("Packet should start with sync header 55AA55AA",
                result.toUpperCase().startsWith("55AA55AA"));
    }

    @Test
    public void testTurnOnLight_ContainsCommand06() {
        String result = ManuvalCommands.turnOnLight(KNOWN_SERIAL, KNOWN_IDENTIFIER);
        assertTrue("Packet should contain command identifier 06 (Light ON/OFF)",
                result.toUpperCase().contains("06"));
    }

    @Test
    public void testTurnOnLight_OperationType01ForOn() {
        String result = ManuvalCommands.turnOnLight(KNOWN_SERIAL, KNOWN_IDENTIFIER);
        int opTypeIndex = result.toUpperCase().indexOf("06") + 2 + 4; // skip cmd + payload len
        String opType = result.substring(opTypeIndex, opTypeIndex + 2);
        assertEquals("01", opType);
    }

    @Test
    public void testTurnOnLight_OperationValue0001ForOn() {
        String result = ManuvalCommands.turnOnLight(KNOWN_SERIAL, KNOWN_IDENTIFIER);
        int opValIndex = result.toUpperCase().indexOf("06") + 2 + 4 + 2; // skip cmd + payload len + op type
        String opVal = result.substring(opValIndex, opValIndex + 4);
        assertEquals("0001", opVal);
    }

    @Test
    public void testTurnOffLight_WithValidParams_ReturnsNonEmptyHex() {
        String result = ManuvalCommands.turnOffLight(KNOWN_SERIAL, KNOWN_IDENTIFIER);
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void testTurnOffLight_PacketStartsWith55AA55AA() {
        String result = ManuvalCommands.turnOffLight(KNOWN_SERIAL, KNOWN_IDENTIFIER);
        assertTrue("Packet should start with sync header 55AA55AA",
                result.toUpperCase().startsWith("55AA55AA"));
    }

    @Test
    public void testTurnOffLight_OperationValue0000ForOff() {
        String result = ManuvalCommands.turnOffLight(KNOWN_SERIAL, KNOWN_IDENTIFIER);
        int opValIndex = result.toUpperCase().indexOf("06") + 2 + 4 + 2;
        String opVal = result.substring(opValIndex, opValIndex + 4);
        assertEquals("0000", opVal);
    }

    @Test
    public void testTurnOnLight_HasCRCAppended() {
        String result = ManuvalCommands.turnOnLight(KNOWN_SERIAL, KNOWN_IDENTIFIER);
        String crc = result.substring(result.length() - 2);
        assertTrue("CRC should be a valid hex string",
                crc.matches("[0-9A-Fa-f]{2}"));
    }

    @Test
    public void testTurnOffLight_HasCRCAppended() {
        String result = ManuvalCommands.turnOffLight(KNOWN_SERIAL, KNOWN_IDENTIFIER);
        String crc = result.substring(result.length() - 2);
        assertTrue("CRC should be a valid hex string",
                crc.matches("[0-9A-Fa-f]{2}"));
    }

    @Test
    public void testTurnOnLight_DcuIdentifierInPacket() {
        String result = ManuvalCommands.turnOnLight(KNOWN_SERIAL, KNOWN_IDENTIFIER);
        String expectedHexIdent = String.format("%08x", KNOWN_IDENTIFIER);
        assertTrue("Packet should contain the DCU identifier",
                result.toUpperCase().contains(expectedHexIdent.toUpperCase()));
    }

    @Test
    public void testTurnOnLight_WithZeroIdentifier_DoesNotThrow() {
        String result = ManuvalCommands.turnOnLight(KNOWN_SERIAL, 0);
        assertNotNull(result);
    }

    @Test
    public void testTurnOffLight_WithEmptySerial_DoesNotThrow() {
        String result = ManuvalCommands.turnOffLight("", KNOWN_IDENTIFIER);
        assertNotNull(result);
    }

    @Test
    public void testTurnOnLight_WithNullSerial_DoesNotThrow() {
        String result = ManuvalCommands.turnOnLight(null, KNOWN_IDENTIFIER);
        assertNotNull(result);
    }

    @Test
    public void testTurnOnOff_ProduceDifferentPackets() {
        String on = ManuvalCommands.turnOnLight(KNOWN_SERIAL, KNOWN_IDENTIFIER);
        String off = ManuvalCommands.turnOffLight(KNOWN_SERIAL, KNOWN_IDENTIFIER);
        assertNotEquals("ON and OFF packets should differ", on, off);
    }

    @Test
    public void testTurnOnLight_ContainsNodeId() {
        String result = ManuvalCommands.turnOnLight(KNOWN_SERIAL, KNOWN_IDENTIFIER);
        assertTrue("Packet should contain node id 00000001",
                result.toUpperCase().contains("00000001"));
    }

    @Test
    public void testTurnOnLight_ContainsDNS01() {
        String header = ManuvalCommands.turnOnLight(KNOWN_SERIAL, KNOWN_IDENTIFIER)
                .toUpperCase();
        int dnsIndex = header.indexOf("55AA55AA") + 8 + 2 + 2 + 8;
        String dns = header.substring(dnsIndex, dnsIndex + 2);
        assertEquals("01", dns);
    }
}

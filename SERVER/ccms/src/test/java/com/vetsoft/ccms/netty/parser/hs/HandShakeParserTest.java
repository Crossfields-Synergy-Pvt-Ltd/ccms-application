package com.vetsoft.ccms.netty.parser.hs;

import static org.junit.Assert.*;

import org.junit.Test;

import com.vetsoft.ccms.netty.pojo.HandShake;

public class HandShakeParserTest {

    @Test
    public void acceptsExistingSixteenCharacterSerial() {
        assertTrue(HandShakeParser.isValidGatewaySerial("1905HY1P1C009534"));
    }

    @Test
    public void rejectsNullAndWrongLengthSerials() {
        assertFalse(HandShakeParser.isValidGatewaySerial(null));
        assertFalse(HandShakeParser.isValidGatewaySerial("DCU001"));
        assertFalse(HandShakeParser.isValidGatewaySerial("1905HY1P1C00953"));
    }

    @Test
    public void rejectsPaddedOrControlCharacterSerials() {
        assertFalse(HandShakeParser.isValidGatewaySerial("\u00001905HY1P1C00953"));
        assertFalse(HandShakeParser.isValidGatewaySerial("1905HY1P1C00953\u0000"));
        assertFalse(HandShakeParser.isValidGatewaySerial(" 1905HY1P1C00953"));
    }

    @Test
    public void handshakeResponseUsesProtocolGatewayIdentifierForAlphanumericDcuSerial() {
        HandShake handshake = new HandShake();
        handshake.setGateway_identifier(2043);
        handshake.setSerial_number("1903SO1P1C013917");

        String request = "55AA55AA0101000007FB01060008"
                + "31393033534F31503143303133393137";

        String response = HandShakeParser.getHandshakeResponseData(request, handshake);

        assertEquals("000007fb", response.substring(12, 20));
    }
}

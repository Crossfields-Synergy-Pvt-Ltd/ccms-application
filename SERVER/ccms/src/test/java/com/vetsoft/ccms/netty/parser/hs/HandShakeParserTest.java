package com.vetsoft.ccms.netty.parser.hs;

import static org.junit.Assert.*;

import org.junit.Test;

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
}

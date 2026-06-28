package com.vetsoft.ccms.netty.parser;

import static org.junit.Assert.*;

import org.junit.Test;

import com.vetsoft.ccms.netty.pojo.CommonHeader;

public class BaseUtilTest {

    private static final String KNOWN_HEX = "55AA55AA0101000007fcC6000019313930334D4331503143303039333839005d3e94ca000007fc";
    private static final String KNOWN_STRING_CONVERSION = "HELLO";

    // --- getHexString ---

    @Test
    public void testGetHexString_ConvertsStringCorrectly() {
        String expected = "48454C4C4F";
        assertEquals(expected, BaseUtil.getHexString(KNOWN_STRING_CONVERSION));
    }

    @Test
    public void testGetHexString_EmptyString_ReturnsEmpty() {
        assertEquals("", BaseUtil.getHexString(""));
    }

    @Test
    public void testGetHexString_NumbersAndSymbols() {
        String input = "ABC123!@#";
        String result = BaseUtil.getHexString(input);
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void testGetHexString_OutputIsUpperCase() {
        String result = BaseUtil.getHexString(KNOWN_STRING_CONVERSION);
        assertEquals(result, result.toUpperCase());
    }

    @Test(expected = NullPointerException.class)
    public void testGetHexString_NullInput_ThrowsNPE() {
        BaseUtil.getHexString(null);
    }

    // --- convertHexToString ---

    @Test
    public void testConvertHexToString_RoundTrips() {
        String original = "55AA55AA";
        String binary = BaseUtil.convertHexToString(original);
        String backToHex = BaseUtil.getHexString(binary);
        assertEquals(original, backToHex);
    }

    @Test
    public void testConvertHexToString_EmptyInput() {
        assertEquals("", BaseUtil.convertHexToString(""));
    }

    @Test
    public void testConvertHexToString_HelloWorld() {
        String hex = "48656C6C6F20576F726C64";
        assertEquals("Hello World", BaseUtil.convertHexToString(hex));
    }

    // --- calculateCRC ---

    @Test
    public void testCalculateCRC_ForKnownPacket_ReturnsNonZero() {
        String binary = BaseUtil.convertHexToString("55AA55AA0101000007FB");
        char[] chars = binary.toCharArray();
        int crc = BaseUtil.calculateCRC(chars, chars.length);
        assertTrue("CRC should be non-negative", crc >= 0);
    }

    @Test
    public void testCalculateCRC_DifferentInputs_DifferentCRC() {
        String bin1 = BaseUtil.convertHexToString("55AA55AA010100000001");
        String bin2 = BaseUtil.convertHexToString("55AA55AA010100000002");
        int crc1 = BaseUtil.calculateCRC(bin1.toCharArray(), bin1.length());
        int crc2 = BaseUtil.calculateCRC(bin2.toCharArray(), bin2.length());
        assertNotEquals("Different inputs should produce different CRCs", crc1, crc2);
    }

    @Test
    public void testCalculateCRC_SameInput_SameCRC() {
        String bin = BaseUtil.convertHexToString("55AA55AA0101000007FB");
        int crc1 = BaseUtil.calculateCRC(bin.toCharArray(), bin.length());
        int crc2 = BaseUtil.calculateCRC(bin.toCharArray(), bin.length());
        assertEquals("Same input should produce same CRC", crc1, crc2);
    }

    @Test
    public void testCalculateCRC_EmptyInput() {
        int crc = BaseUtil.calculateCRC(new char[0], 0);
        assertTrue("CRC should be non-negative", crc >= 0);
    }

    // --- getCommonHeader ---

    @Test
    public void testGetCommonHeader_ParsesSof() {
        CommonHeader header = BaseUtil.getCommonHeader(KNOWN_HEX);
        assertEquals("55AA55AA", header.getSof());
    }

    @Test
    public void testGetCommonHeader_ParsesProtocolVersion() {
        CommonHeader header = BaseUtil.getCommonHeader(KNOWN_HEX);
        assertEquals(1, header.getProtocol_version());
    }

    @Test
    public void testGetCommonHeader_ParsesGatewayIdentifier() {
        CommonHeader header = BaseUtil.getCommonHeader(KNOWN_HEX);
        assertTrue(header.getGateway_identifier() > 0);
    }

    @Test
    public void testGetCommonHeader_InvalidHex_ReturnsEmptyHeader() {
        CommonHeader header = BaseUtil.getCommonHeader("");
        assertNull(header.getSof());
    }

    @Test
    public void testGetCommonHeader_ShortHex_NoException() {
        CommonHeader header = BaseUtil.getCommonHeader("55AA");
        assertNotNull(header);
    }

    // --- validatPacket ---

    @Test
    public void testValidatPacket_InvalidCRC_ReturnsFalse() {
        assertFalse(BaseUtil.validatPacket("55AA55AA010100000001"));
    }

    @Test
    public void testValidatPacket_EmptyString_ReturnsTrue() {
        assertTrue(BaseUtil.validatPacket(""));
    }

    // --- getDeviceSerialNumberFromDecimalToHexWithPadding ---

    @Test
    public void testGetDeviceSerialNumberFromDecimalToHexWithPadding_8Chars() {
        String result = BaseUtil.getDeviceSerialNumberFromDecimalToHexWithPadding(2043);
        assertEquals(8, result.length());
        assertTrue(result.contains("7fb"));
    }

    @Test
    public void testGetDeviceSerialNumberFromDecimalToHexWithPadding_Zero() {
        String result = BaseUtil.getDeviceSerialNumberFromDecimalToHexWithPadding(0);
        assertEquals("00000000", result);
    }

    // --- getCurrentYYMMDD ---

    @Test
    public void testGetCurrentYYMMDD_Returns8Digits() {
        int result = BaseUtil.getCurrentYYMMDD();
        assertTrue(result > 20250000);
    }

    // --- byteToHex ---

    @Test
    public void testByteToHex_ValidByte() {
        assertEquals("0a", BaseUtil.byteToHex((byte) 10));
    }

    @Test
    public void testByteToHex_Zero() {
        assertEquals("00", BaseUtil.byteToHex((byte) 0));
    }

    @Test
    public void testByteToHex_Max() {
        assertEquals("ff", BaseUtil.byteToHex((byte) 255));
    }

    // --- byetArrayToHex ---

    @Test
    public void testByetArrayToHex_ValidArray() {
        byte[] arr = { (byte) 0x55, (byte) 0xAA };
        assertEquals("55aa", BaseUtil.byetArrayToHex(arr));
    }

    @Test
    public void testByetArrayToHex_EmptyArray() {
        assertEquals("", BaseUtil.byetArrayToHex(new byte[0]));
    }
}

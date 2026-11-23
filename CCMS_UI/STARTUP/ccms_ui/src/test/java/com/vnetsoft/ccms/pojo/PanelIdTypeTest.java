package com.vnetsoft.ccms.pojo;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PanelIdTypeTest {

    @Test
    public void handshakePreservesAlphanumericPanelId() {
        HandShake handshake = new HandShake();

        handshake.setPanel_unique_id("CSA9388");

        assertEquals("CSA9388", handshake.getPanel_unique_id());
    }

    @Test
    public void dcuPreservesNumericPanelIdAsText() {
        DCU dcu = new DCU();

        dcu.setPanel_unique_id("9388");

        assertEquals("9388", dcu.getPanel_unique_id());
    }
}

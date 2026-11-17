package com.vetsoft.ccms;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import io.netty.channel.ChannelHandlerContext;

public class MainBootAppTest {

    @Before
    public void clearRegistries() {
        MainBootApp.channels_list.clear();
        MainBootApp.hafe_opened_channels_list.clear();
    }

    @Test
    public void registersAndRemovesByChannelIdentity() {
        ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);

        MainBootApp.trackChannels("1905HY1P1C009534", "10.0.0.1", ctx);
        MainBootApp.removeChannel(ctx);

        assertFalse(MainBootApp.channels_list.containsKey("1905HY1P1C009534"));
    }

    @Test
    public void replacingConnectionDoesNotAllowOldDisconnectToRemoveNewConnection() {
        ChannelHandlerContext oldCtx = mock(ChannelHandlerContext.class);
        ChannelHandlerContext newCtx = mock(ChannelHandlerContext.class);

        MainBootApp.trackChannels("1905HY1P1C009534", "10.0.0.1", oldCtx);
        ChannelHandlerContext previous = MainBootApp.trackChannels(
                "1905HY1P1C009534", "10.0.0.2", newCtx);

        assertSame(oldCtx, previous);
        MainBootApp.removeChannel(oldCtx);
        assertSame(newCtx, MainBootApp.channels_list.get("1905HY1P1C009534").ctx);
    }

    @Test
    public void rejectsNullSerialWithoutCreatingMapEntry() {
        MainBootApp.trackChannels(null, "10.0.0.1", mock(ChannelHandlerContext.class));
        assertTrue(MainBootApp.channels_list.isEmpty());
    }
}

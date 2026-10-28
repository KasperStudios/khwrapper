package com.kasperstudios.khwrapper.neoforge;

import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;

public final class ExampleModNeoForge {
    public static void setup(IEventBus modEventBus) {
        // Initialize the wrapper with the NeoForge implementation
        com.kasperstudios.khwrapper.KHWrapper.init(new NeoForgePlatform());

        // Register mod bus listeners
        modEventBus.register(NeoForgeNetworkingBootstrap.class);
        modEventBus.register(NeoForgeModBusHandlers.class);

        // Register game bus listeners
        NeoForge.EVENT_BUS.register(NeoForgeEventHandlers.class);
    }
}

package com.kasperstudios.khwrapper.neoforge;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

public class NeoForgeEventHandlers {

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        for (java.util.function.Consumer<net.minecraft.server.MinecraftServer> callback : NeoForgeCommonCallbacks.serverTickCallbacks) {
            callback.accept(event.getServer());
        }
    }
}

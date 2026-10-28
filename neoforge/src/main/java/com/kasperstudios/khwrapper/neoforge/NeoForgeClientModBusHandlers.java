package com.kasperstudios.khwrapper.neoforge;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.minecraft.client.KeyMapping;

public class NeoForgeClientModBusHandlers {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        for (Runnable callback : NeoForgeClientCallbacks.initCallbacks) {
            callback.run();
        }
    }

    @SubscribeEvent
    public static void onRegisterGuiOverlays(net.neoforged.neoforge.client.event.RegisterGuiLayersEvent event) {
        for (NeoForgeClientCallbacks.GuiOverlayRegistration reg : NeoForgeClientCallbacks.guiOverlays) {
            event.registerAbove(net.neoforged.neoforge.client.gui.VanillaGuiLayers.HOTBAR, 
                net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("kashub", reg.name()), 
                (guiGraphics, partialTick) -> reg.renderer().accept(guiGraphics, partialTick.getGameTimeDeltaTicks()));
        }
    }

    @SubscribeEvent
    public static void onKeyMappingRegistration(RegisterKeyMappingsEvent event) {
        for (KeyMapping mapping : NeoForgeClientCallbacks.keyMappings) {
            event.register(mapping);
        }
    }

}

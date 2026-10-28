package com.kasperstudios.khwrapper.neoforge;

import net.minecraft.client.KeyMapping;

import java.util.ArrayList;
import java.util.List;

public class NeoForgeClientCallbacks {
    public static final List<Runnable> initCallbacks = new ArrayList<>();
    public static final List<Runnable> tickCallbacks = new ArrayList<>();
    public static final List<Runnable> stoppingCallbacks = new ArrayList<>();
    public static final List<KeyMapping> keyMappings = new ArrayList<>();

    // GUI Overlays
    public record GuiOverlayRegistration(String name, java.util.function.BiConsumer<net.minecraft.client.gui.GuiGraphics, Float> renderer) {}
    public static final List<GuiOverlayRegistration> guiOverlays = new ArrayList<>();

    // Commands
    public record CommandRegistration(String name, String description, java.util.function.Consumer<com.kasperstudios.khwrapper.ClientCommandContext> handler) {}
    public static final List<CommandRegistration> commandRegistrations = new ArrayList<>();

    public static void addInitCallback(Runnable callback) {
        initCallbacks.add(callback);
    }

    public static void addTickCallback(Runnable callback) {
        tickCallbacks.add(callback);
    }

    public static void addStoppingCallback(Runnable callback) {
        stoppingCallbacks.add(callback);
    }

    public static void addKeyMapping(KeyMapping mapping) {
        keyMappings.add(mapping);
    }
}

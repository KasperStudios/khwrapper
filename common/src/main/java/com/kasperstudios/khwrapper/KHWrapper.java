package com.kasperstudios.khwrapper;

import java.nio.file.Path;

/**
 * Global access point for the platform wrapper.
 */
public class KHWrapper {
    private static IPlatformWrapper platform;

    public static void init(IPlatformWrapper implementation) {
        if (platform != null) {
            return;
        }
        platform = implementation;
    }

    public static IPlatformWrapper get() {
        if (platform == null) {
            throw new IllegalStateException("KHWrapper not initialized! Call init() in your mod's entry point.");
        }
        return platform;
    }
    
    public static Path getConfigDir() { return get().getConfigDir(); }
    public static Path getGameDir() { return get().getGameDir(); }
    public static boolean isModLoaded(String modId) { return get().isModLoaded(modId); }

    public static void onModInit(Runnable callback) { get().onModInit(callback); }
    public static void onClientInit(Runnable callback) { get().onClientInit(callback); }
    public static void onClientTick(Runnable callback) { get().onClientTick(callback); }
    public static void onClientStopping(Runnable callback) { get().onClientStopping(callback); }

    public static void setKeyState(String keyId, boolean pressed) { get().setKeyState(keyId, pressed); }
    public static boolean isKeyPressed(int glfwKeyCode) { return get().isKeyPressed(glfwKeyCode); }
    public static void sendChat(String message) { get().sendChat(message); }
    public static void setScreen(Object screen) { get().setScreen(screen); }
    public static IKeyBindingHandle registerKeyBinding(String translationKey, int glfwKeyCode, String category) { 
        return get().registerKeyBinding(translationKey, glfwKeyCode, category); 
    }
    public static void registerClientCommand(String name, String description, java.util.function.Consumer<ClientCommandContext> handler) {
        get().registerClientCommand(name, description, handler);
    }

    // Networking
    public static void register(NetworkRegistration registration) { get().register(registration); }
    public static void sendPacket(IPacketPayload payload) { get().sendPacket(payload); }

    // Connection Events
    public static void onPlayerJoin(Runnable callback) { get().onPlayerJoin(callback); }
    public static void onPlayerLeave(Runnable callback) { get().onPlayerLeave(callback); }
}

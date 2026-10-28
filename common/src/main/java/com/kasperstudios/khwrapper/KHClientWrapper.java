package com.kasperstudios.khwrapper;

/**
 * Global access point for the client-specific platform wrapper.
 */
public class KHClientWrapper {
    private static IClientPlatformWrapper platform;

    public static void init(IClientPlatformWrapper implementation) {
        if (platform != null) {
            return;
        }
        platform = implementation;
    }

    public static IClientPlatformWrapper get() {
        if (platform == null) {
            throw new IllegalStateException("KHClientWrapper not initialized! Call init() in your mod's client entry point.");
        }
        return platform;
    }

    public static void onClientInit(Runnable callback) { get().onClientInit(callback); }
    public static void onClientTick(Runnable callback) { get().onClientTick(callback); }
    public static void onClientStopping(Runnable callback) { get().onClientStopping(callback); }

    public static IKeyBindingHandle registerKeyBinding(String translationKey, int glfwKeyCode, String category) { 
        return get().registerKeyBinding(translationKey, glfwKeyCode, category); 
    }
    public static boolean isKeyPressed(int glfwKeyCode) { return get().isKeyPressed(glfwKeyCode); }
    public static void setKeyState(String keyId, boolean pressed) { get().setKeyState(keyId, pressed); }
    
    public static void registerGuiOverlay(String name, java.util.function.BiConsumer<net.minecraft.client.gui.GuiGraphics, Float> renderer) {
        get().registerGuiOverlay(name, renderer);
    }
    
    public static void registerClientCommand(String name, String description, java.util.function.Consumer<ClientCommandContext> handler) {
        get().registerClientCommand(name, description, handler);
    }
    
    public static void setScreen(Object screen) { get().setScreen(screen); }
    public static void sendChat(String message) { get().sendChat(message); }
}

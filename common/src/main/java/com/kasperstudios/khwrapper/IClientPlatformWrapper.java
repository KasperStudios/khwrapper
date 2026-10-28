package com.kasperstudios.khwrapper;

public interface IClientPlatformWrapper {
    // Client Lifecycle
    void onClientInit(Runnable callback);
    void onClientTick(Runnable callback);
    void onClientStopping(Runnable callback);

    // Client Input
    IKeyBindingHandle registerKeyBinding(String translationKey, int glfwKeyCode, String category);
    boolean isKeyPressed(int glfwKeyCode);
    
    /**
     * Set the pressed state of a key binding.
     */
    void setKeyState(String keyId, boolean pressed);

    // Client GUI
    void registerGuiOverlay(String name, java.util.function.BiConsumer<net.minecraft.client.gui.GuiGraphics, Float> renderer);

    // Client Commands
    void registerClientCommand(String name, String description, java.util.function.Consumer<ClientCommandContext> handler);

    /** Sets the current screen (Client-only). */
    void setScreen(Object screen);

    // Chat
    void sendChat(String message);
}

package com.kasperstudios.khwrapper.neoforge;

import com.kasperstudios.khwrapper.IClientPlatformWrapper;
import com.kasperstudios.khwrapper.IKeyBindingHandle;
import com.kasperstudios.khwrapper.ClientCommandContext;
import net.minecraft.client.Minecraft;

public class NeoForgeClientPlatform implements IClientPlatformWrapper {

    @Override
    public void onClientInit(Runnable callback) {
        NeoForgeClientCallbacks.addInitCallback(callback);
    }

    @Override
    public void onClientTick(Runnable callback) {
        NeoForgeClientCallbacks.addTickCallback(callback);
    }

    @Override
    public void onClientStopping(Runnable callback) {
        NeoForgeClientCallbacks.addStoppingCallback(callback);
    }

    @Override
    public IKeyBindingHandle registerKeyBinding(String translationKey, int glfwKeyCode, String category) {
        net.minecraft.client.KeyMapping keyMapping = new net.minecraft.client.KeyMapping(
            translationKey,
            glfwKeyCode,
            category
        );
        NeoForgeClientCallbacks.addKeyMapping(keyMapping);
        return new IKeyBindingHandle() {
            @Override
            public boolean isPressed() {
                return keyMapping.isDown();
            }
            @Override
            public boolean wasPressed() {
                return keyMapping.consumeClick();
            }
        };
    }

    @Override
    public boolean isKeyPressed(int glfwKeyCode) {
        long handle = Minecraft.getInstance().getWindow().getWindow();
        return com.mojang.blaze3d.platform.InputConstants.isKeyDown(handle, glfwKeyCode);
    }

    @Override
    public void registerGuiOverlay(String name, java.util.function.BiConsumer<net.minecraft.client.gui.GuiGraphics, Float> renderer) {
        NeoForgeClientCallbacks.guiOverlays.add(new NeoForgeClientCallbacks.GuiOverlayRegistration(name, renderer));
    }

    @Override
    public void setKeyState(String keyId, boolean pressed) {
        Minecraft client = Minecraft.getInstance();
        if (client == null) return;
        
        switch (keyId.toLowerCase()) {
            case "forward": client.options.keyUp.setDown(pressed); break;
            case "back": client.options.keyDown.setDown(pressed); break;
            case "left": client.options.keyLeft.setDown(pressed); break;
            case "right": client.options.keyRight.setDown(pressed); break;
            case "jump": client.options.keyJump.setDown(pressed); break;
            case "sneak": client.options.keyShift.setDown(pressed); break;
            case "sprint": client.options.keySprint.setDown(pressed); break;
        }
    }

    @Override
    public void registerClientCommand(String name, String description, java.util.function.Consumer<ClientCommandContext> handler) {
        NeoForgeClientCallbacks.commandRegistrations.add(new NeoForgeClientCallbacks.CommandRegistration(name, description, handler));
    }

    @Override
    public void setScreen(Object screen) {
        Minecraft.getInstance().setScreen((net.minecraft.client.gui.screens.Screen) screen);
    }

    @Override
    public void sendChat(String message) {
        Minecraft client = Minecraft.getInstance();
        if (client.player != null) {
            client.player.connection.sendChat(message);
        }
    }
}

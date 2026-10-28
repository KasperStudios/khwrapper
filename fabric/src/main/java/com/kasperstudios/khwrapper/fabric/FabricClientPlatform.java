package com.kasperstudios.khwrapper.fabric;

import com.kasperstudios.khwrapper.IClientPlatformWrapper;
import com.kasperstudios.khwrapper.IKeyBindingHandle;
import com.kasperstudios.khwrapper.ClientCommandContext;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;

public class FabricClientPlatform implements IClientPlatformWrapper {

    @Override
    public void onClientInit(Runnable callback) {
        net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents.CLIENT_STARTED.register(client -> callback.run());
    }

    @Override
    public void onClientTick(Runnable callback) {
        net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register(client -> callback.run());
    }

    @Override
    public void onClientStopping(Runnable callback) {
        net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents.CLIENT_STOPPING.register(client -> callback.run());
    }

    @Override
    public IKeyBindingHandle registerKeyBinding(String translationKey, int glfwKeyCode, String category) {
        net.minecraft.client.KeyMapping keyMapping = new net.minecraft.client.KeyMapping(
            translationKey,
            glfwKeyCode,
            category
        );
        net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper.registerKeyBinding(keyMapping);
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
        net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            renderer.accept(drawContext, tickDelta.getGameTimeDeltaTicks());
        });
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
        net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal(name)
                .executes(context -> {
                    handler.accept(new ClientCommandContext() {
                        @Override
                        public void sendFeedback(String message) {
                            context.getSource().sendFeedback(net.minecraft.network.chat.Component.literal(message));
                        }

                        @Override
                        public void sendError(String message) {
                            context.getSource().sendError(net.minecraft.network.chat.Component.literal(message));
                        }

                        @Override
                        public String[] getArguments() {
                            String input = context.getInput();
                            String[] parts = input.split("\\s+");
                            if (parts.length > 1) {
                                String[] args = new String[parts.length - 1];
                                System.arraycopy(parts, 1, args, 0, parts.length - 1);
                                return args;
                            }
                            return new String[0];
                        }
                    });
                    return 1;
                })
            );
        });
    }

    @Override
    public void sendChat(String message) {
        Minecraft client = Minecraft.getInstance();
        if (client.player != null) {
            client.player.connection.sendChat(message);
        }
    }

    @Override
    public void setScreen(Object screen) {
        if (FabricLoader.getInstance().getEnvironmentType() == net.fabricmc.api.EnvType.CLIENT) {
            Minecraft.getInstance().setScreen((net.minecraft.client.gui.screens.Screen) screen);
        }
    }
}

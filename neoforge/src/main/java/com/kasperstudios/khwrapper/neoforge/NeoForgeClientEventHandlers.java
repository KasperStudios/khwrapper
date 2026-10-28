package com.kasperstudios.khwrapper.neoforge;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;

public class NeoForgeClientEventHandlers {

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        for (Runnable callback : NeoForgeClientCallbacks.tickCallbacks) {
            callback.run();
        }
    }

    @SubscribeEvent
    public static void onPlayerJoin(ClientPlayerNetworkEvent.LoggingIn event) {
        for (Runnable callback : NeoForgeCommonCallbacks.playerJoinCallbacks) {
            callback.run();
        }
    }

    @SubscribeEvent
    public static void onPlayerLeave(ClientPlayerNetworkEvent.LoggingOut event) {
        for (Runnable callback : NeoForgeCommonCallbacks.playerLeaveCallbacks) {
            callback.run();
        }
    }

    @SubscribeEvent
    public static void onRegisterCommands(net.neoforged.neoforge.client.event.RegisterClientCommandsEvent event) {
        for (NeoForgeClientCallbacks.CommandRegistration reg : NeoForgeClientCallbacks.commandRegistrations) {
            event.getDispatcher().register(net.minecraft.commands.Commands.literal(reg.name())
                .executes(context -> {
                    reg.handler().accept(new com.kasperstudios.khwrapper.ClientCommandContext() {
                        @Override
                        public void sendFeedback(String message) {
                            context.getSource().sendSystemMessage(net.minecraft.network.chat.Component.literal(message));
                        }

                        @Override
                        public void sendError(String message) {
                            context.getSource().sendFailure(net.minecraft.network.chat.Component.literal(message));
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
        }
    }
}

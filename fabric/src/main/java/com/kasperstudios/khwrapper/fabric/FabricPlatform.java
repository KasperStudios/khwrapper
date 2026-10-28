package com.kasperstudios.khwrapper.fabric;

import com.kasperstudios.khwrapper.IPlatformWrapper;
import com.kasperstudios.khwrapper.IKeyBindingHandle;
import net.fabricmc.loader.api.FabricLoader;
import java.nio.file.Path;

public class FabricPlatform implements IPlatformWrapper {
    private net.minecraft.server.MinecraftServer server;

    public FabricPlatform() {
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.SERVER_STARTED.register(s -> this.server = s);
    }

    @Override
    public void openMenu(net.minecraft.server.level.ServerPlayer player, net.minecraft.world.MenuProvider provider) {
        player.openMenu(provider);
    }

    @Override
    public net.minecraft.server.MinecraftServer getServer() {
        return server;
    }

    @Override
    public Path getConfigDir() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public Path getGameDir() {
        return FabricLoader.getInstance().getGameDir();
    }

    @Override
    public Path getModsDir() {
        return FabricLoader.getInstance().getGameDir().resolve("mods");
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public java.util.List<com.kasperstudios.khwrapper.PlatformModInfo> getAllMods() {
        java.util.List<com.kasperstudios.khwrapper.PlatformModInfo> list = new java.util.ArrayList<>();
        for (net.fabricmc.loader.api.ModContainer mod : FabricLoader.getInstance().getAllMods()) {
            list.add(new com.kasperstudios.khwrapper.PlatformModInfo(
                mod.getMetadata().getId(),
                mod.getMetadata().getName(),
                mod.getMetadata().getVersion().getFriendlyString()
            ));
        }
        return list;
    }

    @Override
    public String getModVersion(String modId) {
        return FabricLoader.getInstance()
                .getModContainer(modId)
                .map(m -> m.getMetadata().getVersion().getFriendlyString())
                .orElse("unknown");
    }

    @Override
    public net.minecraft.world.level.block.Block getBlock(String id) {
        return net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(net.minecraft.resources.ResourceLocation.parse(id));
    }

    @Override
    public net.minecraft.world.item.Item getItem(String id) {
        return net.minecraft.core.registries.BuiltInRegistries.ITEM.get(net.minecraft.resources.ResourceLocation.parse(id));
    }

    @Override
    public String getBlockId(Object block) {
        return net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey((net.minecraft.world.level.block.Block) block).toString();
    }

    @Override
    public String getItemId(Object item) {
        return net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey((net.minecraft.world.item.Item) item).toString();
    }

    @Override
    public java.util.Map<String, Object> getItemData(Object stack) {
        java.util.Map<String, Object> data = new java.util.HashMap<>();
        net.minecraft.world.item.ItemStack itemStack = (net.minecraft.world.item.ItemStack) stack;
        if (itemStack.isEmpty()) return data;

        // In 1.21.1, we use the component system
        itemStack.getComponents().forEach(component -> {
            String key = net.minecraft.core.registries.BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(component.type()).toString();
            // We convert to string for now to keep it simple and serializable for scripts
            data.put(key, component.value().toString());
        });

        return data;
    }

    @Override
    public void onModInit(Runnable callback) {
        // In Fabric, this would normally be called during ModInitializer.onInitialize()
        // For now, since the wrapper is initialized inside onInitialize, we might just run it immediately
        // if we are already initializing.
        callback.run();
    }

    @Override
    public void onClientInit(Runnable callback) {}

    @Override
    public void onClientTick(Runnable callback) {}

    @Override
    public void onClientStopping(Runnable callback) {
        try {
            com.kasperstudios.khwrapper.KHClientWrapper.onClientStopping(callback);
        } catch (Exception ignored) {}
    }

    @Override
    public void setKeyState(String keyId, boolean pressed) {
        try {
            com.kasperstudios.khwrapper.KHClientWrapper.setKeyState(keyId, pressed);
        } catch (Exception ignored) {}
    }

    @Override
    public boolean isKeyPressed(int glfwKeyCode) {
        try {
            return com.kasperstudios.khwrapper.KHClientWrapper.isKeyPressed(glfwKeyCode);
        } catch (Exception ignored) {
            return false;
        }
    }

    @Override
    public void sendChat(String message) {
        try {
            com.kasperstudios.khwrapper.KHClientWrapper.get().sendChat(message);
        } catch (Exception ignored) {}
    }

    @Override
    public void setScreen(Object screen) {
        try {
            com.kasperstudios.khwrapper.KHClientWrapper.get().setScreen(screen);
        } catch (Exception ignored) {}
    }

    @Override
    public com.kasperstudios.khwrapper.IKeyBindingHandle registerKeyBinding(String translationKey, int glfwKeyCode, String category) {
        try {
            return com.kasperstudios.khwrapper.KHClientWrapper.registerKeyBinding(translationKey, glfwKeyCode, category);
        } catch (Exception ignored) {
            return new com.kasperstudios.khwrapper.IKeyBindingHandle() {
                @Override public boolean isPressed() { return false; }
                @Override public boolean wasPressed() { return false; }
            };
        }
    }

    @Override
    public void registerClientCommand(String name, String description, java.util.function.Consumer<com.kasperstudios.khwrapper.ClientCommandContext> handler) {}

    @Override
    public void register(com.kasperstudios.khwrapper.NetworkRegistration registration) {
        net.minecraft.resources.ResourceLocation identifier = net.minecraft.resources.ResourceLocation.parse(registration.id());
        net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<FabricCustomPayload> payloadType = new net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<>(identifier);
        
        net.minecraft.network.codec.StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, FabricCustomPayload> codec = net.minecraft.network.codec.StreamCodec.of(
            (buf, value) -> value.payload().write(new FabricPacketBuffer(buf)),
            buf -> new FabricCustomPayload(registration.decoder().apply(new FabricPacketBuffer(buf)))
        );

        // Register for relevant directions
        if (registration.direction() == com.kasperstudios.khwrapper.PacketDirection.S2C || registration.direction() == com.kasperstudios.khwrapper.PacketDirection.BIDIRECTIONAL) {
            net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry.playS2C().register(payloadType, codec);
            
            // Register receiver if handler exists
            if (registration.handler() != null) {
                net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.registerGlobalReceiver(payloadType, (payload, context) -> {
                    registration.handler().accept(payload.payload(), new com.kasperstudios.khwrapper.IPacketContext() {
                        @Override
                        public void execute(Runnable task) {
                            context.client().execute(task);
                        }

                        @Override
                        public net.minecraft.server.level.ServerPlayer getPlayer() {
                            return null;
                        }
                    });
                });
            }
        }

        if (registration.direction() == com.kasperstudios.khwrapper.PacketDirection.C2S || registration.direction() == com.kasperstudios.khwrapper.PacketDirection.BIDIRECTIONAL) {
            net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry.playC2S().register(payloadType, codec);
            
            // Register receiver if handler exists
            if (registration.handler() != null) {
                net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.registerGlobalReceiver(payloadType, (payload, context) -> {
                    registration.handler().accept(payload.payload(), new com.kasperstudios.khwrapper.IPacketContext() {
                        @Override
                        public void execute(Runnable task) {
                            context.server().execute(task);
                        }

                        @Override
                        public net.minecraft.server.level.ServerPlayer getPlayer() {
                            return context.player();
                        }
                    });
                });
            }
        }
    }

    @Override
    public void sendPacket(com.kasperstudios.khwrapper.IPacketPayload payload) {
        net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(new FabricCustomPayload(payload));
    }

    @Override
    public void sendPacketToPlayer(net.minecraft.server.level.ServerPlayer player, com.kasperstudios.khwrapper.IPacketPayload payload) {
        net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.send(player, new FabricCustomPayload(payload));
    }

    @Override
    public void onPlayerJoin(Runnable callback) {
        net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> callback.run());
    }

    @Override
    public void onPlayerLeave(Runnable callback) {
        net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> callback.run());
    }

    @Override
    public void onServerTick(java.util.function.Consumer<net.minecraft.server.MinecraftServer> callback) {
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_SERVER_TICK.register(callback::accept);
    }

    @Override
    public void registerItem(String name, net.minecraft.world.item.Item item) {
        net.minecraft.core.Registry.register(net.minecraft.core.registries.BuiltInRegistries.ITEM, 
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("kashub", name), item);
    }

    @Override
    public void registerBlock(String name, net.minecraft.world.level.block.Block block) {
        net.minecraft.core.Registry.register(net.minecraft.core.registries.BuiltInRegistries.BLOCK, 
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("kashub", name), block);
    }

    @Override
    public void registerBlockEntity(String name, net.minecraft.world.level.block.entity.BlockEntityType<?> type) {
        net.minecraft.core.Registry.register(net.minecraft.core.registries.BuiltInRegistries.BLOCK_ENTITY_TYPE, 
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("kashub", name), type);
    }

    private record FabricCustomPayload(com.kasperstudios.khwrapper.IPacketPayload payload) implements net.minecraft.network.protocol.common.custom.CustomPacketPayload {
        @Override
        public Type<? extends net.minecraft.network.protocol.common.custom.CustomPacketPayload> type() {
            return new Type<>(net.minecraft.resources.ResourceLocation.parse(payload.getPacketId()));
        }
    }

    private static class FabricPacketBuffer implements com.kasperstudios.khwrapper.IPacketBuffer {
        private final net.minecraft.network.RegistryFriendlyByteBuf buf;
        public FabricPacketBuffer(net.minecraft.network.RegistryFriendlyByteBuf buf) { this.buf = buf; }
        @Override public void writeString(String value) { buf.writeUtf(value); }
        @Override public String readString() { return buf.readUtf(); }
        @Override public void writeInt(int value) { buf.writeInt(value); }
        @Override public int readInt() { return buf.readInt(); }
        @Override public void writeLong(long value) { buf.writeLong(value); }
        @Override public long readLong() { return buf.readLong(); }
        @Override public void writeBoolean(boolean value) { buf.writeBoolean(value); }
        @Override public boolean readBoolean() { return buf.readBoolean(); }
        @Override public void writeBytes(byte[] value) { buf.writeByteArray(value); }
        @Override public byte[] readBytes() { return buf.readByteArray(); }
        @Override public void writeDouble(double value) { buf.writeDouble(value); }
        @Override public double readDouble() { return buf.readDouble(); }
        @Override public void writeFloat(float value) { buf.writeFloat(value); }
        @Override public float readFloat() { return buf.readFloat(); }
    }

    @Override
    public void registerCreativeTab(String name, net.minecraft.network.chat.Component title, java.util.function.Supplier<net.minecraft.world.item.ItemStack> icon, java.util.List<? extends net.minecraft.world.item.Item> items) {
        net.minecraft.world.item.CreativeModeTab tab = net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup.builder()
            .icon(icon)
            .title(title)
            .displayItems((params, output) -> {
                for (net.minecraft.world.item.Item item : items) {
                    output.accept(item);
                }
            })
            .build();
        net.minecraft.core.Registry.register(net.minecraft.core.registries.BuiltInRegistries.CREATIVE_MODE_TAB, 
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("kashub", name), tab);
    }

    @Override
    public boolean isItemEquipped(net.minecraft.world.entity.player.Player player, net.minecraft.world.item.Item item) {
        if (player == null || item == null) return false;

        // 1. Check Vanilla Inventory (Armor + Hands)
        for (net.minecraft.world.item.ItemStack stack : player.getInventory().armor) {
            if (!stack.isEmpty() && stack.getItem() == item) return true;
        }
        if (player.getMainHandItem().getItem() == item) return true;
        if (player.getOffhandItem().getItem() == item) return true;

        // 2. Check Trinkets (Fabric)
        if (isModLoaded("trinkets")) {
            try {
                Class<?> apiClass = Class.forName("dev.emi.trinkets.api.TrinketsApi");
                java.lang.reflect.Method getComponent = apiClass.getMethod("getTrinketComponent", net.minecraft.world.entity.LivingEntity.class);
                java.util.Optional<?> componentOpt = (java.util.Optional<?>) getComponent.invoke(null, player);
                if (componentOpt.isPresent()) {
                    Object component = componentOpt.get();
                    // Iterate through all slots to find the item
                    java.lang.reflect.Method getInventory = component.getClass().getMethod("getInventory");
                    java.util.Map<?, ?> inventory = (java.util.Map<?, ?>) getInventory.invoke(component);
                    
                    for (Object group : inventory.values()) {
                        // group is Map<String, TrinketInventory>
                        java.util.Map<?, ?> slots = (java.util.Map<?, ?>) group;
                        for (Object trinketInv : slots.values()) {
                            net.minecraft.world.Container container = (net.minecraft.world.Container) trinketInv;
                            for (int i = 0; i < container.getContainerSize(); i++) {
                                if (container.getItem(i).getItem() == item) return true;
                            }
                        }
                    }
                }
            } catch (Exception ignored) {}
        }
        return false;
    }

    @Override
    public Object getComputerCraftPeripheral(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos, net.minecraft.core.Direction side) {
        if (!isModLoaded("computercraft")) return null;

        try {
            // Fabric: CC:T uses legacy API (no capabilities system like NeoForge)
            Class<?> apiClass = Class.forName("dan200.computercraft.api.ComputerCraftAPI");
            java.lang.reflect.Method getPeripheralMethod = apiClass.getMethod("getPeripheralAt",
                net.minecraft.world.level.Level.class,
                net.minecraft.core.BlockPos.class,
                net.minecraft.core.Direction.class);
            
            // Try with specified side first
            if (side != null) {
                Object peripheral = getPeripheralMethod.invoke(null, level, pos, side);
                if (peripheral != null) return peripheral;
            }
            
            // Try all directions
            for (net.minecraft.core.Direction dir : net.minecraft.core.Direction.values()) {
                Object peripheral = getPeripheralMethod.invoke(null, level, pos, dir);
                if (peripheral != null) return peripheral;
            }
            
            // Try with null (no specific side)
            Object peripheral = getPeripheralMethod.invoke(null, level, pos, null);
            if (peripheral != null) return peripheral;
            
        } catch (ClassNotFoundException e) {
            // ComputerCraft not available
        } catch (Exception e) {
            // Log but don't crash
            System.err.println("KHWrapper: Failed to get CC:T peripheral: " + e.getMessage());
        }
        
        return null;
    }

    @Override
    public boolean isClientSide() {
        return net.fabricmc.loader.api.FabricLoader.getInstance().getEnvironmentType() == net.fabricmc.api.EnvType.CLIENT;
    }
}
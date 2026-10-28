package com.kasperstudios.khwrapper.neoforge;

import com.kasperstudios.khwrapper.IPlatformWrapper;
import com.kasperstudios.khwrapper.IKeyBindingHandle;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLPaths;
import java.nio.file.Path;

public class NeoForgePlatform implements IPlatformWrapper {
    @Override
    public Path getConfigDir() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public Path getGameDir() {
        return FMLPaths.GAMEDIR.get();
    }

    @Override
    public Path getModsDir() {
        return FMLPaths.MODSDIR.get();
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public java.util.List<com.kasperstudios.khwrapper.PlatformModInfo> getAllMods() {
        java.util.List<com.kasperstudios.khwrapper.PlatformModInfo> list = new java.util.ArrayList<>();
        for (net.neoforged.neoforgespi.language.IModInfo mod : ModList.get().getMods()) {
            list.add(new com.kasperstudios.khwrapper.PlatformModInfo(
                mod.getModId(),
                mod.getDisplayName(),
                mod.getVersion().toString()
            ));
        }
        return list;
    }

    @Override
    public String getModVersion(String modId) {
        return ModList.get().getModContainerById(modId)
                .map(m -> m.getModInfo().getVersion().toString())
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

        itemStack.getComponents().forEach(component -> {
            String key = net.minecraft.core.registries.BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(component.type()).toString();
            data.put(key, component.value().toString());
        });

        return data;
    }

    @Override
    public void onModInit(Runnable callback) {
        NeoForgeCommonCallbacks.addInitCallback(callback);
    }

    @Override
    public void onClientInit(Runnable callback) {}

    @Override
    public void onClientTick(Runnable callback) {}

    @Override
    public void onClientStopping(Runnable callback) {}

    @Override
    public void setKeyState(String keyId, boolean pressed) {}

    @Override
    public boolean isKeyPressed(int glfwKeyCode) { return false; }

    @Override
    public void sendChat(String message) {}

    @Override
    public void setScreen(Object screen) {}

    @Override
    public IKeyBindingHandle registerKeyBinding(String translationKey, int glfwKeyCode, String category) {
        net.minecraft.client.KeyMapping mapping = new net.minecraft.client.KeyMapping(translationKey, glfwKeyCode, category);
        NeoForgeClientCallbacks.addKeyMapping(mapping);
        return new IKeyBindingHandle() {
            @Override public boolean isPressed() { return mapping.isDown(); }
            @Override public boolean wasPressed() { return mapping.consumeClick(); }
        };
    }

    @Override
    public void registerClientCommand(String name, String description, java.util.function.Consumer<com.kasperstudios.khwrapper.ClientCommandContext> handler) {
        NeoForgeClientCallbacks.commandRegistrations.add(new NeoForgeClientCallbacks.CommandRegistration(name, description, handler));
    }

    @Override
    public void register(com.kasperstudios.khwrapper.NetworkRegistration registration) {
        System.out.println("[KH-DEBUG] Adding network registration: " + registration.id() + " (" + registration.direction() + ")");
        NeoForgeCommonCallbacks.networkRegistrations.add(registration);
    }

    @Override
    public void sendPacket(com.kasperstudios.khwrapper.IPacketPayload payload) {
        NeoForgeNetworkingBootstrap.send(payload);
    }

    @Override
    public void sendPacketToPlayer(net.minecraft.server.level.ServerPlayer player, com.kasperstudios.khwrapper.IPacketPayload payload) {
        NeoForgeNetworkingBootstrap.sendToPlayer(player, payload);
    }

    @Override
    public void onPlayerJoin(Runnable callback) {
        NeoForgeCommonCallbacks.playerJoinCallbacks.add(callback);
    }

    @Override
    public void onPlayerLeave(Runnable callback) {
        NeoForgeCommonCallbacks.playerLeaveCallbacks.add(callback);
    }

    @Override
    public void onServerTick(java.util.function.Consumer<net.minecraft.server.MinecraftServer> callback) {
        NeoForgeCommonCallbacks.serverTickCallbacks.add(callback);
    }

    @Override
    public void openMenu(net.minecraft.server.level.ServerPlayer player, net.minecraft.world.MenuProvider provider) {
        player.openMenu(provider);
    }

    @Override
    public net.minecraft.server.MinecraftServer getServer() {
        return net.neoforged.neoforge.server.ServerLifecycleHooks.getCurrentServer();
    }

    @Override
    public void registerItem(String name, net.minecraft.world.item.Item item) {
        NeoForgeCommonCallbacks.itemRegistrations.add(new NeoForgeCommonCallbacks.ItemRegistration(name, item));
    }

    @Override
    public void registerBlock(String name, net.minecraft.world.level.block.Block block) {
        NeoForgeCommonCallbacks.blockRegistrations.add(new NeoForgeCommonCallbacks.BlockRegistration(name, block));
    }

    @Override
    public void registerBlockEntity(String name, net.minecraft.world.level.block.entity.BlockEntityType<?> type) {
        NeoForgeCommonCallbacks.blockEntityRegistrations.add(new NeoForgeCommonCallbacks.BlockEntityRegistration(name, type));
    }

    @Override
    public void registerCreativeTab(String name, net.minecraft.network.chat.Component title, java.util.function.Supplier<net.minecraft.world.item.ItemStack> icon, java.util.List<? extends net.minecraft.world.item.Item> items) {
        NeoForgeCommonCallbacks.creativeTabRegistrations.add(new NeoForgeCommonCallbacks.CreativeTabRegistration(name, title, icon, items));
    }

    private static Class<?> curiosApiClass = null;
    private static java.lang.reflect.Method getCuriosInventoryMethod = null;
    private static java.lang.reflect.Method findCuriosMethod = null;
    private static boolean curiosReflectionInitialized = false;

    @Override
    public boolean isItemEquipped(net.minecraft.world.entity.player.Player player, net.minecraft.world.item.Item item) {
        if (player == null || item == null) return false;

        net.minecraft.resources.ResourceLocation targetId = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(item);

        // 1. Check Vanilla Inventory (Armor + Hands)
        for (net.minecraft.world.item.ItemStack stack : player.getInventory().armor) {
            if (!stack.isEmpty()) {
                if (net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem()).equals(targetId)) return true;
            }
        }
        if (!player.getMainHandItem().isEmpty() && net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(player.getMainHandItem().getItem()).equals(targetId)) return true;
        if (!player.getOffhandItem().isEmpty() && net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(player.getOffhandItem().getItem()).equals(targetId)) return true;

        // 2. Check Curios (NeoForge)
        if (isModLoaded("curios")) {
            try {
                if (!curiosReflectionInitialized) {
                    curiosApiClass = Class.forName("top.theillusivec4.curios.api.CuriosApi");
                    getCuriosInventoryMethod = curiosApiClass.getMethod("getCuriosInventory", net.minecraft.world.entity.LivingEntity.class);
                    curiosReflectionInitialized = true;
                }

                if (getCuriosInventoryMethod != null) {
                    java.util.Optional<?> inventoryOpt = (java.util.Optional<?>) getCuriosInventoryMethod.invoke(null, player);

                    if (inventoryOpt.isPresent()) {
                        Object handler = inventoryOpt.get();
                        // ICuriosItemHandler.getCurios() -> Map<String, ICurioStacksHandler>
                        java.lang.reflect.Method getCuriosMethod = handler.getClass().getMethod("getCurios");
                        java.util.Map<String, ?> curioMap = (java.util.Map<String, ?>) getCuriosMethod.invoke(handler);

                        for (Object stacksHandler : curioMap.values()) {
                            // ICurioStacksHandler.getStacks() -> IItemHandlerModifiable
                            java.lang.reflect.Method getStacksMethod = stacksHandler.getClass().getMethod("getStacks");
                            net.neoforged.neoforge.items.IItemHandlerModifiable stacks = (net.neoforged.neoforge.items.IItemHandlerModifiable) getStacksMethod.invoke(stacksHandler);

                            for (int i = 0; i < stacks.getSlots(); i++) {
                                net.minecraft.world.item.ItemStack stack = stacks.getStackInSlot(i);
                                if (!stack.isEmpty()) {
                                    net.minecraft.resources.ResourceLocation stackId = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem());
                                    if (stackId.equals(targetId)) {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public Object getComputerCraftPeripheral(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos, net.minecraft.core.Direction side) {
        net.minecraft.world.level.block.state.BlockState state = level.getBlockState(pos);
        String blockId = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();
        
        net.minecraft.world.level.block.entity.BlockEntity be = level.getBlockEntity(pos);
        if (be == null) {
            // Try neighbors (for multi-blocks)
            for (net.minecraft.core.Direction dir : net.minecraft.core.Direction.values()) {
                be = level.getBlockEntity(pos.relative(dir));
                if (be != null) {
                    break;
                }
            }
        }

        if (be == null) return null;
        
        if (!isModLoaded("computercraft")) {
            return be; // Still return BE even if CC not loaded, for generic scanning
        }

        try {
            // Strategy 1: Standard CC:T Discovery
            try {
                Class<?> lookupClass = Class.forName("dan200.computercraft.api.peripheral.PeripheralLookup");
                java.lang.reflect.Method getMethod = lookupClass.getMethod("get", 
                        net.minecraft.world.level.Level.class, 
                        net.minecraft.core.BlockPos.class, 
                        net.minecraft.core.Direction.class);
                
                Object result = getMethod.invoke(null, level, pos, null);
                if (result != null) return result;
                
                for (net.minecraft.core.Direction dir : net.minecraft.core.Direction.values()) {
                    result = getMethod.invoke(null, level, pos, dir);
                    if (result != null) return result;
                }
            } catch (Exception ignored) {}

            // Strategy 2: Capability Search
            for (net.neoforged.neoforge.capabilities.BlockCapability<?, ?> bc : 
                    net.neoforged.neoforge.capabilities.BlockCapability.getAll()) {
                if (bc.name().toString().contains("peripheral")) {
                    try {
                        @SuppressWarnings("unchecked")
                        net.neoforged.neoforge.capabilities.BlockCapability<Object, net.minecraft.core.Direction> typedCap =
                            (net.neoforged.neoforge.capabilities.BlockCapability<Object, net.minecraft.core.Direction>) bc;
                        Object res = level.getCapability(typedCap, pos, level.getBlockState(pos), be, null);
                        if (res != null) return res;
                    } catch (Exception ignored) {}
                }
            }

            // Strategy 3: Omnivore Mode
            return be;

        } catch (Exception e) {
            System.err.println("KHWrapper: Peripheral scan error: " + e.getMessage());
        }
        return be;
    }

    @Override
    public boolean isClientSide() {
        return net.neoforged.fml.loading.FMLEnvironment.dist.isClient();
    }
}
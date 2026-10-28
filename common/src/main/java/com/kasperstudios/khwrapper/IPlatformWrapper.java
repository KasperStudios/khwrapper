package com.kasperstudios.khwrapper;

import java.nio.file.Path;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Interface defining platform-specific operations that differ between Fabric and NeoForge.
 */
public interface IPlatformWrapper {
    Path getConfigDir();
    Path getGameDir();
    Path getModsDir();
    boolean isModLoaded(String modId);
    String getModVersion(String modId);
    java.util.List<PlatformModInfo> getAllMods();

    // Registry Access
    Object getBlock(String id);
    Object getItem(String id);
    String getBlockId(Object block);
    String getItemId(Object item);
    java.util.Map<String, Object> getItemData(Object stack);
    
    // Core Lifecycle
    void onModInit(Runnable callback);
    void onClientInit(Runnable callback);
    void onClientTick(Runnable callback);
    void onClientStopping(Runnable callback);

    // Client-side interactions (Safe signatures)
    void setKeyState(String keyId, boolean pressed);
    boolean isKeyPressed(int glfwKeyCode);
    void sendChat(String message);
    void setScreen(Object screen);
    IKeyBindingHandle registerKeyBinding(String translationKey, int glfwKeyCode, String category);
    void registerClientCommand(String name, String description, java.util.function.Consumer<ClientCommandContext> handler);

    // Networking
    void register(NetworkRegistration registration);
    void sendPacket(IPacketPayload payload);
    void sendPacketToPlayer(net.minecraft.server.level.ServerPlayer player, IPacketPayload payload);

    // Connection Events
    void onPlayerJoin(Runnable callback);
    void onPlayerLeave(Runnable callback);

    // Server Lifecycle
    void onServerTick(java.util.function.Consumer<net.minecraft.server.MinecraftServer> callback);
    net.minecraft.server.MinecraftServer getServer();

    // UI & Containers
    void openMenu(net.minecraft.server.level.ServerPlayer player, net.minecraft.world.MenuProvider provider);

    // Registration
    void registerItem(String name, net.minecraft.world.item.Item item);
    void registerBlock(String name, net.minecraft.world.level.block.Block block);
    void registerBlockEntity(String name, net.minecraft.world.level.block.entity.BlockEntityType<?> type);

    // Creative Tabs
    void registerCreativeTab(String name, net.minecraft.network.chat.Component title, java.util.function.Supplier<net.minecraft.world.item.ItemStack> icon, java.util.List<? extends net.minecraft.world.item.Item> items);

    // Accessories & Equipment
    boolean isItemEquipped(net.minecraft.world.entity.player.Player player, net.minecraft.world.item.Item item);

    // ComputerCraft Integration (Optional)
    /**
     * Attempts to get a ComputerCraft peripheral at the given position.
     * Returns null if ComputerCraft is not loaded or no peripheral exists.
     * 
     * @param level The world/level
     * @param pos Block position
     * @param side Direction to check (null for any side)
     * @return IPeripheral instance or null
     */
    Object getComputerCraftPeripheral(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos, net.minecraft.core.Direction side);
    /** Returns true if running on the client side (can be integrated server or dedicated client). */
    boolean isClientSide();
}
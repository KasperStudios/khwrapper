package com.kasperstudios.khwrapper.neoforge;

import java.util.ArrayList;
import java.util.List;

public class NeoForgeCommonCallbacks {
    public static final List<Runnable> initCallbacks = new ArrayList<>();
    public static final List<java.util.function.Consumer<net.minecraft.server.MinecraftServer>> serverTickCallbacks = new ArrayList<>();
    
    // Structured Networking
    public static final List<com.kasperstudios.khwrapper.NetworkRegistration> networkRegistrations = new ArrayList<>();
    
    // Events
    public static final List<Runnable> playerJoinCallbacks = new ArrayList<>();
    public static final List<Runnable> playerLeaveCallbacks = new ArrayList<>();

    // Items and Blocks
    public record ItemRegistration(String name, net.minecraft.world.item.Item item) {}
    public record BlockRegistration(String name, net.minecraft.world.level.block.Block block) {}
    public record BlockEntityRegistration(String name, net.minecraft.world.level.block.entity.BlockEntityType<?> type) {}
    public static final List<ItemRegistration> itemRegistrations = new ArrayList<>();
    public static final List<BlockRegistration> blockRegistrations = new ArrayList<>();
    public static final List<BlockEntityRegistration> blockEntityRegistrations = new ArrayList<>();
    
    // Creative Tabs
    public record CreativeTabRegistration(String name, net.minecraft.network.chat.Component title, java.util.function.Supplier<net.minecraft.world.item.ItemStack> icon, java.util.List<? extends net.minecraft.world.item.Item> items) {}
    public static final List<CreativeTabRegistration> creativeTabRegistrations = new ArrayList<>();

    public static void addInitCallback(Runnable callback) {
        initCallbacks.add(callback);
    }
}

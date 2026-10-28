package com.kasperstudios.khwrapper.neoforge;

import net.neoforged.bus.api.SubscribeEvent;

public class NeoForgeModBusHandlers {

    @SubscribeEvent
    public static void onRegisterItems(net.neoforged.neoforge.registries.RegisterEvent event) {
        if (event.getRegistryKey().equals(net.minecraft.core.registries.Registries.ITEM)) {
            System.out.println("DEBUG: NeoForge registering items, count: " + NeoForgeCommonCallbacks.itemRegistrations.size());
            for (NeoForgeCommonCallbacks.ItemRegistration reg : NeoForgeCommonCallbacks.itemRegistrations) {
                System.out.println("DEBUG: Registering item: kashub:" + reg.name());
                event.register(net.minecraft.core.registries.Registries.ITEM, 
                    net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("kashub", reg.name()), 
                    () -> reg.item());
            }
        }
    }

    @SubscribeEvent
    public static void onRegisterBlocks(net.neoforged.neoforge.registries.RegisterEvent event) {
        if (event.getRegistryKey().equals(net.minecraft.core.registries.Registries.BLOCK)) {
            for (NeoForgeCommonCallbacks.BlockRegistration reg : NeoForgeCommonCallbacks.blockRegistrations) {
                event.register(net.minecraft.core.registries.Registries.BLOCK, 
                    net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("kashub", reg.name()), 
                    () -> reg.block());
            }
        }
    }

    @SubscribeEvent
    public static void onRegisterBlockEntities(net.neoforged.neoforge.registries.RegisterEvent event) {
        if (event.getRegistryKey().equals(net.minecraft.core.registries.Registries.BLOCK_ENTITY_TYPE)) {
            for (NeoForgeCommonCallbacks.BlockEntityRegistration reg : NeoForgeCommonCallbacks.blockEntityRegistrations) {
                event.register(net.minecraft.core.registries.Registries.BLOCK_ENTITY_TYPE, 
                    net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("kashub", reg.name()), 
                    () -> reg.type());
            }
        }
    }

    @SubscribeEvent
    public static void onRegisterCreativeTabs(net.neoforged.neoforge.registries.RegisterEvent event) {
        if (event.getRegistryKey().equals(net.minecraft.core.registries.Registries.CREATIVE_MODE_TAB)) {
            System.out.println("DEBUG: NeoForge registering creative tabs, count: " + NeoForgeCommonCallbacks.creativeTabRegistrations.size());
            for (NeoForgeCommonCallbacks.CreativeTabRegistration reg : NeoForgeCommonCallbacks.creativeTabRegistrations) {
                System.out.println("DEBUG: Registering creative tab: kashub:" + reg.name());
                event.register(net.minecraft.core.registries.Registries.CREATIVE_MODE_TAB,
                    net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("kashub", reg.name()),
                    () -> net.minecraft.world.item.CreativeModeTab.builder()
                        .title(reg.title())
                        .icon(reg.icon())
                        .displayItems((params, output) -> {
                            System.out.println("DEBUG: Filling creative tab: kashub:" + reg.name() + " with " + reg.items().size() + " items");
                            for (net.minecraft.world.item.Item item : reg.items()) {
                                output.accept(item);
                            }
                        })
                        .build());
            }
        }
    }

    @SubscribeEvent
    public static void onBuildCreativeTabContents(net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent event) {
        // This is an alternative way to add items to existing tabs,
        // but since we create our own tab above, we handle it there.
        // However, if we wanted to add to Vanilla tabs, we would do it here.
    }
}

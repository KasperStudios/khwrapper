package com.kasperstudios.khwrapper.fabric;

import net.fabricmc.api.ModInitializer;
import com.kasperstudios.khwrapper.KHWrapper;

public final class KHWrapperFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // Initialize the platform wrapper for Fabric
        KHWrapper.init(new FabricPlatform());
    }
}

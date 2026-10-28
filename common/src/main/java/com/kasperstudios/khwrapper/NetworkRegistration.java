package com.kasperstudios.khwrapper;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Unified model for network packet registration.
 */
public record NetworkRegistration(
    String id,
    PacketDirection direction,
    Function<IPacketBuffer, IPacketPayload> decoder,
    BiConsumer<IPacketPayload, IPacketContext> handler,
    String version
) {
    /**
     * Builder-like constructor for common use cases.
     */
    public static NetworkRegistration s2c(String id, Function<IPacketBuffer, IPacketPayload> decoder, BiConsumer<IPacketPayload, IPacketContext> handler) {
        return new NetworkRegistration(id, PacketDirection.S2C, decoder, handler, "1");
    }

    public static NetworkRegistration c2s(String id, Function<IPacketBuffer, IPacketPayload> decoder, BiConsumer<IPacketPayload, IPacketContext> handler) {
        return new NetworkRegistration(id, PacketDirection.C2S, decoder, handler, "1");
    }

    public static NetworkRegistration bidirectional(String id, Function<IPacketBuffer, IPacketPayload> decoder, BiConsumer<IPacketPayload, IPacketContext> handler) {
        return new NetworkRegistration(id, PacketDirection.BIDIRECTIONAL, decoder, handler, "1");
    }
}

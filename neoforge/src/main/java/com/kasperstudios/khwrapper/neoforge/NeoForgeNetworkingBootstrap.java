package com.kasperstudios.khwrapper.neoforge;

import com.kasperstudios.khwrapper.IPacketBuffer;
import com.kasperstudios.khwrapper.IPacketContext;
import com.kasperstudios.khwrapper.IPacketPayload;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.fml.loading.FMLEnvironment;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class NeoForgeNetworkingBootstrap {

    private static final java.util.Set<String> REGISTERED_IDS = java.util.Collections.synchronizedSet(new java.util.HashSet<>());
    private static final Map<ResourceLocation, CustomPacketPayload.Type<NeoForgeCustomPayload>> TYPE_CACHE = new ConcurrentHashMap<>();

    private static CustomPacketPayload.Type<NeoForgeCustomPayload> getType(ResourceLocation rl) {
        return TYPE_CACHE.computeIfAbsent(rl, CustomPacketPayload.Type::new);
    }

    @SubscribeEvent
    public static void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
        System.out.println("[KH-DEBUG] onRegisterPayloads event received!");
        
        java.util.Map<String, com.kasperstudios.khwrapper.NetworkRegistration> uniqueRegs = new java.util.HashMap<>();
        for (com.kasperstudios.khwrapper.NetworkRegistration reg : NeoForgeCommonCallbacks.networkRegistrations) {
            uniqueRegs.put(reg.id(), reg);
        }

        for (com.kasperstudios.khwrapper.NetworkRegistration reg : uniqueRegs.values()) {
            if (REGISTERED_IDS.contains(reg.id())) continue;

            ResourceLocation rl = ResourceLocation.parse(reg.id());
            final PayloadRegistrar registrar = event.registrar(reg.version());

            System.out.println("[KH-DEBUG] Registering packet: " + reg.id() + " (dir: " + reg.direction() + ")");

            CustomPacketPayload.Type<NeoForgeCustomPayload> type = getType(rl);
            StreamCodec<RegistryFriendlyByteBuf, NeoForgeCustomPayload> codec = StreamCodec.of(
                (buf, value) -> value.payload().write(new NeoForgePacketBuffer(buf)),
                buf -> new NeoForgeCustomPayload(reg.decoder().apply(new NeoForgePacketBuffer(buf)))
            );

            if (reg.direction() == com.kasperstudios.khwrapper.PacketDirection.BIDIRECTIONAL) {
                registrar.playBidirectional(type, codec, (payload, context) -> {
                    if (reg.handler() != null) {
                        System.out.println("[KH-DEBUG] Handling BIDIRECTIONAL packet: " + rl);
                        reg.handler().accept(payload.payload(), new NeoForgePacketContext(context));
                    }
                });
            } else if (reg.direction() == com.kasperstudios.khwrapper.PacketDirection.S2C) {
                registrar.playToClient(type, codec, (payload, context) -> {
                    if (reg.handler() != null) {
                        System.out.println("[KH-DEBUG] Handling S2C packet: " + rl);
                        reg.handler().accept(payload.payload(), new NeoForgePacketContext(context));
                    }
                });
            } else if (reg.direction() == com.kasperstudios.khwrapper.PacketDirection.C2S) {
                registrar.playToServer(type, codec, (payload, context) -> {
                    if (reg.handler() != null) {
                        System.out.println("[KH-DEBUG] Handling C2S packet: " + rl);
                        reg.handler().accept(payload.payload(), new NeoForgePacketContext(context));
                    }
                });
            }
            
            REGISTERED_IDS.add(reg.id());
        }
    }

    public static void send(IPacketPayload payload) {
        if (FMLEnvironment.dist.isClient()) {
            ResourceLocation rl = ResourceLocation.parse(payload.getPacketId());
            System.out.println("[KH-DEBUG] Client sending packet: " + rl);
            net.neoforged.neoforge.network.PacketDistributor.sendToServer(new NeoForgeCustomPayload(payload));
        }
    }

    public static void sendToPlayer(net.minecraft.server.level.ServerPlayer player, IPacketPayload payload) {
        if (player != null) {
            ResourceLocation rl = ResourceLocation.parse(payload.getPacketId());
            System.out.println("[KH-DEBUG] Server sending packet " + rl + " to " + player.getScoreboardName());
            net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(player, new NeoForgeCustomPayload(payload));
        }
    }

    private record NeoForgeCustomPayload(IPacketPayload payload) implements CustomPacketPayload {
        @Override
        public Type<? extends CustomPacketPayload> type() {
            return getType(ResourceLocation.parse(payload.getPacketId()));
        }
    }

    private static class NeoForgePacketContext implements IPacketContext {
        private final IPayloadContext context;
        public NeoForgePacketContext(IPayloadContext context) { this.context = context; }
        @Override public void execute(Runnable task) { context.enqueueWork(task); }
        @Override public net.minecraft.server.level.ServerPlayer getPlayer() {
            return context.player() instanceof net.minecraft.server.level.ServerPlayer p ? p : null;
        }
    }

    private static class NeoForgePacketBuffer implements IPacketBuffer {
        private final RegistryFriendlyByteBuf buf;
        public NeoForgePacketBuffer(RegistryFriendlyByteBuf buf) { this.buf = buf; }
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
}

package com.kasperstudios.khwrapper;

/**
 * Context for handling received packets.
 */
public interface IPacketContext {
    /**
     * Executes a task on the main game thread.
     */
    void execute(Runnable task);

    /**
     * Gets the player associated with this context (server-side only).
     */
    net.minecraft.server.level.ServerPlayer getPlayer();
}

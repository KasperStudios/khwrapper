package com.kasperstudios.khwrapper;

/**
 * Interface for platform-agnostic packet payloads.
 */
public interface IPacketPayload {
    /**
     * @return Unique identifier for this packet type.
     */
    String getPacketId();

    /**
     * Writes the payload data to the buffer.
     */
    void write(IPacketBuffer buffer);
}

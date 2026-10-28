package com.kasperstudios.khwrapper;

/**
 * Platform-agnostic packet buffer for reading and writing network data.
 */
public interface IPacketBuffer {
    void writeString(String value);
    String readString();
    
    void writeInt(int value);
    int readInt();
    
    void writeLong(long value);
    long readLong();
    
    void writeBoolean(boolean value);
    boolean readBoolean();
    
    void writeBytes(byte[] value);
    byte[] readBytes();
    
    void writeDouble(double value);
    double readDouble();
    
    void writeFloat(float value);
    float readFloat();
}

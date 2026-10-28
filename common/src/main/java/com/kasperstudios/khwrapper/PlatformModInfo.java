package com.kasperstudios.khwrapper;

public class PlatformModInfo {
    private final String id;
    private final String name;
    private final String version;

    public PlatformModInfo(String id, String name, String version) {
        this.id = id;
        this.name = name;
        this.version = version;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getVersion() { return version; }
}

# KHWrapper DevNotes

> IMPORTANT: This file contains critical project rules, implementation context, and constraints for the author and AI assistants.
> Treat this file as the operational contract of the project.
> If something is unclear, prefer existing code/project patterns over invention.

---

## 1. Project Overview

### What this project is
A cross-platform abstraction library (bridge) for KasHub, designed using the Architectury API. It acts as a mediator between the platform-independent core logic of KasHub and the specific APIs of Minecraft loaders (Fabric and NeoForge).

### Core goal
Provide a stable, thin compatibility layer for Minecraft and Loader-specific APIs, allowing KasHub core logic to remain 100% identical across all supported platforms.

### Success criteria
- KasHub core can be compiled against KHWrapper without any platform-specific imports (net.fabricmc.* or neoforge.*).
- Seamless switching between Fabric and NeoForge by simply swapping the platform-specific implementation of the wrapper.
- Minimal performance overhead (direct proxying).

### Project type
- [x] Library / framework
- [x] Game / mod (Bridge mod)

---

## 2. Current Status

### Status summary
Active development. Initial bridge core is verified and stable for basic platform operations.

### Implemented
- [x] Basic Architectury project structure (common, fabric, neoforge).
- [x] Verified build on both Fabric and NeoForge platforms.
- [x] Migration to Mojang Mappings (Mojmap) for universal compatibility.
- [x] Core IPlatformWrapper contract and KHWrapper static accessor.
- [x] FabricPlatform implementation using Fabric Loader API.
- [x] NeoForgePlatform implementation using FML/NeoForge API.

### In progress
- [ ] Preparation for networking abstraction (Packet management).
- [ ] Researching uniform Event registration bridge (Ticks, Commands).

### Planned / TODO
- [ ] Integration with KasHub core.
- [ ] Automatic JAR build and library distribution logic.
- [ ] Expansion of wrapper to support UI opening bridge.

---

## 3. Tech Stack

### Core stack
- Runtime: Java 21 (Minecraft 1.21+)
- Language(s): Java
- Framework(s): Architectury API
- Build tools: Gradle (Architectury Loom)

### Versions
- Minecraft: 1.21.1
- NeoForge: 21.1.227
- Fabric Loader: 0.19.2
- Architectury API: 13.0.8+

### Environment notes
- OS: Windows / Linux / cross-platform
- Required tools: git, java 21, gradle
- Special setup notes: 
    - **Mappings**: Project uses Mojang Mappings (Mojmap). Use `Minecraft` class instead of `MinecraftClient`.
    - **Build Verification**: Last successful full build (common, fabric, neoforge) confirmed on 2026-04-30.
    - **API Surface**: Initial confirmed methods: `getConfigDir`, `getGameDir`, `isModLoaded`, `getModVersion`, `setKeyState`.

---

## 4. Architecture Notes

### High-level architecture
KHWrapper follows the **Bridge Pattern**. It defines a set of interfaces in the `common` module and provides platform-specific implementations in the `fabric` and `neoforge` modules.

### Main modules / layers
1. **common** - Contains interfaces (`IPlatformWrapper`), static accessor (`KHWrapper`), and platform-independent utilities.
2. **fabric** - Implementation of the wrapper using Fabric API and Fabric Loader.
3. **neoforge** - Implementation of the wrapper using NeoForge API.

### Dependency direction
```txt
[KasHub Core] -> [KHWrapper:common]
[KHWrapper:fabric] -> [KHWrapper:common]
[KHWrapper:neoforge] -> [KHWrapper:common]
```

### Important boundaries
- **KHWrapper must NOT contain any KasHub-specific business logic** (no pathfinding algorithms, no interpreter code). It only provides access to Minecraft/Loader systems.
- KasHub core must NOT import any loader-specific classes directly.

---

## 5. Project Structure

```txt
khwrapper/
├── common/        # Interfaces and shared logic
├── fabric/        # Fabric-specific implementation
├── neoforge/      # NeoForge-specific implementation
└── build.gradle   # Multi-module build configuration
```

---

## 8. Critical Rules

1. **Keep it Thin**: KHWrapper should only wrap what is strictly necessary to maintain cross-platform compatibility.
2. **No Business Logic**: Do not add "features" to the wrapper. It is a bridge, not a feature module.
3. **Vanilla Types Preferred**: Use vanilla Minecraft types (BlockPos, World, Entity) in the interface whenever possible, as they are mostly consistent across platforms via Mojmap.
4. **No Structural Drift**: Maintain the Architectury standard structure. Do not rename `common`, `fabric`, or `neoforge` modules.

---

## 11. API / Integration Notes

### Initialization flow
1. Mod starts (Fabric/NeoForge entry point).
2. Platform-specific implementation of `IPlatformWrapper` is instantiated.
3. `KHWrapper.init(implementation)` is called.
4. KasHub core logic is initialized, using `KHWrapper.get()` for all platform interactions.

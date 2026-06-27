# ApertureCraft — AGENTS.md

Fabric mod for Minecraft 1.20.4 adding Portal (game) content. Java 17, Gradle (Fabric Loom 1.7).

## Commands

| Command | Purpose |
|---|---|
| `./gradlew build` | Build the mod JAR |
| `./gradlew runClient` | Launch Minecraft client with the mod |
| `./gradlew runServer` | Launch dedicated server |
| `./gradlew genSources` | Generate Yarn-mapped sources |
| `./gradlew runDatagenClient` | Run Fabric data generation |

Use `gradlew.bat` on Windows, `./gradlew` on Linux/macOS. VSCode launch configs are at `.vscode/launch.json` (Client, Server, Data Generation).

## Entrypoints

- **Common init**: `com.cemi.ApertureCraft` — registers blocks, items, entities, fluids, particles, networking, commands, config
- **Client init**: `com.cemi.client.ApertureCraftClient` — renders, shaders, particles, fluids, colors, input, models
- **Datagen**: `com.cemi.ApertureCraftDataGenerator` (stub — data gen output goes to `src/main/generated`)

## Key architecture

- **Mod ID**: `aperturecraft`, maven group `com.cemi`
- **Blocks**: `com.cemi.block` + `com.cemi.block.entity`
- **Items**: `com.cemi.item` (portal gun, storage cube, etc.)
- **Entities**: `com.cemi.entity` (turrets, rockets, cubes, radio, portals, HEPs)
- **Fluids**: `com.cemi.fluid` (neurotoxin)
- **Mixins**: `com.cemi.mixin` (common), `com.cemi.client.mixin` (client-only, in `aperturecraft.client.mixins.json`)
- **Config**: Cloth Config via `com.cemi.config.ApertureConfig` (mod menu support)
- **Shaders**: Custom portal shader registered via `CoreShaderRegistrationCallback`
- **Access widener**: `src/main/resources/aperturecraft.accesswidener` — exposes `RenderPhase` inner classes, `RenderLayer.MultiPhaseParameters`, `LineWidth`
- **World data**: `com.cemi.world` — portal state, channel data, todo notes persisted per-world
- **GeckoLib 4.4.4**: Used for animated entity/item models (geo.json + model classes)
- **Immersive Portals**: Integration via `com.github.iPortalTeam:ImmersivePortalsMod:v5.1.0-mc1.20.4`

## Dependencies (from `gradle.properties`)

- Fabric API 0.97.1+1.20.4
- Fabric Loader ≥0.15.11
- Yarn mappings 1.20.4+build.3
- GeckoLib 4.4.4
- Cloth Config 13.0.121

## Important constraints

- No test infrastructure exists — don't look for or assume tests
- No CI workflows configured
- Data generator class is a stub; generated assets would go to `src/main/generated`
- `run/` directory is gitignored (contains local Minecraft runtime)
- Build cache at `.gradle/loom-cache/` (not committed)

## Project
Minecraft 1.20.4 Fabric mod recreating the mechanics, gameplay, and aesthetics of Portal 1 and Portal 2 while fitting naturally into Minecraft.

## Goals
- High-quality implementation over quick hacks.
- Vanilla-friendly performance.
- Multiplayer support.
- Modular, maintainable code.
- Feature parity with Portal games where practical.

## Tech Stack
- Minecraft 1.20.4
- Fabric API
- Java 17
- Gradle

## Core Features
- Portal Gun
- Linked portals
- Accurate portal placement on valid surfaces
- Momentum conservation ("speedy thing goes in...")
- Physics objects
- Weighted cubes
- Buttons
- Energy pellets
- Excursion funnels
- Faith plates
- Pneumatic tubes
- Laser emitters/relays/catchers
- Gels (repulsion, propulsion, conversion)
- Aperture-style blocks and doors
- Test chamber elements
- Portal-style sounds, particles, and animations

## Code Guidelines
- Keep classes focused and small.
- Prefer composition over inheritance.
- Avoid duplicated logic.
- Document non-obvious algorithms.
- Never break existing APIs without updating usages.
- Minimize mixins; inject only where necessary.

## Performance
- Avoid allocations every tick.
- Cache expensive calculations.
- Client-only rendering must stay client-side.
- Server remains authoritative for gameplay.

## AI Agent Rules
- Understand existing architecture before modifying it.
- Reuse utilities instead of rewriting them.
- Keep commits small and localized.
- Preserve backward compatibility when possible.
- Do not add dependencies unless clearly beneficial.
- If uncertain, leave TODO comments rather than guessing.

## Priority Order
1. Stable portals
2. Correct momentum physics
3. Multiplayer synchronization
4. Remaining Portal mechanics
5. Visual polish
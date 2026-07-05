# ApertureCraft

**Now you're thinking with portals!**

A Fabric mod for Minecraft 1.20.4 that recreates the mechanics, gameplay, and aesthetics of Portal 1 and Portal 2, fitting naturally into Minecraft.

## Features

- **Portal Gun** — fire and place linked orange/blue portals on valid surfaces
- **Momentum Conservation** — "speedy thing goes in, speedy thing comes out"
- **Physics Objects** — weighted storage cubes, companion cube, radio
- **Turrets** — animated, GeckoLib-driven turret entities
- **Rockets & Rocket Turrets** — explosive projectiles and rocket turrets
- **High Energy Pellets (HEPs)** — launcher, catcher, and projectile system
- **Neurotoxin Fluid** — custom fluid with particles and damage
- **Interactive Blocks** — floor buttons, pedestal buttons, doors, cube droppers, fizzlers, indicator lights
- **Aperture-Style Blocks** — concrete, metal tiles, glass, slopes, large tiles and columns
- **Custom Portal Shader** — stencil/framebuffer-based portal rendering
- **Custom Animations** — GeckoLib-powered models for all entities and blocks
- **Multiplayer Ready** — server-authoritative gameplay, synced world data
- **Configurable** — Cloth Config / Mod Menu support

## Planned Features

- Faith plates (excursion funnels)
- Gels (repulsion, propulsion, conversion)
- Pneumatic tubes
- Laser emitters / relays / catchers
- Test chamber elements
- More Portal 2 content

## Requirements

- Minecraft 1.20.4
- Fabric Loader >=0.15.11
- Fabric API 0.97.1+
- Java 17+

## Dependencies

| Dependency | Version |
|---|---|
| Fabric API | 0.97.1+1.20.4 |
| GeckoLib | 4.4.4 |
| Cloth Config | 13.0.121 |
| Immersive Portals | v5.1.0-mc1.20.4 |

## Building

```bash
./gradlew build
```

The mod JAR will be output to `build/libs/`.

## Running

```bash
./gradlew runClient   # Launch Minecraft with the mod
./gradlew runServer   # Launch a dedicated server
./gradlew runDatagenClient   # Run Fabric data generation
```

Use `gradlew.bat` on Windows.

## Development

- **Common init**: `com.cemi.ApertureCraft` — registers blocks, items, entities, fluids, particles, networking, commands, config
- **Client init**: `com.cemi.client.ApertureCraftClient` — renders, shaders, particles, fluids, colors, input, models
- **Data generation**: `com.cemi.ApertureCraftDataGenerator` (stub — generated assets go to `src/main/generated`)

Key source packages:

| Package | Contents |
|---|---|
| `block/` | All Aperture blocks and block entities |
| `entity/` | Portal, turret, rocket, cube, radio, HEP entities |
| `item/` | Portal gun, items, creative tabs |
| `client/` | Rendering, models, shaders, input, client mixins |
| `mixin/` | Common Minecraft mixins |
| `fluid/` | Neurotoxin fluid |
| `world/` | Portal state, channel data, world-saved data |
| `networking/` | Custom packet handling |
| `sound/` | Sound events and looping sounds |
| `config/` | Cloth Config integration |

## Project Structure

```
ApertureCraft/
├── build.gradle              # Fabric Loom build
├── gradle.properties         # Version and dependency settings
├── settings.gradle           # Plugin repositories
├── gradlew / gradlew.bat     # Gradle wrappers
├── LICENSE                   # CC0 1.0 Universal
├── src/
│   ├── main/
│   │   ├── java/com/cemi/    # Mod source code
│   │   └── resources/        # Assets, data, mixins, access widener
│   └── generated/            # Data generator output (future)
└── .vscode/                  # VSCode launch configs
```

## License

CC0 1.0 Universal — see [LICENSE](LICENSE).

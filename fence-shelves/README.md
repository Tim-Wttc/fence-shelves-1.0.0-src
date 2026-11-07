# Fence Shelves

A Fabric mod for **Minecraft 26.3** that makes every shelf behave like a fence.

- **Can't be jumped on or over.** Shelf collision is raised to fence height (1.5 blocks), keeping the
  shelf's own footprint. Players, and mobs that can't clear a fence, are stopped the same way.
- **Recognised as fences by entities.** All shelves are added to `#minecraft:fences`, which is what
  pathfinding (`PathType.FENCE`), the "standing on a fence" logic and similar vanilla checks look at.

## How it works

| Piece | File | What it does |
|---|---|---|
| Data tag | `data/minecraft/tags/block/fences.json` | Adds `#minecraft:wooden_shelves` (all 13 vanilla shelves) to `#minecraft:fences`. |
| Collision mixin | `BlockStateBaseMixin` | For any `ShelfBlock` that is in `#minecraft:fences`, extends its collision box up to 1.5 blocks. |
| Cosmetic mixin | `FenceBlockMixin` | Stops nether brick fences from growing an arm into a shelf (they would otherwise treat it as "the same fence type"). |

The collision change is tied to the tag on purpose: remove a shelf from `#minecraft:fences` in a data pack
and it goes back to vanilla behaviour. Modded shelves that extend `ShelfBlock` get the collision as soon as
you add them to the tag (most mods already put theirs in `#minecraft:wooden_shelves`).

## Install

Needs Minecraft 26.3, Fabric Loader 0.19.5 or newer and Java 25. It does **not** need Fabric API.

Install it on **both the server and the clients**. If only the server has it, players' movement
prediction won't match and they will rubber-band against shelves.

## Build

```
./gradlew build
```

The jar ends up in `build/libs/fence-shelves-1.0.0.jar` (use the one without `-sources`).
Versions come from the official `fabric-example-mod` `26.3` branch (Loom `1.19-SNAPSHOT`, Gradle 9.8.2,
Fabric Loader 0.20.1); see <https://fabricmc.net/develop> if you need to bump them.

## Side effects worth knowing

These all come with being in `#minecraft:fences`, i.e. they are what "behaves like a fence" means in vanilla:

- Leads can be tied to shelves (a leash knot appears on the shelf).
- Arrows and other projectiles stop at the invisible part above the shelf, just like above a fence.
- Command selectors, predicates and other data that read `#minecraft:fences` now include shelves.

## Customising

- **Different height:** change `FENCE_COLLISION_HEIGHT` in `FenceShelves.java`.
- **Opt shelves out:** remove them from `data/minecraft/tags/block/fences.json` and rebuild. Both the
  fence recognition and the raised collision follow that tag.

## Not verified

This was written without being able to compile or launch the game, because the Fabric Maven and the
Minecraft jars weren't reachable from where it was produced. The Gradle setup is copied from the official
26.3 template, and the two mixin target signatures (`BlockStateBase#getCollisionShape` and
`FenceBlock#connectsTo`) were checked against published Mojang mappings for 1.21.5, not 26.3 itself.
The first `./gradlew build` and `./gradlew runClient` on your machine is the real test.

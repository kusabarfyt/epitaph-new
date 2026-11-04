# Mace Epitaph (Fabric, Minecraft 1.21.11)

Plays the Dusk's Epitaph death sequence (ground marker, chains, sky beam, explosion + sounds)
where a player dies when **you kill them with a mace**.

* Client-side only: nothing to install on the server, works on any server and in singleplayer.
* Needs **Fabric API** and **GeckoLib 5.4.x for 1.21.11** in your mods folder.
* `/maceepitaph` plays the animation 6 blocks in front of you, for testing without a kill.

## Build it on GitHub
1. Create a new GitHub repo and upload everything in this folder, **including the hidden `.github` folder**
   (if your file picker skips it, create `.github/workflows/build.yml` on the GitHub website and paste the file in).
2. Open the repo's **Actions** tab, open the latest "build" run.
3. Download the **mace-epitaph-jar** artifact at the bottom (it contains the jar) and unzip it into your mods folder.
4. If the run fails, open the failed step and copy the red error lines. Most first-build problems are one-line
   fixes in the files listed below.

## Most likely first-build fixes
| Error | Fix |
|---|---|
| `cannot find symbol: Identifier` | Mojang names it `ResourceLocation` in your mappings. Change it in `MaceEpitaph.java` only. |
| `package software.bernie.geckolib.animatable.manager does not exist` | GeckoLib moved `AnimatableManager`; change that import in `EpitaphEffectEntity.java`. |
| `EntityRendererRegistry` not found / deprecated | Different Fabric API name for registering entity renderers; edit `MaceEpitaphClient.java`. |
| GeckoLib artifact not found | See the comment in `build.gradle` (Modrinth maven fallback). |

## Tuning the look
* Timings (when the beam sound and the explosion burst happen) are constants at the top of `EpitaphEffect.java`.
* Which animation each effect plays and how long it lives: `EpitaphEffectEntity.java`.
* Kill detection window (how long after your last hit a death still counts): `MaceKillWatcher.java`.

## Where the assets came from
Models, animations, textures and sounds are from the original `charter_weapons` assets, moved into the
GeckoLib 5 layout and renamed to match the entity ids:

| Original | Now |
|---|---|
| `geo/beam.geo.json`, `animations/beam.animation.json`, `textures/entities/beam.png` | `epitaph_beam` |
| chains | `epitaph_chains` |
| marker (`divinity_marker.png`) | `epitaph_marker` |
| particle_thingy (`particethingy.png`) | `epitaph_burst` |
| `sounds/beam.ogg`, `sounds/explosion.ogg` | `lesserdivinitybeam`, `dusksepitaphexplosion` sound events |

The Dusk's Epitaph / Lesser Divinity items, the charter ore block and their models/textures are not included.

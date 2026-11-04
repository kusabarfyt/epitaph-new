package com.maceepitaph;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

/**
 * The four effect entity types. Their ids decide which asset files GeckoLib loads:
 *   assets/mace_epitaph/geckolib/models/entity/<id>.geo.json
 *   assets/mace_epitaph/geckolib/animations/entity/<id>.animation.json
 *   assets/mace_epitaph/textures/entity/<id>.png
 */
public final class EpitaphEntities {
    public static final EntityType<EpitaphEffectEntity> MARKER = register("epitaph_marker");
    public static final EntityType<EpitaphEffectEntity> CHAINS = register("epitaph_chains");
    public static final EntityType<EpitaphEffectEntity> BEAM = register("epitaph_beam");
    public static final EntityType<EpitaphEffectEntity> BURST = register("epitaph_burst");

    private EpitaphEntities() {}

    private static EntityType<EpitaphEffectEntity> register(String name) {
        ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, MaceEpitaph.id(name));
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, key,
                EntityType.Builder.<EpitaphEffectEntity>of(EpitaphEffectEntity::new, MobCategory.MISC)
                        .sized(30.0F, 64.0F)
                        .noSave()
                        .noSummon()
                        .fireImmune()
                        .clientTrackingRange(10)
                        .updateInterval(20)
                        .build(key));
    }

    /** Touching this class registers the entity types. */
    public static void init() {}
}

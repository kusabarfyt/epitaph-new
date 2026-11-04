package com.maceepitaph;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Marker;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * A client-side-only, invisible-by-default "prop" entity that just plays one GeckoLib animation
 * and removes itself afterwards. It extends vanilla's Marker so no abstract methods are needed.
 * Which animation it plays is decided by the entity type it was registered as.
 */
public class EpitaphEffectEntity extends Marker implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final String animationName;
    private final int lifetimeTicks;

    public EpitaphEffectEntity(EntityType<?> type, Level level) {
        super(type, level);
        String path = BuiltInRegistries.ENTITY_TYPE.getKey(type).getPath();
        // animation id (from the .animation.json) and how long to keep the entity alive (ticks)
        switch (path) {
            case "epitaph_marker" -> { this.animationName = "animation.marker.idle";        this.lifetimeTicks = 130; }
            case "epitaph_chains" -> { this.animationName = "animation.chains.spawn";       this.lifetimeTicks = 150; }
            case "epitaph_beam"   -> { this.animationName = "animation.beam.idle";          this.lifetimeTicks = 180; }
            default               -> { this.animationName = "animation.particlethingy.grow"; this.lifetimeTicks = 170; }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount >= this.lifetimeTicks) {
            this.discard();
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        RawAnimation animation = RawAnimation.begin().thenPlay(this.animationName);
        controllers.add(new AnimationController<>(test -> test.setAndContinue(animation)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}

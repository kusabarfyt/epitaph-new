package com.maceepitaph;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MaceEpitaphClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EpitaphEntities.init();

        EntityRendererRegistry.register(EpitaphEntities.MARKER, context -> new GeoEntityRenderer<>(context, EpitaphEntities.MARKER));
        EntityRendererRegistry.register(EpitaphEntities.CHAINS, context -> new GeoEntityRenderer<>(context, EpitaphEntities.CHAINS));
        EntityRendererRegistry.register(EpitaphEntities.BEAM, context -> new GeoEntityRenderer<>(context, EpitaphEntities.BEAM));
        EntityRendererRegistry.register(EpitaphEntities.BURST, context -> new GeoEntityRenderer<>(context, EpitaphEntities.BURST));

        MaceKillWatcher.init();

        // /maceepitaph  -> plays the animation 6 blocks in front of you, so you can test it without a kill.
        // (If this block ever fails to compile, you can delete it; nothing else depends on it.)
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                dispatcher.register(ClientCommandManager.literal("maceepitaph").executes(context -> {
                    Minecraft mc = Minecraft.getInstance();
                    Player player = mc.player;
                    if (mc.level != null && player != null) {
                        Vec3 ahead = player.getLookAngle().multiply(6.0, 0.0, 6.0);
                        EpitaphEffect.play(mc.level, player.position().add(ahead));
                    }
                    return 1;
                })));
    }
}

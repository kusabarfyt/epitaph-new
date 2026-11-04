package com.maceepitaph;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;

/**
 * Client-side mace kill detection, so it works on any server (no server mod needed):
 *  1. remember the last player you hit and whether you were holding a mace
 *  2. if that player dies within a few seconds, play the animation where they died
 */
public final class MaceKillWatcher {
    /** How long after your last hit a death still counts as your kill. */
    private static final int KILL_WINDOW_TICKS = 60;

    private static int tickCounter = 0;
    private static int targetId = -1;
    private static int hitTick = 0;
    private static boolean hitWithMace = false;

    private MaceKillWatcher() {}

    public static void init() {
        AttackEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
            if (player instanceof LocalPlayer && entity instanceof Player) {
                targetId = entity.getId();
                hitTick = tickCounter;
                hitWithMace = player.getMainHandItem().is(Items.MACE);
            }
            return InteractionResult.PASS;
        });

        ClientTickEvents.END_CLIENT_TICK.register(MaceKillWatcher::tick);
    }

    private static void tick(Minecraft mc) {
        tickCounter++;
        if (mc.level == null || mc.player == null) {
            targetId = -1;
            EpitaphEffect.clear();
            return;
        }
        EpitaphEffect.tick();

        if (targetId == -1) return;
        if (tickCounter - hitTick > KILL_WINDOW_TICKS) {
            targetId = -1;
            return;
        }

        Entity target = mc.level.getEntity(targetId);
        if (target instanceof Player victim && victim != mc.player && victim.isDeadOrDying()) {
            if (hitWithMace) {
                EpitaphEffect.play(mc.level, victim.position());
            }
            targetId = -1;
        }
    }
}

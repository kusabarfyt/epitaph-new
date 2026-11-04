package com.maceepitaph;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

public final class MaceKillWatcher {
    /** Prints "[Mace Epitaph] ..." messages in your chat so you can see which step worked. */
    public static boolean DEBUG = true;

    /** How long after your last hit a death still counts as your kill. */
    private static final int KILL_WINDOW_TICKS = 60;

    private static int tickCounter = 0;
    private static int targetId = -1;
    private static String targetName = "";
    private static Vec3 targetPos = Vec3.ZERO;
    private static int hitTick = 0;
    private static boolean hitWithMace = false;

    private MaceKillWatcher() {}

    public static void init() {
        AttackEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
            if (player instanceof LocalPlayer && entity instanceof Player victim) {
                targetId = victim.getId();
                targetName = victim.getName().getString();
                targetPos = victim.position();
                hitTick = tickCounter;
                hitWithMace = player.getMainHandItem().is(Items.MACE);
                debug("hit " + targetName + (hitWithMace ? " with a mace" : " (not a mace)"));
            }
            return InteractionResult.PASS;
        });

        ClientTickEvents.END_CLIENT_TICK.register(MaceKillWatcher::tick);
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> onGameMessage(message));
    }

    public static void debug(String text) {
        if (!DEBUG) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            mc.player.displayClientMessage(Component.literal("[Mace Epitaph] " + text), false);
        }
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
        if (target instanceof Player victim && victim != mc.player) {
            targetPos = victim.position();
            if (victim.isDeadOrDying()) {
                trigger(mc, targetPos, "victim shows as dead");
            }
        }
    }

    private static void onGameMessage(Component message) {
        Minecraft mc = Minecraft.getInstance();
        if (targetId == -1 || mc.level == null || mc.player == null) return;
        if (tickCounter - hitTick > KILL_WINDOW_TICKS) return;
        if (!(message.getContents() instanceof TranslatableContents contents)) return;
        if (!contents.getKey().startsWith("death.")) return;

        String text = message.getString();
        if (text.contains(targetName) && text.contains(mc.player.getName().getString())) {
            Entity target = mc.level.getEntity(targetId);
            Vec3 pos = target != null ? target.position() : targetPos;
            trigger(mc, pos, "death message");
        }
    }

    private static void trigger(Minecraft mc, Vec3 pos, String via) {
        targetId = -1;
        if (hitWithMace) {
            debug("kill detected (" + via + "), playing animation");
            EpitaphEffect.play(mc.level, pos);
        } else {
            debug("kill detected (" + via + ") but your last hit wasn't with a mace");
        }
    }
}

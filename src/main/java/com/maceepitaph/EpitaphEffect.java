package com.maceepitaph;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;

/** Plays the whole Dusk's Epitaph death sequence at a position. Tweak the timings below. */
public final class EpitaphEffect {
    // ---- TIMINGS (ticks, 20 = 1 second). These are educated guesses from the animation files. ----
    /** The beam animation itself holds off until 1.75s and lands at 2.25s. */
    public static final int BEAM_SOUND_DELAY = 35;
    /** When the explosion burst appears + explosion sound plays (beam landing = 45 ticks). */
    public static final int BURST_DELAY = 45;
    public static final float BEAM_VOLUME = 4.0F;
    public static final float EXPLOSION_VOLUME = 4.0F;
    // -------------------------------------------------------------------------------------------

    private record Task(int dueTick, Runnable action) {}

    private static final List<Task> TASKS = new ArrayList<>();
    private static int now = 0;
    private static int nextFakeId = -2_000_000; // negative ids can't clash with real server entities

    private EpitaphEffect() {}

    public static void play(ClientLevel level, Vec3 pos) {
        spawn(level, EpitaphEntities.MARKER, pos);
        spawn(level, EpitaphEntities.CHAINS, pos);
        spawn(level, EpitaphEntities.BEAM, pos);

        later(BEAM_SOUND_DELAY, () -> sound(level, pos, EpitaphSounds.BEAM, BEAM_VOLUME));
        later(BURST_DELAY, () -> {
            spawn(level, EpitaphEntities.BURST, pos);
            sound(level, pos, EpitaphSounds.EXPLOSION, EXPLOSION_VOLUME);
        });
    }

    /** Called once per client tick. */
    public static void tick() {
        now++;
        if (TASKS.isEmpty()) return;
        List<Task> due = new ArrayList<>();
        for (Task task : TASKS) {
            if (task.dueTick() <= now) due.add(task);
        }
        TASKS.removeAll(due);
        for (Task task : due) task.action().run();
    }

    public static void clear() {
        TASKS.clear();
    }

    private static void later(int ticks, Runnable action) {
        TASKS.add(new Task(now + ticks, action));
    }

    private static void spawn(ClientLevel level, EntityType<EpitaphEffectEntity> type, Vec3 pos) {
        EpitaphEffectEntity entity = new EpitaphEffectEntity(type, level);
        entity.setId(nextFakeId--);
        entity.setPos(pos.x, pos.y, pos.z);
        level.addEntity(entity);
    }

    private static void sound(ClientLevel level, Vec3 pos, SoundEvent sound, float volume) {
        level.playLocalSound(pos.x, pos.y, pos.z, sound, SoundSource.PLAYERS, volume, 1.0F, false);
    }
}

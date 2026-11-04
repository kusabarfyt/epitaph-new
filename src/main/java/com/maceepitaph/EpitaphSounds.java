package com.maceepitaph;

import net.minecraft.sounds.SoundEvent;

/** Sound events from sounds.json. Only used client-side, so they aren't registered in a registry. */
public final class EpitaphSounds {
    public static final SoundEvent BEAM = SoundEvent.createVariableRangeEvent(MaceEpitaph.id("lesserdivinitybeam"));
    public static final SoundEvent EXPLOSION = SoundEvent.createVariableRangeEvent(MaceEpitaph.id("dusksepitaphexplosion"));

    private EpitaphSounds() {}
}

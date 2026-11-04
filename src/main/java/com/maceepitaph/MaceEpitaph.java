package com.maceepitaph;

// NOTE: In 1.21.11 Mojang renamed ResourceLocation -> Identifier. This is the ONLY file that
// names the class, so if the compiler says it can't find "Identifier", swap it for
// net.minecraft.resources.ResourceLocation here (and ResourceLocation.fromNamespaceAndPath).
import net.minecraft.resources.Identifier;

public final class MaceEpitaph {
    public static final String MOD_ID = "mace_epitaph";

    private MaceEpitaph() {}

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}

package net.narutomod.goatee.client;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.NarutomodMod;

public class Sounds {

    public static SoundEvent get(String name) {
        return ElementsNarutomodMod.sounds.get(new ResourceLocation(NarutomodMod.MODID, name));
    }
}

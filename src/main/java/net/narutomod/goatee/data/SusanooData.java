package net.narutomod.goatee.data;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.narutomod.entity.EntitySusanooBase;
import net.narutomod.entity.EntitySusanooSkeleton;
import net.narutomod.entity.EntitySusanooWinged;

public class SusanooData {
    public NarutoData data;

    public Entry FULL_WINGED = new Entry();

    public SusanooData(NarutoData data) {
        this.data = data;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound comp) {
        NBTTagCompound tag = new NBTTagCompound();

        tag.setTag("FULL_WINGED", FULL_WINGED.writeToNBT());
        comp.setTag("susanooData", tag);
        return comp;
    }

    public Entry getFromEntity(Entity base) {
        if (base instanceof EntitySusanooWinged.EntityCustom)
            return FULL_WINGED;

        return null;
    }

    public void readFromNBT(NBTTagCompound compound) {
        NBTTagCompound tag = compound.getCompoundTag("susanooData");


        if (tag.hasKey("FULL_WINGED"))
            FULL_WINGED.readFromNBT(tag.getCompoundTag("FULL_WINGED"));
    }

    public static class Entry {
        public float size = -1, offset = -1;
        public boolean showParticles;
        public boolean renderPlayer, mini;

        public NBTTagCompound writeToNBT() {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setFloat("size", size);
            compound.setFloat("offset", offset);
            compound.setBoolean("showParticles", showParticles);
            compound.setBoolean("renderPlayer", renderPlayer);
            compound.setBoolean("mini", mini);
            return compound;
        }

        public void readFromNBT(NBTTagCompound compound) {
            if (compound.hasKey("size"))
                size = compound.getFloat("size");

            if (compound.hasKey("offset"))
                offset = compound.getFloat("offset");
            
            if (compound.hasKey("showParticles"))
                showParticles = compound.getBoolean("showParticles");

            if (compound.hasKey("mini"))
                mini = compound.getBoolean("mini");

            if (compound.hasKey("renderPlayer"))
                renderPlayer = compound.getBoolean("renderPlayer");

            if (compound.hasKey("mini"))
                mini = compound.getBoolean("mini");
        }
    }
}

package net.narutomod.goatee.data;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class WheelData {
    private String NAME;
    private Segment[] wheelSegment = new Segment[6];

    public WheelData(String name) {
        NAME = name;
        for (int i = 0; i < wheelSegment.length; i++)
            wheelSegment[i] = new Segment(i);
    }

    public Segment get(int slot) {
        return wheelSegment[slot];
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound wheelNbt = new NBTTagCompound();
        for (int i = 0; i < wheelSegment.length; i++)
            wheelSegment[i].writeToNBT(wheelNbt);

        compound.setTag(NAME, wheelNbt);
        return compound;
    }

    public void readFromNBT(NBTTagCompound compound) {
        NBTTagCompound wheelNbt = compound.getCompoundTag(NAME);
        for (int i = 0; i < wheelSegment.length; i++)
            wheelSegment[i].readFromNBT(wheelNbt.getCompoundTag(i + ""));
    }

    public static class Segment {
        public int slot = -1;
        public ItemStack stack = null;

        public Segment() {
        }

        public Segment(int slot) {
            this.slot = slot;
        }

        public NBTTagCompound writeToNBT(NBTTagCompound compound) {
            compound.setTag(slot + "", stack.writeToNBT(new NBTTagCompound()));
            return compound;
        }

        public void readFromNBT(NBTTagCompound compound) {
            stack = new ItemStack(compound);
        }

        public void reset() {
            stack = null;
        }
    }
}

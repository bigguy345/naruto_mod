package net.narutomod.goatee.data;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import static net.narutomod.goatee.data.capability.NarutoCapabilities.NARUTO_DATA_CAPABILITY;

public class NarutoData {

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        return compound;
    }

    public void readFromNBT(NBTTagCompound compound) {
    }

    public static NarutoData get(EntityPlayer player) {
        return player.getCapability(NARUTO_DATA_CAPABILITY, null);
    }

    public static class CapProvider implements ICapabilitySerializable<NBTTagCompound> {
        private final NarutoData instance = new NarutoData();

        @Override
        public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
            return capability == NARUTO_DATA_CAPABILITY;
        }

        @Override
        public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
            return capability == NARUTO_DATA_CAPABILITY ? NARUTO_DATA_CAPABILITY.cast(instance) : null;
        }

        @Override
        public NBTTagCompound serializeNBT() {
            return instance.writeToNBT(new NBTTagCompound()); // implement if you want save support
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt) {
            // implement if you want load support
            instance.readFromNBT(nbt);
        }
    }
}

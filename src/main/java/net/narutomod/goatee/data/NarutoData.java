package net.narutomod.goatee.data;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.narutomod.goatee.network.PacketHandler;
import net.narutomod.goatee.network.packets.NarutoSyncData;

import static net.narutomod.goatee.data.capability.NarutoCapabilities.NARUTO_DATA_CAPABILITY;

public class NarutoData {
    public EntityPlayer player;
    public WheelData dojutsuWheel = new WheelData("DojutsuWheel");

    public NarutoData() {
    }

    public NarutoData(EntityPlayer player) {
        this.player = player;
    }

    public NBTTagCompound writeToNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        dojutsuWheel.writeToNBT(compound);

        return compound;
    }

    public void readFromNBT(NBTTagCompound compound) {
        dojutsuWheel.readFromNBT(compound);
    }

    public static NarutoData get(EntityPlayer player) {
        return player.getCapability(NARUTO_DATA_CAPABILITY, null);
    }

    @SideOnly(Side.CLIENT)
    public static NarutoData getClient() {
        return Minecraft.getMinecraft().player.getCapability(NARUTO_DATA_CAPABILITY, null);
    }

    public static class CapProvider implements ICapabilitySerializable<NBTTagCompound> {
        private final NarutoData instance;

        public CapProvider(EntityPlayer player) {
            instance = new NarutoData(player);
        }

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
            return instance.writeToNBT(); // implement if you want save support
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt) {
            // implement if you want load support
            instance.readFromNBT(nbt);
        }
    }
}

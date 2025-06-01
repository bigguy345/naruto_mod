package net.narutomod.goatee.data.capability;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.narutomod.NarutomodMod;
import net.narutomod.goatee.data.NarutoData;

@Mod.EventBusSubscriber
public class NarutoCapabilities {
    @CapabilityInject(NarutoData.class)
    public static final Capability<NarutoData> NARUTO_DATA_CAPABILITY = null;

    public static void registerCapability() {
        CapabilityManager.INSTANCE.register(NarutoData.class, new Capability.IStorage<NarutoData>() {
            @Override
            public NBTBase writeNBT(Capability<NarutoData> capability, NarutoData instance, EnumFacing side) {
                return instance.writeToNBT(new NBTTagCompound());
            }

            @Override
            public void readNBT(Capability<NarutoData> capability, NarutoData instance, EnumFacing side, NBTBase nbt) {
                instance.readFromNBT((NBTTagCompound) nbt);
            }
        }, NarutoData::new);
    }

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityPlayer)
            event.addCapability(new ResourceLocation(NarutomodMod.MODID, "naruto_data"), new NarutoData.CapProvider());
    }
}

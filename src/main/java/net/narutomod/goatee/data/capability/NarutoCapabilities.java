package net.narutomod.goatee.data.capability;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.narutomod.NarutomodMod;
import net.narutomod.goatee.data.NarutoData;
import net.narutomod.goatee.network.packets.NarutoSyncData;
import net.narutomod.item.ItemDojutsu;

@Mod.EventBusSubscriber
public class NarutoCapabilities {
    @CapabilityInject(NarutoData.class)
    public static final Capability<NarutoData> NARUTO_DATA_CAPABILITY = null;

    public static void registerCapability() {
        CapabilityManager.INSTANCE.register(NarutoData.class, new Capability.IStorage<NarutoData>() {
            @Override
            public NBTBase writeNBT(Capability<NarutoData> capability, NarutoData instance, EnumFacing side) {
                return instance.writeToNBT();
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
            event.addCapability(new ResourceLocation(NarutomodMod.MODID, "naruto_data"), new NarutoData.CapProvider((EntityPlayer) event.getObject()));
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.player.world == null || event.player.world.isRemote || event.player instanceof FakePlayer)
            return;

        EntityPlayer player = event.player;
        if (event.side == Side.SERVER && event.phase == TickEvent.Phase.START) {
            NarutoData.get(player).tick();

            if (player.ticksExisted % 10 == 0) 
                NarutoSyncData.syncTrackingClients(NarutoData.get(player));
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        EntityPlayer original = event.getOriginal();
        EntityPlayer clone = event.getEntityPlayer();

        NarutoData.get(clone).readFromNBT(NarutoData.get(original).writeToNBT());
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof EntityPlayer))
            return;

        EntityPlayer player = (EntityPlayer) event.getEntity();
        NarutoData data = NarutoData.get(player);


        if (!player.world.getGameRules().getBoolean("keepInventory")) {
            ItemStack slot = data.getDojutsuSlot();
            if (ItemDojutsu.isDroppable(data.getDojutsuSlot()))
                player.dropItem(slot.copy(), true, false);
            
            if (!slot.isEmpty())
                slot.setCount(0);

            data.dojutsuWheel.dropAll(player, eye -> ItemDojutsu.isDroppable(eye), false);
            data.dojutsuWheel.clear();
        }
    }
}

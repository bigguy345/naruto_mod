package net.narutomod.goatee.client;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.narutomod.goatee.data.NarutoData;
import net.narutomod.goatee.data.SusanooData;

@Mod.EventBusSubscriber(Side.CLIENT)
public class RenderEvents {

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onRenderPlayer(RenderLivingEvent.Pre<EntityPlayer> event) {
        if (event.getEntity() instanceof EntityPlayer) {
            SusanooData.Entry riddenSusanoo = NarutoData.getSusanooData(event.getEntity());
            if (riddenSusanoo != null && !riddenSusanoo.renderPlayer)
                event.setCanceled(true);
        }
    }
}

package net.narutomod.goatee.client;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.narutomod.entity.EntitySusanooBase;
import net.narutomod.goatee.data.NarutoData;
import net.narutomod.goatee.data.SusanooData;

@Mod.EventBusSubscriber(Side.CLIENT)
public class RenderEvents {

    private static boolean isRenderingGui = false;

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onRenderPlayer(RenderLivingEvent.Pre<EntityPlayer> event) {
        if (isRenderingGui)
            return;
        
        if (event.getEntity() instanceof EntityPlayer) {
            SusanooData.Entry riddenSusanoo = NarutoData.getSusanooData(event.getEntity());
            if (riddenSusanoo != null && !riddenSusanoo.renderPlayer) {
                EntitySusanooBase susanoo = (EntitySusanooBase) event.getEntity().getRidingEntity();
                if (susanoo.ticksExisted < 30) //faster fade in animation
                    susanoo.ticksExisted = 30;
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onGuiDraw(GuiScreenEvent.DrawScreenEvent.Pre event) {
        isRenderingGui = true;
    }

    @SubscribeEvent
    public static void onGuiDrawPost(GuiScreenEvent.DrawScreenEvent.Post event) {
        isRenderingGui = false;
    }
}

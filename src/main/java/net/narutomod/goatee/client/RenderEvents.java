package net.narutomod.goatee.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.narutomod.entity.EntitySusanooBase;
import net.narutomod.goatee.data.NarutoData;
import net.narutomod.goatee.data.SusanooData;
import net.narutomod.item.ItemKagutsuchiSwordRanged;
import net.narutomod.item.ItemTotsukaSword;

@Mod.EventBusSubscriber(Side.CLIENT)
public class RenderEvents {

    private static boolean isRenderingGui = false;

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void disableSusanooPlayer(RenderLivingEvent.Pre<EntityPlayer> event) {
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
    public static void disableSusanooHand(RenderHandEvent event) {
        AbstractClientPlayer entity = Minecraft.getMinecraft().player;
        SusanooData.Entry riddenSusanoo = NarutoData.getSusanooData(entity);
        if (riddenSusanoo != null && !riddenSusanoo.renderPlayer) {
            EntitySusanooBase susanoo = (EntitySusanooBase) entity.getRidingEntity();
            if (susanoo.ticksExisted < 30) //faster fade in animation
                susanoo.ticksExisted = 30;

            ItemStack main = entity.getHeldItemMainhand();
            boolean isSusanooSword = susanoo.shouldShowSword() || main.getItem() == ItemTotsukaSword.block || main.getItem() == ItemKagutsuchiSwordRanged.block;
            if (entity.getHeldItemMainhand().isEmpty() && entity.getHeldItemOffhand().isEmpty() || isSusanooSword)
                event.setCanceled(true);
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

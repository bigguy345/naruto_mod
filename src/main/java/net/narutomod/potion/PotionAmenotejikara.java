package net.narutomod.potion;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.narutomod.ElementsNarutomodMod;
import org.lwjgl.opengl.GL11;

@ElementsNarutomodMod.ModElement.Tag
public class PotionAmenotejikara extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:amenotejikara")
	public static final Potion potion = null;

	public PotionAmenotejikara(ElementsNarutomodMod instance) {
		super(instance, 529);
	}

	@Override
	public void initElements() {
		elements.potions.add(() -> new PotionCustom());
	}

	public static class PotionCustom extends Potion {
		private EntityLivingBase target;
		private EntityLivingBase switchTargetWith;
		private BlockPos blockPos;

		public PotionCustom() {
			super(false, 0x0033FFCC);
			setRegistryName("amenotejikara");
			setPotionName("effect.amenotejikara");
		}

		public PotionCustom(EntityLivingBase target, EntityLivingBase switchTargetWith, BlockPos blockPos) {
			this();
			this.target = target;
			this.switchTargetWith = switchTargetWith;
			this.blockPos = blockPos;

			this.target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 3, 20, false, false));
			this.switchTargetWith.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 3, 20, false, false));
		}

		@Override
		public boolean isReady(int duration, int amplifier) {
			if (duration == 1)
				switchTargets();

			return true;
		}

		public void switchTargets() {
			if (blockPos != null) {

			} else {
				double x = target.posX;
				double y = target.posY;
				double z = target.posZ;
				target.setPositionAndUpdate(switchTargetWith.posX, switchTargetWith.posY, switchTargetWith.posZ);
				switchTargetWith.setPositionAndUpdate(x, y, z);

				target.world.playSound(null, target.posX, target.posY, target.posZ, SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:rinnegansfx")), SoundCategory.NEUTRAL, 0.8f, target.getRNG().nextFloat() * 0.4f + 0.8f);
				target.world.playSound(null, switchTargetWith.posX, switchTargetWith.posY, switchTargetWith.posZ, SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:rinnegansfx")), SoundCategory.NEUTRAL, 0.8f, target.getRNG().nextFloat() * 0.4f + 0.8f);
			}
		}

		@Override
		public boolean shouldRenderInvText(PotionEffect effect) {
			return false;
		}

		@Override
		public boolean shouldRenderHUD(PotionEffect effect) {
			return false;
		}

		public boolean shouldRender(PotionEffect effect) {
			return false;
		}
	}

	public static class Effect extends PotionEffect {
		private EntityLivingBase target;
		private EntityLivingBase switchTargetWith;
		private BlockPos blockPos;

		public Effect(EntityLivingBase target, EntityLivingBase switchTargetWith, BlockPos blockPos) {
			super(potion, 10, 0);
			this.target = target;
			this.switchTargetWith = switchTargetWith;
			this.blockPos = blockPos;

			this.target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 3, 20, false, false));
			this.switchTargetWith.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 3, 20, false, false));
		}

		public void switchTargets() {

		}
	}

	public class EntityHook {
		@SubscribeEvent
		@SideOnly(Side.CLIENT)
		public void renderInvert(RenderGameOverlayEvent event) {
			if (event.getType() == RenderGameOverlayEvent.ElementType.POTION_ICONS && !event.isCancelable()) {
				Minecraft mc = Minecraft.getMinecraft();
				if (mc.player.isPotionActive(potion)) {
					PotionEffect eff = mc.player.getActivePotionEffect(potion);
					GL11.glPushMatrix();
					int sWidth = event.getResolution().getScaledWidth();
					int sHeight = event.getResolution().getScaledHeight();
					int color = 452984831;
					GlStateManager.enableAlpha();
					GlStateManager.enableColorLogic();
					GlStateManager.colorLogicOp(GlStateManager.LogicOp.INVERT);
					GuiIngame.drawRect(0, 0, sWidth, sHeight, color);
					GlStateManager.colorLogicOp(GlStateManager.LogicOp.COPY);
					GlStateManager.disableColorLogic();
					GlStateManager.disableAlpha();
					GuiIngame.drawRect(sWidth / 2 - 5, sHeight / 2, sWidth / 2 + 5, sHeight / 2 + 1, -1);
					GuiIngame.drawRect(sWidth / 2, sHeight / 2 - 5, sWidth / 2 + 1, sHeight / 2 + 5, -1);
					GL11.glPopMatrix();
				}
			}
		}
	}

	@Override
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new EntityHook());
	}
}

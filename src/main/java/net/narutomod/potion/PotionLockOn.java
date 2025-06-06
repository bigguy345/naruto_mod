
package net.narutomod.potion;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.narutomod.ElementsNarutomodMod;

@ElementsNarutomodMod.ModElement.Tag
public class PotionLockOn extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:lock_on")
	public static final Potion potion = null;

	public PotionLockOn(ElementsNarutomodMod instance) {
		super(instance, 407);
	}

	@Override
	public void initElements() {
		elements.potions.add(() -> new PotionCustom());
	}

	public static class PotionCustom extends Potion {
		private final ResourceLocation potionIcon;

		public PotionCustom() {
			super(false, -1);
			this.setBeneficial();
			this.setRegistryName("lock_on");
			this.setPotionName("effect.lock_on");
			this.potionIcon = new ResourceLocation("narutomod:textures/mob_effect/lock_on.png");
		}

		@Override
		public boolean isInstant() {
			return true;
		}

		@Override
		public boolean shouldRenderInvText(PotionEffect effect) {
			return true;
		}

		@Override
		public boolean shouldRenderHUD(PotionEffect effect) {
			return true;
		}

		@SideOnly(Side.CLIENT)
		@Override
		public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc) {
			if (mc.currentScreen != null) {
				mc.getTextureManager().bindTexture(potionIcon);
				Gui.drawModalRectWithCustomSizedTexture(x + 6, y + 7, 0, 0, 18, 18, 18, 18);
			}
		}

		@SideOnly(Side.CLIENT)
		@Override
		public void renderHUDEffect(int x, int y, PotionEffect effect, Minecraft mc, float alpha) {
			mc.getTextureManager().bindTexture(potionIcon);
			Gui.drawModalRectWithCustomSizedTexture(x + 3, y + 3, 0, 0, 18, 18, 18, 18);
			
			String duration = Potion.getPotionDurationString(effect, 1.0F);
			mc.fontRenderer.drawStringWithShadow(duration, x - 22, y + 9, 0xFFFFFF); // White color
		}

		@Override
		public boolean isReady(int duration, int amplifier) {
			return true;
		}
	}
}

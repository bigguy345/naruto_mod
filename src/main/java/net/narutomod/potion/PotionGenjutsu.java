package net.narutomod.potion;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.item.ItemInton;

@ElementsNarutomodMod.ModElement.Tag
public class PotionGenjutsu extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:genjutsu")
	public static final Potion potion = null;

	public PotionGenjutsu(ElementsNarutomodMod instance) {
		super(instance, 529);
	}

	@Override
	public void initElements() {
		elements.potions.add(() -> new PotionCustom());
	}

	public static class PotionCustom extends Potion {
		private final ResourceLocation potionIcon;

		public PotionCustom() {
			super(false, 0xff0000);
			setRegistryName("genjutsu");
			setPotionName("effect.genjutsu");
			potionIcon = new ResourceLocation("narutomod:textures/mob_effect/genjutsu.png");
		}

		public void removeAttributesModifiersFromEntity(EntityLivingBase entity, AbstractAttributeMap attributeMapIn, int amplifier) {
			super.removeAttributesModifiersFromEntity(entity, attributeMapIn, amplifier);
			ItemInton.Genjutsu.GENJUTSU_CASTER_MAP.remove(entity.getUniqueID());
		}

		@Override
		public boolean isReady(int duration, int amplifier) {
			return true;
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
	}
}


package net.narutomod.item;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.entity.EntityMindTransfer;
import net.narutomod.entity.EntityShadowImitation;
import net.narutomod.entity.EntityTailedBeast;
import net.narutomod.potion.PotionGenjutsu;
import net.narutomod.potion.PotionParalysis;
import net.narutomod.procedure.ProcedureSync;
import net.narutomod.procedure.ProcedureUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@ElementsNarutomodMod.ModElement.Tag
public class ItemInton extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:inton")
	public static final Item block = null;
	public static final int ENTITYID = 172;
	public static final ItemJutsu.JutsuEnum GENJUTSU = new ItemJutsu.JutsuEnum(0, "genjutsu", 'B', 300d, new Genjutsu());
	public static final ItemJutsu.JutsuEnum MBTRANSFER = new ItemJutsu.JutsuEnum(1, "mind_transfer", 'C', 300d, new EntityMindTransfer.EC.Jutsu());
	public static final ItemJutsu.JutsuEnum SHADOW_IMITATION = new ItemJutsu.JutsuEnum(2, "shadow_imitation", 'B', 50d, new EntityShadowImitation.EC.Jutsu());

	public ItemInton(ElementsNarutomodMod instance) {
		super(instance, 441);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new RangedItem(GENJUTSU, MBTRANSFER, SHADOW_IMITATION));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:inton", "inventory"));
	}

	public static class RangedItem extends ItemJutsu.Base {
		public RangedItem(ItemJutsu.JutsuEnum... list) {
			super(ItemJutsu.JutsuEnum.Type.INTON, list);
			this.setUnlocalizedName("inton");
			this.setRegistryName("inton");
			this.setCreativeTab(TabModTab.tab);
		}
	}

	public static class Genjutsu implements ItemJutsu.IJutsuCallback {
		private final double maxRange;
		private final int duration;
		private final int cooldown = 1200;

		public Genjutsu() {
			this(16.0d, 200);
		}

		public Genjutsu(double range, int durationIn) {
			this.maxRange = range;
			this.duration = durationIn;
		}

		@Override
		public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
			Entity target = ProcedureUtils.objectEntityLookingAt(entity, this.maxRange).entityHit;
			if (target instanceof EntityLivingBase && this.createJutsu(entity, (EntityLivingBase)target, this.duration)) {
				
				if (stack != null && entity instanceof EntityPlayer) {
					ItemJutsu.setCurrentJutsuCooldown(stack, (EntityPlayer)entity, this.cooldown);
				}
				return true;
			}
			return false;
		}

		public static boolean createJutsu(EntityLivingBase entity, ItemStack stack, double range, int durationIn, int cooldown) {
			Entity target = ProcedureUtils.objectEntityLookingAt(entity, range).entityHit;
			if (!(target instanceof EntityLivingBase))
				return false;

			if (((EntityLivingBase) target).isPotionActive(PotionGenjutsu.potion)) {
				return remove(entity, (EntityLivingBase) target);
			}
			
			if (cooldown > 0) { //cooldown is bigger than > 0 if implementing custom coolown logic, else set to -1 to use ItemJutsu's cooldown
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).sendStatusMessage(new TextComponentTranslation("chattext.cooldown.formatted", cooldown / 20), true);
				return false;
			}
			
			if (target instanceof EntityLivingBase && createJutsu(entity, (EntityLivingBase) target, durationIn)) {
				if (cooldown == -1 && stack != null && entity instanceof EntityPlayer)
					ItemJutsu.setCurrentJutsuCooldown(stack, entity, 1200);

				return true;
			}
			return false;
		}

		public static boolean createJutsu(EntityLivingBase entity, EntityLivingBase target, int durationIn) {
			if (canTargetBeAffected(entity, target)) {
				entity.world.playSound(null, target.posX, target.posY, target.posZ,
				  SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:genjutsu")), SoundCategory.NEUTRAL, 1f, 1f);

				target.addPotionEffect(new PotionEffect(PotionGenjutsu.potion, durationIn, 0));
				GENJUTSU_CASTER_MAP.put(target.getUniqueID(), entity.getUniqueID());

				target.addPotionEffect(new PotionEffect(PotionParalysis.potion, durationIn, 1, false, false));
				target.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, durationIn + 40, 0, false, false));
				target.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, durationIn, 0, false, false));
				if (target instanceof EntityPlayerMP) {
					ProcedureSync.MobAppearanceParticle.send((EntityPlayerMP)target, entity.getEntityId());
				}
				target.setRevengeTarget(entity);
				return true;
			}
			return false;			
		}

		public static final Map<UUID, UUID> GENJUTSU_CASTER_MAP = new HashMap<>();

		public static EntityLivingBase getGenjutsuCaster(EntityLivingBase target) {
			UUID casterId = GENJUTSU_CASTER_MAP.get(target.getUniqueID());
			if (casterId != null && target.world instanceof WorldServer)
				return (EntityLivingBase) ((WorldServer) target.world).getEntityFromUuid(casterId);

			return null;
		}

		public static boolean remove(EntityLivingBase remover, EntityLivingBase target) {
			boolean remove;
			EntityLivingBase caster = getGenjutsuCaster(target);

			if (caster == null || caster.equals(remover))
				remove = true;
			else {
				ItemDojutsu.Tier casterTier = ItemDojutsu.getTier(ItemDojutsu.getWorn(caster));
				ItemDojutsu.Tier removerTier = ItemDojutsu.getTier(ItemDojutsu.getWorn(remover));

				remove = removerTier.canRemoveGenjutsuFrom(casterTier);
			}

			if (remove) {
				target.removePotionEffect(PotionGenjutsu.potion);
				target.removePotionEffect(PotionParalysis.potion);
				target.removePotionEffect(MobEffects.NAUSEA);
				target.removePotionEffect(MobEffects.BLINDNESS);
				return true;
			}

			return false;
		}

		public static boolean canTargetBeAffected(EntityLivingBase caster, EntityLivingBase target) {
			if (target instanceof EntityTailedBeast.Base && !ItemSharingan.wearingAny(caster)) 
				return false;
			else if (ItemRinnegan.isWearing(target) && (!ItemRinnegan.isWearing(caster) || !ItemSharingan.isWearingEternal(caster)))
				return false;
			else if (ItemSharingan.isWearingEternal(target) && !ItemSharingan.isWearingEternal(caster))
				return false;
			else if (ItemSharingan.isWearingMangekyo(target) && !ItemSharingan.isWearingMangekyo(caster))
				return false;
			else {
				ItemStack stack = ProcedureUtils.getMatchingItemStack(target, ItemNinjutsu.block);
				if (stack != null && ItemNinjutsu.isJutsuEnabled(stack, ItemNinjutsu.BUGSWARM)) {
					return false;
				}
			}
			return true;
		}
	}
}

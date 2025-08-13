package net.narutomod.item;

import net.minecraft.entity.EntityLiving;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

import net.minecraft.world.World;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.Item;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;

import net.minecraft.util.text.translation.I18n;
import net.minecraft.block.material.Material;

import net.narutomod.Chakra;
import net.narutomod.ModConfig;
import net.narutomod.goatee.client.Sounds;
import net.narutomod.goatee.client.model.ModelDojutsu;
import net.narutomod.goatee.data.DojutsuData;
import net.narutomod.goatee.data.sidedata.RinneganTomoeSideData;
import net.narutomod.goatee.util.AdvancementUtil;
import net.narutomod.potion.PotionLockOn;
import net.narutomod.procedure.*;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.ElementsNarutomodMod;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import javax.annotation.Nullable;
import net.minecraft.util.math.RayTraceResult;

@ElementsNarutomodMod.ModElement.Tag
public class ItemSharingan extends ElementsNarutomodMod.ModElement {
	@ObjectHolder("narutomod:sharinganhelmet")
	public static final Item helmet = null;

	public static final String RINNEGEN_AWAKENING_KEY = "RinneganAwakening";

	public static double getMangekyoChakraUsage(EntityLivingBase entity) {
		double chakraUsage = ModConfig.DOJUTSU.MANGEKYO_CHAKRA_USAGE;
		ItemStack stack = ItemDojutsu.getWorn(entity);
		return ItemDojutsu.isOwner(stack, entity) ? chakraUsage : chakraUsage * 3;
	}

	public static double getEternalChakraUsage(EntityLivingBase entity) {
		double chakraUsage = ModConfig.DOJUTSU.ETERNAL_CHAKRA_USAGE;
		ItemStack stack = ItemDojutsu.getWorn(entity);
		return ItemDojutsu.isOwner(stack, entity) ? chakraUsage : chakraUsage * 3;
	}
	
	public ItemSharingan(ElementsNarutomodMod instance) {
		super(instance, 56);
	}

	public static final HashMap<Item, Integer> MANGEKYO_POOL = new HashMap<>();

	@Override
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new LockOn());
		addMangekyoToPool(ItemMangekyoSharingan.helmet, 50);
		addMangekyoToPool(ItemMangekyoSharinganObito.helmet, 50);
		
	}

	public static void addMangekyoToPool(Item mangekyo, int weight) {
		MANGEKYO_POOL.put(mangekyo, weight);
	}
	

	public static Item getRandomMangekyoFromPool() {
		Random rand = ThreadLocalRandom.current();
		
		int totalWeight = 0;
		for (int weight : MANGEKYO_POOL.values()) 
			totalWeight += weight;
		
		if (totalWeight <= 0 || MANGEKYO_POOL.isEmpty()) {
			return null; 
		}

		int randomWeight = rand.nextInt(totalWeight); // [0, totalWeight)
		int runningWeight = 0;

		// Step 3: Find the item corresponding to this weight
		for (Map.Entry<Item, Integer> entry : MANGEKYO_POOL.entrySet()) {
			runningWeight += entry.getValue();
			if (randomWeight < runningWeight) {
				return entry.getKey();
			}
		}

		return null; 
	}

	public static Item getRandomEternal() {
		return ((ItemSharingan.Base) ItemSharingan.getRandomMangekyoFromPool()).getEternalMangekyo(ItemStack.EMPTY, ItemStack.EMPTY).getItem();
	}

	public static class Base extends ItemDojutsu.Base {
		private boolean canDamage;

		public Base(ItemArmor.ArmorMaterial material) {
			super(material);
		}

		@Override
		public ItemDojutsu.Type getType() {
			return ItemDojutsu.Type.SHARINGAN;
		}

		@Override
		public ItemDojutsu.Tier getTier(ItemStack stack) {
			if (isEternal())
				return ItemDojutsu.Tier.ETERNAL;
			else if (isMangekyo())
				return ItemDojutsu.Tier.MANGEKYO;

			return ItemDojutsu.Tier.SHARINGAN;
		}

		public Type getSubType() {
			return Type.BASE;
		}

		public boolean isMangekyo() {
			return false;
		}

		public boolean isEternal() {
			return false;
		}

		/**
		 * Inherit this and override it. Medical Scroll fetches this
		 * @param mangekyo1 Owner's mangekyo
		 * @param mangekyo2 Mangekyo to combine with
		 * @return
		 */
		public ItemStack getEternalMangekyo(ItemStack mangekyo1, ItemStack mangekyo2) {
			return new ItemStack(ItemMangekyoSharinganEternal.helmet);
		}

		@SideOnly(Side.CLIENT)
		@Override
		public ModelBiped getArmorModel(EntityLivingBase living, ItemStack stack, EntityEquipmentSlot slot, ModelBiped defaultModel) {
			if (data.useAdvancedModel(stack)) {
				ModelDojutsu model = ModelDojutsu.getModel(living, stack, this);
				model.rightEye.showModel = model.leftEye.showModel = !isBlinded(stack);
				return model;
			} else {
				ItemDojutsu.ClientModel.ModelHelmetSnug armorModel = (ItemDojutsu.ClientModel.ModelHelmetSnug) super.getArmorModel(living, stack, slot, defaultModel);
				armorModel.highlightHide = isBlinded(stack);
				return armorModel;
			}
		}

		@Override
		public void onArmorTick(World world, EntityPlayer entity, ItemStack itemstack) {
			super.onArmorTick(world, entity, itemstack);
			if (world.isRemote)
				return;
			int x = (int) entity.posX;
			int y = (int) entity.posY;
			int z = (int) entity.posZ;
			{
				HashMap<Object, Object> $_dependencies = new HashMap<>();
				$_dependencies.put("entity", entity);
				$_dependencies.put("x", x);
				$_dependencies.put("y", y);
				$_dependencies.put("z", z);
				$_dependencies.put("itemstack", itemstack);
				$_dependencies.put("world", world);
				ProcedureSharinganHelmetTickEvent.executeProcedure((HashMap) $_dependencies);
			}
			if (entity.ticksExisted % 6 == 1
			 && (!((Base)itemstack.getItem()).isEternal() || !this.isOwner(itemstack, entity))
			 && (entity.getEntityData().getBoolean("amaterasu_active")
			  || entity.getEntityData().getBoolean("susanoo_activated") || entity.getEntityData().getBoolean("kamui_teleport"))) {
			 	((Base)itemstack.getItem()).canDamage = true;
				itemstack.damageItem(this.isOwner(itemstack, entity) ? 3 : 9, entity);
				((Base)itemstack.getItem()).canDamage = false;
			}

			

			if (genjutsuCD > 0)
				genjutsuCD--;
		}

		public void applyEffects(EntityPlayer player, ItemStack itemstack) {
			if (player.ticksExisted % 60 == 0) {
				if (isEternal()) {
					player.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 100, 2, false, false));
					player.addPotionEffect(new PotionEffect(MobEffects.SPEED, 100, 4, false, false));
					player.addPotionEffect(new PotionEffect(MobEffects.HASTE, 100, 3, false, false));
				} else if (isMangekyo()) {
					player.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 100, 1, false, false));
					player.addPotionEffect(new PotionEffect(MobEffects.SPEED, 100, 3, false, false));
					player.addPotionEffect(new PotionEffect(MobEffects.HASTE, 100, 2, false, false));
				} else {
					player.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 100, 0, false, false));
					player.addPotionEffect(new PotionEffect(MobEffects.SPEED, 100, 2, false, false));
					player.addPotionEffect(new PotionEffect(MobEffects.HASTE, 100, 1, false, false));
				}
			}
		}

		public void consumeChakra(EntityPlayer player, ItemStack stack) {
			if (player.isCreative())
				return;

			if (player.ticksExisted % 20 == 0) {
				if (isEternal()) {
					Chakra.pathway(player).consume(getEternalChakraUsage(player));
				} else if (isMangekyo()) {
					Chakra.pathway(player).consume(getMangekyoChakraUsage(player));
				}
			}
		}


		@Override
		public void onUpdate(ItemStack stack, World world, Entity entity, int par4, boolean par5) {
			super.onUpdate(stack, world, entity, par4, par5);
//			if (entity instanceof EntityPlayer && entity.ticksExisted % 20 == 0) {
//				for (ItemStack stack1 : ProcedureUtils.getAllItemsOfSubType((EntityPlayer)entity, Base.class)) {
//					if (!ItemStack.areItemStacksEqual(itemstack, stack1) && stack1.getItem() == helmet) {
//						UUID uuid1 = ProcedureUtils.getOwnerId(itemstack);
//						if (uuid1 != null && uuid1.equals(ProcedureUtils.getOwnerId(stack1))) {
//							stack1.shrink(1);
//						}
			//					}w
//				}
//			}
			//if (entity instanceof EntityPlayerMP)
			//	((EntityPlayerMP) entity).getFoodStats().setFoodLevel(4);


			if (!world.isRemote && entity.ticksExisted % 20 == 0 && entity instanceof EntityPlayerMP && hasRinneganAwakenKey(stack)) {
				checkRinneganAwakening(stack, world, entity);
			}
		}

		public void checkRinneganAwakening(ItemStack stack, World world, Entity entity) {

			if (Math.random() <= 0.0005) { // 0.05%
				boolean unlockTomoe = Math.random() * 100 <= ModConfig.DOJUTSU.RINNEGAN_TOMOE_AWAKEN_CHANCE;

				stack.getTagCompound().removeTag(ItemSharingan.RINNEGEN_AWAKENING_KEY);
				ItemStack rinneganstack = new ItemStack(unlockTomoe ? ItemRinneganTomoe.helmet : ItemRinnegan.helmet);
				ItemDojutsu.setOwner(rinneganstack, (EntityLivingBase) entity);

				if (unlockTomoe) {
					RinneganTomoeSideData rightSide = (RinneganTomoeSideData) DojutsuData.getRight(rinneganstack);
					String eternalMangekyoTexture = RinneganTomoeSideData.getEyeTexture(stack, entity);
					rightSide.setTexture(ItemRinneganTomoe.ETERNAL_ON, eternalMangekyoTexture);

					setColor(rinneganstack, getColor(stack));
					ItemRinneganTomoe.setTomoeStatus(rinneganstack, ItemRinneganTomoe.ETERNAL_ON);
				}
				
				ProcedureUtils.swapItemToSlot((EntityPlayer) entity, EntityEquipmentSlot.HEAD, rinneganstack);

				AdvancementUtil.grant((EntityPlayerMP) entity, "narutomod:rinneganawakened");
				entity.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("ui.toast.challenge_complete")), 1, 1);
			}
		}

		@Override
		public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
			super.addInformation(stack, worldIn, tooltip, flagIn);
			tooltip.add(TextFormatting.DARK_GRAY + I18n.translateToLocal("tooltip.sharingan.descr") + (isDodgeEnabled(stack) ? TextFormatting.GREEN + I18n.translateToLocal("tooltip.sharingan.dodge_on") : TextFormatting.RED + I18n.translateToLocal("tooltip.sharingan.dodge_off")) + TextFormatting.WHITE);

			if (hasRinneganAwakenKey(stack))
				tooltip.add("§d§l" + I18n.translateToLocal("item.ems.chakra_transmutation"));
			
			if (ItemDojutsu.is(stack, ItemSharingan.class))
				tooltip.add(TextFormatting.ITALIC + I18n.translateToLocal("key.mcreator.specialjutsu1") + ": " + TextFormatting.GRAY + I18n.translateToLocal("entity.genjutsu.name"));
		}
		// returns true if evaded, false if otherwise
		public boolean onAttackEvent(LivingAttackEvent event, EntityLivingBase entity, Entity attacker) {
			if (isDodgeEnabled(ItemDojutsu.getWorn(entity)) && entity.getRNG().nextFloat() <= 0.6f) {
		 		Entity immediateSource = event.getSource().getImmediateSource();
				if (immediateSource == null)
					return false;
				Entity trueSource = event.getSource().getTrueSource();
				
		    	List<BlockPos> list = ProcedureUtils.getAllAirBlocks(entity.world, entity.getEntityBoundingBox().grow(2.5d));
		    	for (int i = 0; i < list.size(); i++) {
		    		BlockPos pos = list.get(entity.getRNG().nextInt(list.size()));
		    		Material material = entity.world.getBlockState(pos.down()).getMaterial();
		    		if ((material.isSolid() || material == material.WATER)
		    		 && immediateSource.getDistanceSqToCenter(pos) > 6.25d && ProcedureUtils.isSpaceOpenToStandOn(entity, pos)) {
			 			event.setCanceled(true);
			 			entity.setPositionAndUpdate(0.5d+pos.getX(), pos.getY(), 0.5d+pos.getZ());

						if (entity instanceof EntityLiving && trueSource instanceof EntityLivingBase) {
							EntityLiving living = (EntityLiving) entity;
							if (living.getAttackTarget() == null)
								living.setAttackTarget((EntityLivingBase) trueSource);
						}
			 			return true;
		    		}
		    	}
		 	}
		 	return false;
		}

		public void onEquip(ItemStack stack, EntityLivingBase entity, boolean takenOff) {
			if (entity != null && LockOn.isAutoLockOn(entity))
				LockOn.giveEffect(entity);
		}

		public void lockOnLookingAt(EntityPlayer player, ItemStack eye) {
			int cooldown = LockOn.getAutoLockOnCD(player);
			if (cooldown > 0) {
				player.sendStatusMessage(new TextComponentTranslation("item.sharingan.lock_on_cooldown", cooldown), true);
				return;
			}

			boolean isAutoLockOn = LockOn.isAutoLockOn(player);
			if (isAutoLockOn) {
				LockOn.unlockOnTarget(player);
				return;
			}

			EntityLivingBase target = null;
			if (LockOn.getLockedTarget(player) != null && player.isSneaking())
				target = LockOn.getLockedTarget(player);


			if (target == null) {
				RayTraceResult rtr = ProcedureUtils.objectEntityLookingAt(player, LockOn.getRange(eye), 1.5f);
				if (!(rtr.entityHit instanceof EntityLivingBase))
					return;
				target = (EntityLivingBase) rtr.entityHit;
			}
			
			int time = LockOn.getAutoLockOnMaxTime(player) - LockOn.getAutoLockOnTime(player);
			LockOn.lockOnTarget(player, target, time * 20);
			player.getEntityData().setBoolean(LockOn.autoLockOnEntity, true);
			ProcedureSync.EntityNBTTag.sendToSelf((EntityPlayerMP) player, LockOn.autoLockOnEntity, true);
			LockOn.giveEffect(player);
		}

		@Override
		public void setDamage(ItemStack stack, int damage) {
			if (this.canDamage) {
				super.setDamage(stack, damage);
			}
		}

		public void forceDamage(ItemStack stack, int damage) {
			super.setDamage(stack, damage);
		}

		@Override
		public int getDamage(ItemStack stack) {
			int itemDamage = this.getMetadata(stack);
			if (itemDamage > this.getMaxDamage()) {
				itemDamage = this.getMaxDamage();
			}
			return itemDamage;
		}

		public SoundEvent getActivationSound(ItemStack eye){
			return Sounds.get("sharingansfx");
		}

		public SoundEvent getDeactivationSound(ItemStack eye) {
			return Sounds.get("sharingan_off");
		}

		@Override
		public void setOwner(ItemStack stack, EntityLivingBase entityIn) {
			super.setOwner(stack, entityIn);
			this.setColor(stack, entityIn.getRNG().nextInt());
		}

		@Override
		public void copyOwner(ItemStack toStack, ItemStack fromStack) {
			super.copyOwner(toStack, fromStack);
			if (toStack.getItem() instanceof Base && fromStack.getItem() instanceof Base) {
				this.setColor(toStack, ((Base)fromStack.getItem()).getColor(fromStack));
			}
		}

		public void setColor(ItemStack stack, int color) {
			if (!stack.hasTagCompound()) {
				stack.setTagCompound(new NBTTagCompound());
			}
			stack.getTagCompound().setInteger("color", (color & 0x00FFFFFF) | 0x20000000);
		}

		public int getColor(ItemStack stack) {
			return stack.hasTagCompound() ? stack.getTagCompound().getInteger("color") : 0;
		}

		protected int genjutsuCD;

		public boolean applyGenjutsu(EntityPlayer entity, double range, int durationSeconds) {

			if (ItemInton.Genjutsu.createJutsu(entity, null, range, durationSeconds * 20, genjutsuCD)) {
				genjutsuCD = 10 * 20;
				return true;
			}
			return false;
		}
		
		@Override
		public boolean onJutsuKey1(boolean is_pressed, ItemStack stack, EntityPlayer entity) {
			if (!is_pressed)
				return applyGenjutsu(entity, 8, 10);

			return false;
		}
		
	}

	public static boolean hasAny(EntityPlayer player) {
		return ProcedureUtils.hasAnyItemOfSubtype(player, Base.class);
	}

	public static boolean hasAnyMangekyo(EntityPlayer player, boolean checkIsOwner) {
		for (ItemStack stack : ProcedureUtils.getAllItemsOfSubType(player, Base.class)) {
			if ((!checkIsOwner || ((Base)stack.getItem()).isOwner(stack, player)) && ((Base)stack.getItem()).isMangekyo()) {
				return true;
			}
		}
		return false;
	}

	public static boolean wearingAny(EntityLivingBase entity) {
		return ItemDojutsu.getWorn(entity).getItem() instanceof Base;
	}

	public static boolean hasRinneganAwakenKey(ItemStack stack) {
		return stack.hasTagCompound() && stack.getTagCompound().getBoolean(RINNEGEN_AWAKENING_KEY);
	}
	public static boolean is(ItemStack stack) {
		return stack.getItem() instanceof Base;
	}

	public static boolean isBase(ItemStack stack) { //base 3 tomoe
		return stack.getItem() instanceof Base && ((Base) stack.getItem()).getTier(stack) == ItemDojutsu.Tier.SHARINGAN;
	}
	
	public static boolean isMangekyo(ItemStack stack) {
		return stack.getItem() instanceof Base && ((Base)stack.getItem()).isMangekyo();
	}

	public static boolean isEternal(ItemStack stack) {
		return stack.getItem() instanceof Base && ((Base) stack.getItem()).isEternal();
	}

	public static boolean isWearingMangekyo(EntityLivingBase entity) {
		return isMangekyo(ItemDojutsu.getWorn(entity));
	}

	public static boolean isWearingEternal(EntityLivingBase entity) {
		return isEternal(ItemDojutsu.getWorn(entity));
	}
	
	public static boolean isBlinded(ItemStack stack) {
		return stack.hasTagCompound() ? stack.getTagCompound().getBoolean("sharingan_blinded") : false;
	}

	public static boolean isDodgeEnabled(ItemStack stack) {
		return stack.hasTagCompound() ? stack.getTagCompound().getBoolean("dodge_enabled") : false;
	}

	public static void setDodgeEnabled(ItemStack stack, boolean dodge) {
		stack.getTagCompound().setBoolean("dodge_enabled", dodge);
	}

	public static boolean isBlinded(EntityPlayer entity) {
		if (entity.isCreative()) {
			return false;
		}
		int i = 0;
		List<ItemStack> list = ProcedureUtils.getAllItemsOfSubType(entity, ItemDojutsu.Base.class);
		for (ItemStack stack : list) {
			if (isBlinded(stack)) {
				++i;
			}
		}
		return !list.isEmpty() && i == list.size();
	}

	public static boolean isLowerTier(ItemStack higher, ItemStack lower) {
		if (!(higher.getItem() instanceof ItemSharingan.Base))
			return false;

		if (!(lower.getItem() instanceof ItemDojutsu.Base))
			return true;

		if (ItemRinneganTomoe.isTomoe(higher) || ItemRinnegan.isRinnegan(lower))
			return ItemRinnegan.isLowerTier(higher, lower);
		
		if (!(lower.getItem() instanceof ItemSharingan.Base))
			return true;

		ItemSharingan.Base higherS = (ItemSharingan.Base) higher.getItem();
		ItemSharingan.Base lowerS = (ItemSharingan.Base) lower.getItem();

		if (higherS.isEternal() && !lowerS.isEternal() || higherS.isMangekyo() && !lowerS.isMangekyo() || ItemRinneganTomoe.isTomoe(higher) && !ItemRinneganTomoe.isTomoe(lower))
			return true;

		return false;
	}

	public static class LockOn {
		private static final String autoLockOnEntity = "autoLockOnEntity";
		private static final String autoLockOnTime = "autoLockOnTime"; //seconds entity spent in autolock
		private static final String autoLockOnCD = "autoLockOnCD"; //seconds of CD

		private static final String shouldTargetLockOnEntity = "shouldTargetLockOnEntity";
		private static final String targetLockOnEntityId = "targetLockOnEntityId";
		private static final String targetLockOnEntityTicksRemaining = "targetLockOnEntityTicksRemaining";

		@SubscribeEvent
		public void onAttacked(LivingAttackEvent event) {
			if (event.getEntity().world.isRemote || !(event.getEntity() instanceof EntityLivingBase) || event.getSource().isUnblockable())
				return;
			
			EntityLivingBase entity = event.getEntityLiving();
			Entity attacker = event.getSource().getTrueSource();
			if (wearingAny(entity) && ItemJutsu.canTarget(entity) && !entity.isRiding() && attacker instanceof EntityLivingBase) {
				((Base) ItemDojutsu.getWorn(entity).getItem()).onAttackEvent(event, entity, attacker);
				if (entity instanceof EntityPlayer && !isAutoLockOn(entity) && attacker instanceof EntityLivingBase) 
					lockOnTarget(entity, (EntityLivingBase) attacker, 300);
			}
		}

		private static boolean isAutoLockOn(EntityLivingBase entity) {
			return entity.getEntityData().getBoolean(autoLockOnEntity);
		}

		private static int getAutoLockOnTime(EntityLivingBase entity) {
			return entity.getEntityData().getInteger(autoLockOnTime);
		}

		private static void setAutoLockOnTime(EntityLivingBase entity, int seconds) {
			entity.getEntityData().setInteger(autoLockOnTime, seconds);
			if (seconds == 0)
				entity.getEntityData().removeTag(autoLockOnEntity);
		}

		private static int getAutoLockOnCD(EntityLivingBase entity) {
			return entity.getEntityData().getInteger(autoLockOnCD);
		}

		private static void setAutoLockOnCD(EntityLivingBase entity, int seconds) {
			entity.getEntityData().setInteger(autoLockOnCD, seconds);
		}

		private static int getAutoLockOnMaxTime(EntityLivingBase entity) {
			ItemStack helmet = ItemDojutsu.getWorn(entity);
			
			int time = 30;
			if (ItemRinneganTomoe.isTomoe(helmet)) {
				if (ItemRinneganTomoe.eternalOn(helmet))
					time = 360;
				else if (ItemRinneganTomoe.sharinganOn(helmet))
					time = 180;
				else
					time = 60;
			} else if (ItemSharingan.isEternal(helmet))
				time = 180;
			else if (ItemSharingan.isMangekyo(helmet))
				time = 90;

			return time;
		}

		public static void giveEffect(EntityLivingBase player) {
			player.removePotionEffect(PotionLockOn.potion);

			int duration = LockOn.getAutoLockOnMaxTime(player) - LockOn.getAutoLockOnTime(player);
			player.addPotionEffect(new PotionEffect(PotionLockOn.potion, duration * 20));
		}

		public static double getRange(ItemStack eye) {
			double range = ModConfig.DOJUTSU.SHARINGAN_LOCK_ON_RANGE;

			if (ItemRinneganTomoe.isTomoe(eye))
				range *= 2;
			else if (ItemSharingan.isEternal(eye))
				range *= 1.5;
			else if (ItemSharingan.isMangekyo(eye))
				range *= 1.25;

			return MathHelper.clamp(range, 0, 256);
		}
		@SubscribeEvent
		public void onPlayerTick(TickEvent.PlayerTickEvent event) {
			if (event.player.world.isRemote || event.phase != TickEvent.Phase.END)
				return;
			
			EntityPlayer entity = event.player;

			if (getAutoLockOnCD(entity) > 0) { //decrement CD every second
				if (entity.ticksExisted % 20 == 0)
					setAutoLockOnCD(entity, getAutoLockOnCD(entity) - 1);
				return;
			}
			
			if (isAutoLockOn(entity)) {
				if (entity.ticksExisted % 20 == 0) {
					ItemStack eye = ItemDojutsu.getWorn(entity);
					int time = getAutoLockOnTime(entity);
					EntityLivingBase target = getLockedTarget(entity);

					if (target == null || !target.isEntityAlive() || !(eye.getItem() instanceof Base) || target.getDistance(entity) > getRange(eye)) {
						unlockOnTarget(entity);
						return;
					}

					boolean reachedMaxTime = time++ >= getAutoLockOnMaxTime(entity);
					if (reachedMaxTime) {
						unlockOnTarget(entity);
						setAutoLockOnTime(entity, 0);
						setAutoLockOnCD(entity, ModConfig.DOJUTSU.SHARINGAN_LOCK_ON_COOLDOWN);
						return;
					}

					setAutoLockOnTime(entity, time);
				}
				return;
			} else if (getAutoLockOnTime(entity) > 0) { //if deactivated and not on CD, decrement every 2 sec
				if (entity.ticksExisted % 40 == 0)
					setAutoLockOnTime(entity, getAutoLockOnTime(entity) - 1);
				return;
			}

			if (hasTargetLockOnEntity(entity)) {
				int remaining = targetLockTicksRemaining(entity);
				EntityLivingBase target = getLockedTarget(entity);
				if (!entity.world.isRemote && (remaining <= 0 || target == null || !target.isEntityAlive() || target.getDistanceSq(entity) > 1024d)) {
					unlockOnTarget(entity);
				} else if (target != null) {
					lockOnTarget(entity, target, remaining - 1);
				}
			}
		}

		@SideOnly(Side.CLIENT)
		@SubscribeEvent
		public void onPlayerTick(TickEvent.ClientTickEvent event) {
			if ( Minecraft.getMinecraft().player == null)
				return;

			EntityPlayer player = Minecraft.getMinecraft().player;
			if (!shouldLockOnTarget(player))
				return;

			EntityLivingBase target = getLockedTarget(player);
			if (target != null) {
				Vec3d vec2 = target.getPositionEyes(1f).subtract(player.getPositionEyes(1f));
				player.rotationYaw = ProcedureUtils.getYawFromVec(vec2);
				player.rotationPitch = ProcedureUtils.getPitchFromVec(vec2);

				ProcedureOnLivingUpdate.setGlowingFor(target, 2);
			}
		}
		@SideOnly(Side.CLIENT)
		@SubscribeEvent
		public void onMouseEvent(MouseEvent event) {
			EntityPlayer player = Minecraft.getMinecraft().player;
			if (FMLClientHandler.instance().isGUIOpen(net.minecraft.client.gui.GuiChat.class) || player == null) {
				return;
			}
			if (event.getButton() == 1 && hasTargetLockOnEntity(player)) {
				//boolean flag = player.getEntityData().getBoolean("shouldTargetLockOnEntity");
				boolean flag = !event.isButtonstate();
			//	player.getEntityData().setBoolean(shouldTargetLockOnEntity, !flag);
			//	ProcedureSync.EntityNBTTag.sendToServer(player, shouldTargetLockOnEntity, !flag);
			}
		}

		@SubscribeEvent
		public void onEntitySpawn(EntityJoinWorldEvent event) {
			if (event.getEntity() instanceof EntityPlayerMP) {
				unlockOnTarget((EntityLivingBase) event.getEntity());
			}
		}

		private static void lockOnTarget(EntityLivingBase entity, EntityLivingBase target, int ticks) {
			if (!entity.world.isRemote && target != null) {
				entity.getEntityData().setInteger(targetLockOnEntityId, target.getEntityId());
				entity.getEntityData().setInteger(targetLockOnEntityTicksRemaining, ticks);
				if (entity instanceof EntityPlayerMP) {
					ProcedureSync.EntityNBTTag.sendToSelf((EntityPlayerMP)entity, targetLockOnEntityId, target.getEntityId());
				}
			}
		}

		private static void unlockOnTarget(EntityLivingBase entity) {
			if (!entity.world.isRemote) {
				entity.getEntityData().removeTag(targetLockOnEntityId);
				entity.getEntityData().removeTag(targetLockOnEntityTicksRemaining);
				entity.getEntityData().removeTag(shouldTargetLockOnEntity);
				entity.getEntityData().removeTag(autoLockOnEntity);
				entity.removePotionEffect(PotionLockOn.potion);
				if (entity instanceof EntityPlayerMP) {
					ProcedureSync.EntityNBTTag.sendToSelf((EntityPlayerMP)entity, targetLockOnEntityId);
					ProcedureSync.EntityNBTTag.sendToSelf((EntityPlayerMP)entity, shouldTargetLockOnEntity);
					ProcedureSync.EntityNBTTag.sendToSelf((EntityPlayerMP) entity, autoLockOnEntity);
				}
			}
		}

		private static boolean shouldLockOnTarget(EntityLivingBase entity) {
			return entity.getEntityData().getBoolean(shouldTargetLockOnEntity) || entity.getEntityData().getBoolean(autoLockOnEntity);
		}

		private static boolean hasTargetLockOnEntity(EntityLivingBase entity) {
			return entity.getEntityData().hasKey(targetLockOnEntityId);
		}

		@Nullable
		private static EntityLivingBase getLockedTarget(EntityLivingBase entity) {
			Entity target = entity.world.getEntityByID(entity.getEntityData().getInteger(targetLockOnEntityId));
			return target instanceof EntityLivingBase ? (EntityLivingBase)target : null;
		}

		private static int targetLockTicksRemaining(EntityLivingBase entity) {
			return entity.getEntityData().getInteger(targetLockOnEntityTicksRemaining);
		}
	}

	@Override
	public void initElements() {
		ItemArmor.ArmorMaterial enuma = EnumHelper.addArmorMaterial("SHARINGAN", "narutomod:sharingan_",
		 1024, new int[]{2, 5, 6, 10}, 0, null, 0.0F);
		this.elements.items.add(() -> new Base(enuma) {
			public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
				return "narutomod:textures/sharinganhelmet.png";
			}
		}.setUnlocalizedName("sharinganhelmet").setRegistryName("sharinganhelmet").setCreativeTab(TabModTab.tab));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(helmet, 0, new ModelResourceLocation("narutomod:sharinganhelmet", "inventory"));
	}


	public enum Type {
		BASE,
		AMATERASU,
		KAMUI;
	}
}

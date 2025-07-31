
package net.narutomod.item;

import net.minecraft.item.Item;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;

import net.minecraft.world.World;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.MobEffects;

import net.narutomod.goatee.client.model.ModelDojutsu;
import net.narutomod.goatee.data.DojutsuData;
import net.narutomod.goatee.data.NarutoData;
import net.narutomod.goatee.data.SideData;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.NarutomodModVariables;
import net.narutomod.ElementsNarutomodMod;

import java.util.UUID;
import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class ItemDojutsu extends ElementsNarutomodMod.ModElement {
	public ItemDojutsu(ElementsNarutomodMod instance) {
		super(instance, 447);
	}

	public abstract static class Base extends ItemArmor {
		public DojutsuData data = new DojutsuData(this);
		
		@SideOnly(Side.CLIENT)
		private ModelBiped armorModel;

		public Base(ItemArmor.ArmorMaterial material) {
			super(material, 0, EntityEquipmentSlot.HEAD);
		}

		@Override
		public void onArmorTick(World world, EntityPlayer entity, ItemStack itemstack) {
			if (world.isRemote)
				return;
			
			super.onArmorTick(world, entity, itemstack);
			if (!this.isOwner(itemstack, entity) && !entity.isCreative()) {
				UUID uuid = ProcedureUtils.getOwnerId(itemstack);
				if (uuid != null && !uuid.equals(entity.getEntityData().getUniqueId("lastWornForeignDojutsu"))) {
					entity.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 1200, 0, false, false));
					entity.getEntityData().setUniqueId("lastWornForeignDojutsu", uuid);
				}
				if (entity.ticksExisted % 20 == 5) {
					net.narutomod.Chakra.pathway(entity).consume(15.0d);
				}
			}
			if (entity.ticksExisted % 20 == 1) {
				entity.getEntityData().setLong(NarutomodModVariables.MostRecentWornDojutsuTime, world.getTotalWorldTime());
			}
		}

		@SideOnly(Side.CLIENT)
		@Override
		public ModelBiped getArmorModel(EntityLivingBase living, ItemStack stack, EntityEquipmentSlot slot, ModelBiped defaultModel) {
			if (this.armorModel == null) {
				this.armorModel = new ClientModel().new ModelHelmetSnug();
			}
			this.armorModel.isSneak = living.isSneaking();
			this.armorModel.isRiding = living.isRiding();
			this.armorModel.isChild = living.isChild();
			return this.armorModel;
		}

		@SideOnly(Side.CLIENT)
		public ModelDojutsu model;

		public boolean useAdvancedModel() {
			return false;
		}
		
		public String getRightEyeTexture(ItemStack stack, Entity entity, SideData side) {
			return getArmorTexture(stack, entity, EntityEquipmentSlot.HEAD, null);
		}

		public String getLeftEyeTexture(ItemStack stack, Entity entity, SideData side) {
			return getArmorTexture(stack, entity, EntityEquipmentSlot.HEAD, null);
		}

		public String getRinnesharinganTexture(ItemStack stack, Entity entity) {
			return getArmorTexture(stack, entity, EntityEquipmentSlot.HEAD, null);
		}

		public SideData getSideData(SideData.Side side, ItemStack stack) {
			return new SideData(side, stack, this);
		}
		
		public boolean isOwner(ItemStack stack, EntityLivingBase entity) {
			return ProcedureUtils.isOriginalOwner(entity, stack);
		}

		@Nullable
		public EntityLivingBase getOwner(ItemStack stack, World world) {
			UUID uuid = ProcedureUtils.getOwnerId(stack);
			Entity entity = uuid != null ? ProcedureUtils.getEntityFromUUID(world, uuid) : null;
			return entity instanceof EntityLivingBase ? (EntityLivingBase)entity : null;
		}

		public void copyOwner(ItemStack toStack, ItemStack fromStack) {
			UUID uuid = ProcedureUtils.getOwnerId(fromStack);
			if (uuid != null) {
				ProcedureUtils.setOriginalOwner(toStack, uuid);
				toStack.setStackDisplayName(fromStack.getDisplayName());
			}
		}
		
		public void setOwner(ItemStack stack, EntityLivingBase entityIn) {
			ProcedureUtils.setOriginalOwner(entityIn, stack);
			stack.setStackDisplayName(entityIn.getName() + "'s " + stack.getItem().getItemStackDisplayName(stack));
		}

		@Override
		public void onUpdate(ItemStack stack, World world, Entity entity, int par4, boolean par5) {
			if (entity instanceof EntityPlayer && ((EntityPlayer)entity).isCreative() && ProcedureUtils.getOwnerId(stack) == null) {
				this.setOwner(stack, (EntityLivingBase)entity);
				stack.setStackDisplayName(stack.getDisplayName() + " (creative)");
			}
			if (entity.ticksExisted % 20 == 1 && stack.hasTagCompound() && stack.getTagCompound().hasKey("ench", 9)) {
				stack.getTagCompound().removeTag("ench");
			}
			super.onUpdate(stack, world, entity, par4, par5);
		}

		public void onEquip(ItemStack stack, EntityLivingBase entity, boolean takenOff) {
		}
		
		public abstract Type getType();

		public abstract Tier getTier(ItemStack stack);

		public boolean canBuildInKamui(ItemStack stack) {
			if (stack.getTagCompound().hasKey("kamuiCanBuild"))
				return true;

			return false;
		}

		public SoundEvent getActivationSound(ItemStack eye) {
			return null;
		}

		public SoundEvent getDeactivationSound(ItemStack eye) {
			return null;
		}
		
		public boolean onJutsuKey1(boolean is_pressed, ItemStack stack, EntityPlayer player) {
			return false;
		}

		public boolean onJutsuKey2(boolean is_pressed, ItemStack stack, EntityPlayer player) {
			return false;
		}

		public boolean onJutsuKey3(boolean is_pressed, ItemStack stack, EntityPlayer player) {
			return false;
		}

		public boolean onJutsuKey4(byte pressType, ItemStack stack, EntityPlayer player) {
			return false;
		}

		public boolean onJutsuKey5(byte pressType, ItemStack stack, EntityPlayer player) {
			return false;
		}

		public boolean onJutsuKey6(byte pressType, ItemStack stack, EntityPlayer player) {
			return false;
		}
		
		public boolean onJutsuKey7(byte pressType, ItemStack stack, EntityPlayer player) {
			return false;
		}
		
		public boolean onSwitchJutsuKey(boolean is_pressed, ItemStack stack, EntityPlayer player) {
			return false;
		}
	}

	public static void playActivationSound(ItemStack eye, EntityLivingBase entity) {
		if (eye.getItem() instanceof Base) {
			SoundEvent sound = ((Base) eye.getItem()).getActivationSound(eye);
			if (sound != null)
				entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, sound, SoundCategory.NEUTRAL, 2, 1);
		}
	}

	public static void playDeactivationSound(ItemStack eye, EntityLivingBase entity) {
		if (eye.getItem() instanceof Base) {
			SoundEvent sound = ((Base) eye.getItem()).getDeactivationSound(eye);
			if (sound != null)
				entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, sound, SoundCategory.NEUTRAL, 2, 1);
		}
	}
	public static boolean hasAnyDojutsu(EntityPlayer player) {
		return ProcedureUtils.hasAnyItemOfSubtype(player, Base.class) || NarutoData.get(player).dojutsuWheel.hasAny((stack) -> stack.getItem() instanceof Base);
	}

	public static boolean wearingAnyDojutsu(EntityLivingBase entity) {
		return ItemDojutsu.getWorn(entity).getItem() instanceof Base;
	}

	public static boolean is(ItemStack stack) {
		return stack.getItem() instanceof Base;
	}

	public static boolean is(ItemStack stack, Class<?> outerClass) {
		return stack.getItem().getClass().getEnclosingClass() == outerClass;
	}
	
	public static ItemStack getWorn(EntityLivingBase entity) {
		if (entity instanceof EntityPlayer) {
			NarutoData data = NarutoData.get((EntityPlayer) entity);
			if (data != null && !data.getDojutsuSlot().isEmpty())
				return data.getDojutsuSlot();
		}
		
		return entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
	}

	public static DojutsuData getWornData(EntityLivingBase entity) {
		ItemStack worn = getWorn(entity);
		if (worn.getItem() instanceof Base)
			return ((Base) worn.getItem()).data;

		return null;
	}

	public static long getMostRecentWornTime(EntityLivingBase entity) {
		return entity.getEntityData().getLong(NarutomodModVariables.MostRecentWornDojutsuTime);
	}

	public static boolean isLowerTier(ItemStack higher, ItemStack lower) {
		if (higher.getItem() instanceof ItemSharingan.Base)
			return ItemSharingan.isLowerTier(higher, lower);

		if (ItemRinnegan.isRinnegan(higher))
			return ItemRinnegan.isLowerTier(higher, lower);

		return false;
	}

	public static boolean hasSameData(ItemStack higher, ItemStack lower) {
		if (ItemRinnegan.isRinnegan(higher))
			return ItemRinnegan.hasSameData(higher, lower);

		return true;
	}

	public static boolean isDojutsu(ItemStack stack) {
		return stack.getItem() instanceof ItemDojutsu.Base;
	}
	
public static boolean dropOnForceDojutsuDrop(Item eye){
		return eye == ItemByakugan.helmet || eye == ItemSharingan.helmet||eye == ItemMangekyoSharingan.helmet||eye == ItemMangekyoSharinganObito.helmet;
}

	public enum Type {
		BYAKUGAN,
		SHARINGAN,
		RINNE_TENSEI;
	}

	public static class Hook {
		@SubscribeEvent
		public void onEquipmentChange(LivingEquipmentChangeEvent event) {
			if (event.getEntity().world.isRemote || !(event.getEntity() instanceof EntityLivingBase) || event.getSlot() != EntityEquipmentSlot.HEAD)
				return;

			ItemStack to = event.getTo(), from = event.getFrom();
			if (from.getItem().equals(to.getItem()) && hasSameData(from, to) || !ItemDojutsu.is(to) && !ItemDojutsu.is(from))
				return;

			EntityLivingBase entity = (EntityLivingBase) event.getEntity();
			if (to.getItem() instanceof Base) { //eye activation sound
				((Base) to.getItem()).onEquip(to, entity, false);
				
				if (ItemDojutsu.isLowerTier(from, to)) //don't play sound when descending into a lower tier sharingan
					ItemDojutsu.playDeactivationSound(from, entity);
				else
					ItemDojutsu.playActivationSound(to, entity); 
			} else if (from.getItem() instanceof Base) {//eye deactivation sound
				((Base) from.getItem()).onEquip(to, entity, true);
				ItemDojutsu.playDeactivationSound(from, entity);
			}
			
		}

		@SubscribeEvent
		public void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
			EntityPlayer player = event.getEntityPlayer();
			if (player.world.isRemote)
				return;
			ItemStack toEquip = event.getItemStack();

			if (toEquip.getItem() instanceof Base) {
				ItemStack wornHelmet = ItemDojutsu.getWorn(player);
				if (!wornHelmet.isEmpty()) {
					int slot = ProcedureUtils.getSlotFor(toEquip, player);

					boolean emptyBefore = NarutoData.get(player).getDojutsuSlot().isEmpty(); //DOJUTSU SLOT COMPAT
					player.setItemStackToSlot(EntityEquipmentSlot.HEAD, toEquip.copy());
					boolean emptyAfter = NarutoData.get(player).getDojutsuSlot().isEmpty();

					if (!(emptyBefore && !emptyAfter))
						player.inventory.mainInventory.set(slot, wornHelmet);


					if (emptyBefore && !emptyAfter && !isDojutsu(wornHelmet))
						toEquip.shrink(1);

				}
			}
		}
	}

	@Override
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new Hook());
	}

	public static Tier getTier(ItemStack stack) {
		if (!ItemDojutsu.is(stack))
			return Tier.NONE;

		return ((ItemDojutsu.Base) stack.getItem()).getTier(stack);
	}

	public enum Tier {
		NONE(0),
		BYAKUGAN(1),
		SHARINGAN(1),
		MANGEKYO(2),
		ETERNAL(3),
		RINNEGAN(4),
		RINNESHARINGAN(5);

		public final int level;

		Tier(int level) {
			this.level = level;
		}

		public int getLevel() {
			return level;
		}

		public boolean canRemoveGenjutsuFrom(ItemDojutsu.Tier caster) {
			if (caster.level <= SHARINGAN.level)  //no dojutsu or higher removes sharingan 3 tomoe
				return true;
			return this.level >= caster.level - 1; //sharingan+ removes mangekyo, mangekyo+ removes eternal, eternal+ removes rinnegan, rinne+ removes rinnesharin
		}
	}

	public static class ClientModel {
		@SideOnly(Side.CLIENT)
		public class ModelHelmetSnug extends ModelBiped {
			protected final ModelRenderer onface;
			protected final ModelRenderer hornRight;
			protected final ModelRenderer hornLeft;
			protected final ModelRenderer hornMiddle;
			private final ModelRenderer highlight; //actual eyes
			private final ModelRenderer forehead; //kekkei mora rinnesharin
			protected boolean headHide;
			protected boolean headwearHide;
			protected boolean headwearShine;
			protected boolean highlightHide;
			protected boolean foreheadHide;
			protected boolean isSo6;
	
			public ModelHelmetSnug() {
				this.textureWidth = 64;
				this.textureHeight = 16;

				bipedHead = new ModelRenderer(this);
				bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedHead.cubeList.add(new ModelBox(this.bipedHead, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.01F, false));

				onface = new ModelRenderer(this);
				onface.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedHead.addChild(onface);
				onface.cubeList.add(new ModelBox(onface, 32, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.01F, false));

				hornRight = new ModelRenderer(this);
				hornRight.setRotationPoint(-2.5F, -6.0F, -4.0F);
				bipedHead.addChild(hornRight);
				setRotationAngle(hornRight, 0.5236F, 0.3491F, -0.1309F);
				hornRight.cubeList.add(new ModelBox(hornRight, 0, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.1F, false));
		
				ModelRenderer bone2 = new ModelRenderer(this);
				bone2.setRotationPoint(0.0F, -1.0F, 0.0F);
				hornRight.addChild(bone2);
				setRotationAngle(bone2, -0.1745F, 0.0F, 0.0F);
				bone2.cubeList.add(new ModelBox(bone2, 0, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.2F, false));
		
				ModelRenderer bone3 = new ModelRenderer(this);
				bone3.setRotationPoint(0.0F, -1.0F, 0.0F);
				bone2.addChild(bone3);
				setRotationAngle(bone3, -0.1745F, 0.0F, 0.0F);
				bone3.cubeList.add(new ModelBox(bone3, 0, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.1F, false));
		
				ModelRenderer bone4 = new ModelRenderer(this);
				bone4.setRotationPoint(0.0F, -0.9F, 0.0F);
				bone3.addChild(bone4);
				setRotationAngle(bone4, -0.1745F, 0.0F, 0.0F);
				bone4.cubeList.add(new ModelBox(bone4, 0, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.0F, false));
		
				hornLeft = new ModelRenderer(this);
				hornLeft.setRotationPoint(2.5F, -6.0F, -4.0F);
				bipedHead.addChild(hornLeft);
				setRotationAngle(hornLeft, 0.5236F, -0.3491F, 0.1309F);
				hornLeft.cubeList.add(new ModelBox(hornLeft, 0, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.1F, true));
		
				ModelRenderer bone5 = new ModelRenderer(this);
				bone5.setRotationPoint(0.0F, -1.0F, 0.0F);
				hornLeft.addChild(bone5);
				setRotationAngle(bone5, -0.1745F, 0.0F, 0.0F);
				bone5.cubeList.add(new ModelBox(bone5, 0, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.2F, true));
		
				ModelRenderer bone6 = new ModelRenderer(this);
				bone6.setRotationPoint(0.0F, -1.0F, 0.0F);
				bone5.addChild(bone6);
				setRotationAngle(bone6, -0.1745F, 0.0F, 0.0F);
				bone6.cubeList.add(new ModelBox(bone6, 0, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.1F, true));
		
				ModelRenderer bone7 = new ModelRenderer(this);
				bone7.setRotationPoint(0.0F, -0.9F, 0.0F);
				bone6.addChild(bone7);
				setRotationAngle(bone7, -0.1745F, 0.0F, 0.0F);
				bone7.cubeList.add(new ModelBox(bone7, 0, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.0F, true));

				bipedHeadwear = new ModelRenderer(this);
				bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedHeadwear.cubeList.add(new ModelBox(this.bipedHeadwear, 32, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.4F, false));

				hornMiddle = new ModelRenderer(this);
				hornMiddle.setRotationPoint(0.0F, -6.25F, -3.75F);
				bipedHeadwear.addChild(hornMiddle);
				setRotationAngle(hornMiddle, 0.5236F, 0.0F, 0.0F);
				
		
				ModelRenderer bone13 = new ModelRenderer(this);
				bone13.setRotationPoint(0.0F, 0.0F, 0.0F);
				hornMiddle.addChild(bone13);
				setRotationAngle(bone13, 0.0F, -0.7854F, 0.0F);
				bone13.cubeList.add(new ModelBox(bone13, 0, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.1F, false));
		
				ModelRenderer bone8 = new ModelRenderer(this);
				bone8.setRotationPoint(0.0F, -1.0F, 0.0F);
				bone13.addChild(bone8);
				setRotationAngle(bone8, -0.0873F, 0.0F, 0.0873F);
				bone8.cubeList.add(new ModelBox(bone8, 0, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.2F, false));
		
				ModelRenderer bone9 = new ModelRenderer(this);
				bone9.setRotationPoint(0.0F, -1.0F, 0.0F);
				bone8.addChild(bone9);
				setRotationAngle(bone9, -0.0873F, 0.0F, 0.0873F);
				bone9.cubeList.add(new ModelBox(bone9, 0, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.1F, false));
		
				ModelRenderer bone10 = new ModelRenderer(this);
				bone10.setRotationPoint(0.0F, -0.9F, 0.0F);
				bone9.addChild(bone10);
				setRotationAngle(bone10, -0.0873F, 0.0F, 0.0873F);
				bone10.cubeList.add(new ModelBox(bone10, 0, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.0F, false));
		
				ModelRenderer bone11 = new ModelRenderer(this);
				bone11.setRotationPoint(0.0F, -0.8F, 0.0F);
				bone10.addChild(bone11);
				setRotationAngle(bone11, -0.0873F, 0.0F, 0.0873F);
				bone11.cubeList.add(new ModelBox(bone11, 0, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.15F, false));
		
				ModelRenderer bone12 = new ModelRenderer(this);
				bone12.setRotationPoint(0.0F, -0.6F, 0.0F);
				bone11.addChild(bone12);
				setRotationAngle(bone12, -0.0873F, 0.0F, 0.0873F);
				bone12.cubeList.add(new ModelBox(bone12, 0, 4, -0.5F, -0.9F, -0.5F, 1, 1, 1, -0.3F, false));

				this.highlight = new ModelRenderer(this);
				this.highlight.setRotationPoint(0.0F, 0.0F, 0.0F);
				this.highlight.cubeList.add(new ModelBox(this.highlight, 24, 0, -4.0F, -8.0F, -4.06F, 8, 8, 0, 0.0F, false));
				//this.bipedHead.addChild(this.highlight);
				this.forehead = new ModelRenderer(this);
				this.forehead.setRotationPoint(0.0F, 0.0F, 0.0F);
				this.forehead.cubeList.add(new ModelBox(this.forehead, 0, 0, -1.94F, -6.62F, -4.15F, 4, 4, 0, 0.0F, false));
			}
	
			@Override
			public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
				this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
				GlStateManager.pushMatrix();
				GlStateManager.enableBlend();
				GlStateManager.alphaFunc(0x204, 0.01f);
				if (entityIn.isSneaking()) {
					GlStateManager.translate(0.0F, 0.2F, 0.0F);
				}

				if (isSo6) {
					if (this.headwearShine) {
						GlStateManager.disableLighting();
						OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
					}
					
					this.bipedHead.render(scale);

					if (!this.foreheadHide) {
						this.copyModelAngles(this.bipedHead, this.forehead);
						this.forehead.render(scale);
					}

					if (!this.highlightHide) {
						this.copyModelAngles(this.bipedHead, this.highlight);
						this.highlight.render(scale);
					}
					if (!this.headwearHide) {
						this.copyModelAngles(this.bipedHead, this.bipedHeadwear);
						this.bipedHeadwear.render(scale);
					}
					if (this.headwearShine) {
						int i = entityIn.getBrightnessForRender();
						OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) (i % 65536), (float) (i / 65536));
						GlStateManager.enableLighting();
					}
				} else {
				if (!this.headHide) {
					this.bipedHead.render(scale);
				}
				if (!this.headwearHide) {
					if (this.headwearShine) {
						GlStateManager.disableLighting();
						OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
					}
					this.bipedHeadwear.render(scale);
					if (this.headwearShine) {
						int i = entityIn.getBrightnessForRender();
						OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)(i % 65536), (float)(i / 65536));
						GlStateManager.enableLighting();
					}
				}
				if (this.bipedHead.showModel) {
					if (!this.highlightHide) {
						GlStateManager.disableLighting();
						OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
						this.copyModelAngles(this.bipedHead, this.highlight);
						this.highlight.render(scale);
						int i = entityIn.getBrightnessForRender();
						OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)(i % 65536), (float)(i / 65536));
						GlStateManager.enableLighting();
					}
					if (!this.foreheadHide) {
						this.copyModelAngles(this.bipedHead, this.forehead);
						this.forehead.render(scale);
					}
				}
				}
				GlStateManager.alphaFunc(0x204, 0.1f);
				GlStateManager.disableBlend();
				GlStateManager.popMatrix();
			}

			public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
				modelRenderer.rotateAngleX = x;
				modelRenderer.rotateAngleY = y;
				modelRenderer.rotateAngleZ = z;
			}
		}
	}
}

package net.narutomod.item;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.narutomod.Chakra;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.ModConfig;
import net.narutomod.PlayerTracker;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.entity.EntityKingOfHell;
import net.narutomod.entity.EntityPretaShield;
import net.narutomod.entity.EntitySusanooBase;
import net.narutomod.entity.EntityTenTails;
import net.narutomod.goatee.client.Sounds;
import net.narutomod.goatee.client.model.ModelDojutsu;
import net.narutomod.goatee.data.sidedata.SideData;
import net.narutomod.gui.GuiNinjaScroll;
import net.narutomod.potion.PotionReach;
import net.narutomod.potion.PotionSpaceInversion;
import net.narutomod.procedure.*;
import net.narutomod.world.WorldKamuiDimension;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static net.narutomod.item.ItemRinnegan.isRinnegan;
import static net.narutomod.item.ItemRinnegan.isRinnesharinganActivated;
import static net.narutomod.item.ItemSharingan.isBlinded;

@ElementsNarutomodMod.ModElement.Tag
public class ItemRinneganTomoe extends ElementsNarutomodMod.ModElement {

    @GameRegistry.ObjectHolder("narutomod:rinnegantomoehelmet")
    public static final Item helmet = null;

    protected static final UUID RINNEGANTOMOE_MODIFIER = UUID.fromString("135da083-a632-1488-85bd-2281f15ca7e0");
    public static final double ETERNAL_CHAKRA_USAGE = 1d; // per tick
    
    public ItemRinneganTomoe(ElementsNarutomodMod instance) {
        super(instance, 20000);
    }

    public static double getEternalChakraUsage(ItemStack stack, EntityLivingBase entity) {
        return ((ItemDojutsu.Base) stack.getItem()).isOwner(stack, entity) ? ETERNAL_CHAKRA_USAGE : ETERNAL_CHAKRA_USAGE * 3;
    }
    
    public void initElements() {
        ItemArmor.ArmorMaterial enuma = EnumHelper.addArmorMaterial("RINNEGANTOMOE", "narutomod:rinnegantomoe_", 25, new int[]{2, 5, 6, 15}, 0, null, 2.0F);
        this.elements.items.add(() -> new Base(enuma).setUnlocalizedName("rinnegantomoehelmet").setRegistryName("rinnegantomoehelmet").setCreativeTab(TabModTab.tab));
    }

    public static class Base extends ItemSharingan.Base {
        public Base(ItemArmor.ArmorMaterial material) {
            super(material);
        }

        @Override
        public ItemDojutsu.Tier getTier(ItemStack stack) {
            if (ItemRinnegan.isRinnesharinganActivated(stack))
                return ItemDojutsu.Tier.RINNESHARINGAN;
            
            return ItemDojutsu.Tier.RINNEGAN;
        }
        public SideData getSideData(SideData.Side side, ItemStack stack) {
            return new SideData(side, stack, this);
        }

        public void onArmorTick(World world, EntityPlayer player, ItemStack itemstack) {
            super.onArmorTick(world, player, itemstack);
            if (world.isRemote)
                return;

            boolean flag = player.capabilities.allowFlying || player.isCreative() || player.dimension == WorldKamuiDimension.DIMID;
            if (player.capabilities.allowFlying != flag) {
                player.capabilities.allowFlying = flag;
                player.sendPlayerAbilities();
            }
            if (player.getEntityData().getBoolean("kamui_teleport")) {
                Chakra.pathway(player).consume(ItemMangekyoSharinganObito.getTeleportChakraUsage(player));
            }
            if (player.getEntityData().getBoolean("kamui_intangible")) {
                Chakra.pathway(player).consume(ItemMangekyoSharinganObito.getIntangibleChakraUsage(player));
                ProcedureWhenPlayerAttcked.setInvulnerable(player, 2);
            }

            applyEffects(player, itemstack);

            int x = (int) player.posX;
            int y = (int) player.posY;
            int z = (int) player.posZ;
            HashMap $_dependencies = new HashMap();
            $_dependencies.put("entity", player);
            $_dependencies.put("x", x);
            $_dependencies.put("y", y);
            $_dependencies.put("z", z);
            $_dependencies.put("itemstack", itemstack);
            $_dependencies.put("world", world);
            ProcedureRinneganHelmetTickEvent.executeProcedure($_dependencies);
        }

        public void applyEffects(EntityPlayer player, ItemStack itemstack) {
            if (player.ticksExisted % 60 == 0) {
                int tomoeStatus = getTomoeStatus(itemstack);

                if (tomoeStatus == SHARINGAN_ON) {
                    player.addPotionEffect(new PotionEffect(MobEffects.SPEED, 100, 9, false, false));
                    player.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 100, 4, false, false));
                    player.addPotionEffect(new PotionEffect(MobEffects.HASTE, 100, 4, false, false));
                } else if (tomoeStatus == ETERNAL_ON) {
                    player.addPotionEffect(new PotionEffect(MobEffects.SPEED, 100, 15, false, false));
                    player.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 100, 8, false, false));
                    player.addPotionEffect(new PotionEffect(MobEffects.HASTE, 100, 6, false, false));
                    player.addPotionEffect(new PotionEffect(PotionReach.potion, 100, 1, false, false));

                    if (!isRinnesharinganActivated(itemstack))
                        Chakra.pathway(player).consume(getEternalChakraUsage(itemstack, player));
                }
            }
        }

        public void onUpdate(ItemStack itemstack, World world, Entity entity, int par4, boolean par5) {
            super.onUpdate(itemstack, world, entity, par4, par5);
            if (world.isRemote)
                return;


            if (entity.ticksExisted % 20 == 0) {
                UUID uuid = ProcedureUtils.getUniqueId(itemstack, "KoH_id");
                if (uuid != null) {
                    Entity koh = ((WorldServer) world).getEntityFromUuid(uuid);
                    if (!(koh instanceof EntityKingOfHell.EntityCustom) || !koh.isEntityAlive()) {
                        ProcedureUtils.removeUniqueIdTag(itemstack, "KoH_id");
                    }
                }

                if (entity instanceof EntityPlayer) {
                    EntityPlayer player = (EntityPlayer) entity;
                    ItemStack helmetStack = ItemDojutsu.getWorn(player);
                    GuiNinjaScroll.enableJutsu(player, (ItemJutsu.Base) ItemYoton.block, ItemYoton.SEALING9D, isRinnegan(helmetStack));
                    GuiNinjaScroll.enableJutsu(player, (ItemJutsu.Base) ItemYoton.block, ItemYoton.SEALING10, isRinnegan(helmetStack) && EntityTenTails.getBijuManager().isAddedToWorld(player.world));

                    if (!(isRinnegan(helmetStack))) {
                        player.inventory.clearMatchingItems(ItemAsuraCanon.block, -1, -1, null);
                        if (player.getRidingEntity() instanceof EntityPretaShield.EntityCustom) {
                            player.getRidingEntity().setDead();
                        }
                    }
                }
            }

            if (itemstack.hasTagCompound() && itemstack.getTagCompound().hasKey("amenotejikaraDisable")) { //important as to not switch on the same shift click that stores target 
                int counter = itemstack.getTagCompound().getInteger("amenotejikaraDisable") - 1;
                itemstack.getTagCompound().setInteger("amenotejikaraDisable", counter);
                if (counter <= 0) {
                    itemstack.getTagCompound().removeTag("amenotejikaraDisable");
                }
            }
        }

        @Override
        public boolean isMangekyo() {
            return true;
        }

        @Override
        public boolean isEternal() {
            return true;
        }

        public boolean canBuildInKamui(ItemStack stack) {
            return true;
        }

        public boolean useAdvancedModel() {
            return true;
        }

        @SideOnly(Side.CLIENT)
        @Override
        public ModelBiped getArmorModel(EntityLivingBase living, ItemStack stack, EntityEquipmentSlot slot, ModelBiped defaultModel) {
            ModelDojutsu model = (ModelDojutsu) super.getArmorModel(living, stack, slot, defaultModel);

            boolean isS06p = isRinnesharinganActivated(stack);
            model.isS06P = model.headwearShine = model.onface.showModel = isS06p;
            model.hornMiddle.showModel = false;
            model.foreheadHide = !isS06p || !(living instanceof EntityPlayer) || PlayerTracker.getNinjaLevel((EntityPlayer) living) < 180d;
            model.rinnesharinganBase = true;
                
            model.rightEye.showModel = !isBlinded(stack); //sharingan blindness
            return model;
        }

        public String getRightEyeTexture(ItemStack stack, Entity entity, SideData side) {
            if (isRinnesharinganActivated(stack))
                return "narutomod:textures/rinnesharingantomoehelmet.png";
            else if (sharinganOn(stack))
                return "narutomod:textures/sharinganhelmet.png";
            else if (eternalOn(stack))
                return "narutomod:textures/mangekyosharinganhelmet_eternal.png";
            
            //            side.setTextureHD(true);
            //            side.setOffsetX(0.2f);
            //            side.setOffsetY(-0.06f);
            //            side.setColor(0x55555a);
            //            side.setEyeBaseTexture("narutomod:textures/rinnegantomoehelmet_off.png"); "narutomod:textures/eye/base.png";//
            return "narutomod:textures/eye/blackpupils.png";

        }

        public String getLeftEyeTexture(ItemStack stack, Entity entity, SideData side) {
          return "narutomod:textures/rinnegantomoehelmet.png";
        }

        public String getRinnesharinganTexture(ItemStack stack, Entity entity) {
            return "narutomod:textures/rinnesharingantomoehelmet.png";
        }

        public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
            if (isRinnesharinganActivated(stack))
                return "narutomod:textures/rinnesharingantomoehelmet.png";
            else if (sharinganOn(stack))
                return "narutomod:textures/rinnegantomoehelmet_sharingan.png";
            else if (eternalOn(stack))
                return "narutomod:textures/rinnegantomoehelmet_eternal.png";

            return "narutomod:textures/rinnegantomoehelmet_off.png";
        }

        public int getColor(ItemStack stack) {
            if (isRinnesharinganActivated(stack)) //set rinnesharingan color here
                return 0xffffff;
            return super.getColor(stack);
        }

        public int getMaxDamage() {
            return 0;
        }

        public boolean isDamageable() {
            return false;
        }

        public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
            String status = "item.rinnegantomoehelmet.status_" + (sharinganOn(stack) ? "on" : eternalOn(stack) ? "eternal" : "off");
            tooltip.add(TextFormatting.DARK_GRAY + I18n.translateToLocal("item.rinnegantomoehelmet.sharingan_status") + TextFormatting.RED + I18n.translateToLocal(status));

            super.addInformation(stack, worldIn, tooltip, flagIn);
            if (isRinnesharinganActivated(stack))
                tooltip.add(TextFormatting.RED + I18n.translateToLocal("advancements.rinnesharinganactivated.title") + TextFormatting.WHITE);
            tooltip.add(TextFormatting.ITALIC + I18n.translateToLocal("key.mcreator.specialjutsu1") + ": " + TextFormatting.GRAY + I18n.translateToLocal("tooltip.mangekyo.amaterasu.jutsu1"));
            tooltip.add(TextFormatting.ITALIC + I18n.translateToLocal("key.mcreator.specialjutsu2") + ": " + TextFormatting.GRAY + I18n.translateToLocal("entity.susanooclothed.name") + ",     [SHIFT] Genjutsu");
            tooltip.add(TextFormatting.ITALIC + I18n.translateToLocal("key.mcreator.specialjutsu3") + ": " + TextFormatting.GRAY + I18n.translateToLocal("tooltip.mangekyo.kamui.jutsu1"));
            tooltip.add(TextFormatting.ITALIC + I18n.translateToLocal("key.jutsu.4") + ": " + TextFormatting.GRAY + I18n.translateToLocal("chattext.shinratensei"));
            tooltip.add(TextFormatting.ITALIC + I18n.translateToLocal("key.jutsu.5") + ": " + TextFormatting.GRAY + I18n.translateToLocal("tooltip.rinnegan.jutsu2"));
            tooltip.add(TextFormatting.ITALIC + I18n.translateToLocal("key.jutsu.6") + ": " + TextFormatting.GRAY + I18n.translateToLocal("tooltip.rinnegan.jutsu3"));
            tooltip.add(TextFormatting.ITALIC + I18n.translateToLocal("key.jutsu.7") + ": " + TextFormatting.GRAY + I18n.translateToLocal("item.ninjutsu.amenotejikara"));
        }

        public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
            Multimap multimap = super.getAttributeModifiers(slot, stack);
            if (slot == EntityEquipmentSlot.HEAD && ItemRinneganTomoe.isRinneganTomoeActivated(stack)) {
                multimap.put(SharedMonsterAttributes.MAX_HEALTH.getName(), new AttributeModifier(ItemRinneganTomoe.RINNEGANTOMOE_MODIFIER, "rinnegantomoe.maxhealth", 380.0, 0));
            }

            return multimap;
        }

        public String getItemStackDisplayName(ItemStack stack) {
            return TextFormatting.LIGHT_PURPLE + super.getItemStackDisplayName(stack) + TextFormatting.WHITE;
        }

        public SoundEvent getActivationSound(ItemStack eye) {
            if (sharinganOff(eye) && !isRinnesharinganActivated(eye))
                return Sounds.get("rinnegansfx");

            return Sounds.get("rinnesharingansfx");
        }

        @Override
        public boolean onJutsuKey1(boolean is_pressed, ItemStack stack, EntityPlayer entity) {
            if (!eternalOn(stack))
                return false;

            Map<String, Object> $_dependencies = Maps.newHashMap();
            $_dependencies.put("is_pressed", is_pressed);
            $_dependencies.put("entity", entity);
            $_dependencies.put("world", entity.world);
            $_dependencies.put("x", (int) entity.posX);
            $_dependencies.put("y", (int) entity.posY);
            $_dependencies.put("z", (int) entity.posZ);
            ProcedureAmaterasu.executeProcedure($_dependencies);
            return true;
        }

        @Override
        public boolean onJutsuKey2(boolean is_pressed, ItemStack stack, EntityPlayer entity) {
            if (!is_pressed) {
                if (entity.isSneaking()) {
                    int duration = eternalOn(stack) ? 40 : sharinganOn(stack) ? 20 : 10;
                    int range = eternalOn(stack) ? 64 : sharinganOn(stack) ? 32 : 16;
                    return applyGenjutsu(entity, range, duration);
                }
                
                Map<String, Object> $_dependencies = Maps.newHashMap();
                $_dependencies.put("entity", entity);
                $_dependencies.put("world", entity.world);
                ProcedureSusanoo.executeProcedure($_dependencies);
            }
            return true;
        }

        @Override
        public boolean onJutsuKey3(boolean is_pressed, ItemStack stack, EntityPlayer entity) {
            if (!eternalOn(stack))
                return false;

            Map<String, Object> $_dependencies = Maps.newHashMap();
            $_dependencies.put("is_pressed", is_pressed);
            $_dependencies.put("entity", entity);
            $_dependencies.put("world", entity.world);
            if (entity.world.provider.getDimension() == WorldKamuiDimension.DIMID && !entity.isSneaking()) {
                ProcedureGrabEntity.executeProcedure($_dependencies);
            } else {
                $_dependencies.put("x", (int) entity.posX);
                $_dependencies.put("y", (int) entity.posY);
                $_dependencies.put("z", (int) entity.posZ);
                ProcedureKamuiJikukanIdo.executeProcedure($_dependencies);
            }
            return true;
        }

        @Override
        public boolean onJutsuKey4(byte pressType, ItemStack stack, EntityPlayer entity) {
            Map<String, Object> $_dependencies = Maps.newHashMap();
            $_dependencies.put("is_pressed", pressType == 1);
            $_dependencies.put("entity", entity);
            $_dependencies.put("world", entity.world);
            $_dependencies.put("x", (int) entity.posX);
            $_dependencies.put("y", (int) entity.posY);
            $_dependencies.put("z", (int) entity.posZ);
            ProcedureShinraTenseiOnKeyPressed.executeProcedure($_dependencies);
            return true;
        }

        @Override
        public boolean onJutsuKey5(byte pressType, ItemStack stack, EntityPlayer entity) {
            int which_path = stack.hasTagCompound() ? (int) stack.getTagCompound().getDouble("which_path") : -1;
            if (pressType == 2) {
                Map<String, Object> $_dependencies = Maps.newHashMap();
                $_dependencies.put("is_pressed", pressType == 0);
                $_dependencies.put("entity", entity);
                $_dependencies.put("world", entity.world);
                switch (which_path) {
                    case 0:
                        if (entity.isSneaking()) {
                            Vec3d vec1 = entity.getPositionEyes(1f);
                            Vec3d vec2 = vec1.add(entity.getLookVec().scale(100));
                            RayTraceResult rtr = entity.world.rayTraceBlocks(vec1, vec2, false, false, true);
                            $_dependencies.put("x", rtr.getBlockPos().getX());
                            $_dependencies.put("y", rtr.getBlockPos().getY());
                            $_dependencies.put("z", rtr.getBlockPos().getZ());
                            ProcedureMeteorStrike.executeProcedure($_dependencies);
                        } else {
                            $_dependencies.put("x", (int) entity.posX);
                            $_dependencies.put("y", (int) entity.posY);
                            $_dependencies.put("z", (int) entity.posZ);
                            ProcedureChibakuTenseiOnKeyPressed.executeProcedure($_dependencies);
                        }
                        break;
                    case 4:
                        ProcedureNarakaPath.executeProcedure($_dependencies);
                        break;
                    case 3:
                        ProcedurePretaPath.executeProcedure($_dependencies);
                        break;
                    case 2:
                        ProcedureAnimalPath.executeProcedure($_dependencies);
                        break;
                    case 5:
                        Vec3d vec1 = entity.getPositionEyes(1f);
                        Vec3d vec2 = vec1.add(entity.getLookVec().scale(5));
                        RayTraceResult rtr = entity.world.rayTraceBlocks(vec1, vec2, false, false, true);
                        $_dependencies.put("x", rtr.getBlockPos().getX());
                        $_dependencies.put("y", (int) entity.posY);
                        $_dependencies.put("z", rtr.getBlockPos().getZ());
                        ProcedureOuterPath.executeProcedure($_dependencies);
                        break;
                }
            } else if (which_path == 2) {
                Map<String, Object> $_dependencies = Maps.newHashMap();
                $_dependencies.put("is_pressed", pressType < 2);
                $_dependencies.put("entity", entity);
                $_dependencies.put("world", entity.world);
                ProcedureAnimalPath.executeProcedure($_dependencies);
            }
            return true;
        }

        @Override
        public boolean onJutsuKey6(byte pressType, ItemStack stack, EntityPlayer entity) {
            Map<String, Object> $_dependencies = Maps.newHashMap();
            $_dependencies.put("is_pressed", pressType == 0);
            $_dependencies.put("entity", entity);
            ProcedureBanShoTenin.executeProcedure($_dependencies);
            return true;
        }

        @Override
        public boolean onJutsuKey7(byte pressType, ItemStack stack, EntityPlayer entity) {
            if (pressType == 0) {
                if (!entity.world.isRemote && !stack.getTagCompound().getBoolean("amenotejikaraStoreTarget") && !entity.isPotionActive(PotionSpaceInversion.potion)) {
                    Entity hit = ProcedureUtils.objectEntityLookingAt(entity, ModConfig.TECHNIQUES.AMENOTEJIKARA_RANGE).entityHit;
                    ItemNinjutsu.Amenotejikara.setTarget(stack, hit);

                    if (hit != null) {
                        if (entity.isSneaking()) { //shift clicking stores the target, which can be switched to on the next click
                            stack.getTagCompound().setBoolean("amenotejikaraStoreTarget", true);
                            stack.getTagCompound().setInteger("amenotejikaraDisable", 5);
                            entity.sendStatusMessage(new TextComponentTranslation("amenotejikara.target.next_switch", hit.getDisplayName()), true);
                        } else
                            entity.sendStatusMessage(new TextComponentTranslation("amenotejikara.target.switching", hit.getDisplayName()), true);
                    }
                }
            } else if (pressType == 2) {
                ItemNinjutsu.AMENOTEJIKARA.jutsu.createJutsu(stack, entity, 100);
            }
            return true;
        }

        @Override
        public boolean onSwitchJutsuKey(boolean is_pressed, ItemStack stack, EntityPlayer entity) {
            if (is_pressed)
                return false;

            if (entity.getRidingEntity() instanceof EntitySusanooBase) {
                ProcedureSusanoo.upgradeRinneganTomoe(entity, stack);
                return true;
            }

            int i = (stack.hasTagCompound() ? (int) stack.getTagCompound().getDouble("which_path") : -1) + 1;
            if (i > 5) {
                i = 0;
            }
            if (!stack.hasTagCompound()) {
                stack.setTagCompound(new NBTTagCompound());
            }
            stack.getTagCompound().

                    setDouble("which_path", i);
            if (!entity.world.isRemote) {
                entity.sendStatusMessage(new TextComponentString(I18n.translateToLocal(String.format("chattext.rinnegan.path%d", i))), true);
            }

            return true;
        }

        public int getCompatibleStatus(ItemStack current, ItemStack target) {
            if (isRinnesharinganActivated(target))
                return -1;

            if (target.isEmpty())
                return SHARINGAN_OFF;

            if (isTomoe(target))
                return getTomoeStatus(target);

            if (target.getItem() == ItemSharingan.helmet)
                return SHARINGAN_ON;


            if (ItemSharingan.isMangekyo(target))
                return ETERNAL_ON;

            return -1;
        }
    }

    public static boolean isRinneganTomoeActivated(ItemStack stack) {
        return stack.hasTagCompound() && stack.getTagCompound().getBoolean("RinneganTomoeActivated");
    }

    public static final int SHARINGAN_OFF = 0, SHARINGAN_ON = 1, ETERNAL_ON = 2;

    public static boolean sharinganOff(ItemStack stack) {
        int status = getTomoeStatus(stack);
        return status < SHARINGAN_ON || status > ETERNAL_ON;
    }

    public static boolean sharinganOn(ItemStack stack) {
        return stack.hasTagCompound() && getTomoeStatus(stack) == SHARINGAN_ON;
    }

    public static boolean eternalOn(ItemStack stack) {
        return stack.hasTagCompound() && getTomoeStatus(stack) == ETERNAL_ON;
    }

    public static int getTomoeStatus(ItemStack stack) {
        if (stack == null || !stack.hasTagCompound() || !stack.getTagCompound().hasKey("tomoeStatus"))
            return -1;

        if (isRinnesharinganActivated(stack))
            return ETERNAL_ON;
        
        return stack.getTagCompound().getByte("tomoeStatus");
    }

    public static void setTomoeStatus(ItemStack stack, int status) {
        stack.getTagCompound().setByte("tomoeStatus", (byte) status);
    }

    public static void setTomoeStatus(ItemStack stack, int status, EntityLivingBase entity) {
        if (!isTomoe(stack))
            return;

        int oldStatus = ItemRinneganTomoe.getTomoeStatus(stack);
        ItemRinneganTomoe.setTomoeStatus(stack, status);

        if (status > oldStatus) {
            ((ItemDojutsu.Base) stack.getItem()).onEquip(stack, entity, false);
            ItemDojutsu.playActivationSound(stack, entity);
        } else if (status < oldStatus) {
            ((ItemDojutsu.Base) stack.getItem()).onEquip(stack, entity, true);
            ItemDojutsu.playDeactivationSound(stack, entity);
        }
    }

    public static boolean isTomoe(ItemStack stack) {
        return stack.getItem() instanceof Base;
    }

    public static Base get(ItemStack stack) {
        return (Base) stack.getItem();
    }
    public static boolean isWearing(EntityLivingBase player) {
        return isTomoe(ItemDojutsu.getWorn(player));
    }

    @SideOnly(Side.CLIENT)
    public void registerModels(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(helmet, 0, new ModelResourceLocation("narutomod:rinnegantomoehelmet", "inventory"));
    }
}

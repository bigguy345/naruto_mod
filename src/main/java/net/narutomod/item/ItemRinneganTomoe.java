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
import net.minecraft.util.ResourceLocation;
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
import net.narutomod.*;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.entity.EntityKingOfHell;
import net.narutomod.entity.EntityPretaShield;
import net.narutomod.entity.EntitySusanooBase;
import net.narutomod.entity.EntityTenTails;
import net.narutomod.goatee.client.Sounds;
import net.narutomod.gui.GuiNinjaScroll;
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

@ElementsNarutomodMod.ModElement.Tag
public class ItemRinneganTomoe extends ElementsNarutomodMod.ModElement {

    @GameRegistry.ObjectHolder("narutomod:rinnegantomoehelmet")
    public static final Item helmet = null;
    public static final String UNLOCALIZED_NAME = "rinnegantomoehelmet";
    public static final String TEXTURE_FILE = "rinneems.png";
    public static final String ENUM_NAME = "RINNEGANTOMOE";
    public static final String ARMOR_MATERIAL = "rinnegantomoe_";

    public static final String RINNEGANTOMOE_KEY = "RinneganTomoeActivated";
    protected static final UUID RINNEGANTOMOE_MODIFIER = UUID.fromString("135da083-a632-1488-85bd-2281f15ca7e0");

    public ItemRinneganTomoe(ElementsNarutomodMod instance) {
        super(instance, 20000);
    }

    public void initElements() {
        ItemArmor.ArmorMaterial enuma = EnumHelper.addArmorMaterial("RINNEGANTOMOE", "narutomod:rinnegantomoe_", 25, new int[]{2, 5, 6, 15}, 0, null, 2.0F);
        this.elements.items.add(() -> new ItemSharingan.Base(enuma) {

            public void onArmorTick(World world, EntityPlayer entity, ItemStack itemstack) {
                super.onArmorTick(world, entity, itemstack);
                if (world.isRemote)
                    return;

                entity.addPotionEffect(new PotionEffect(MobEffects.SPEED, 2, 2, false, false));
                entity.capabilities.allowFlying = entity.isCreative() || entity.dimension == WorldKamuiDimension.DIMID;
                entity.sendPlayerAbilities();
                if (entity.getEntityData().getBoolean("kamui_teleport")) {
                    Chakra.pathway(entity).consume(ItemMangekyoSharinganObito.getTeleportChakraUsage(entity));
                }
                if (entity.getEntityData().getBoolean("kamui_intangible")) {
                    Chakra.pathway(entity).consume(ItemMangekyoSharinganObito.getIntangibleChakraUsage(entity));
                    entity.getEntityData().setDouble(NarutomodModVariables.InvulnerableTime, 2.0d);
                }
                
                int x = (int) entity.posX;
                int y = (int) entity.posY;
                int z = (int) entity.posZ;
                HashMap $_dependencies = new HashMap();
                $_dependencies.put("entity", entity);
                $_dependencies.put("x", x);
                $_dependencies.put("y", y);
                $_dependencies.put("z", z);
                $_dependencies.put("itemstack", itemstack);
                $_dependencies.put("world", world);
                ProcedureRinneganHelmetTickEvent.executeProcedure($_dependencies);
            }

            public void onUpdate(ItemStack itemstack, World world, Entity entity, int par4, boolean par5) {
                super.onUpdate(itemstack, world, entity, par4, par5);
                if (!world.isRemote && entity.ticksExisted % 20 == 0) {
                    UUID uuid = ProcedureUtils.getUniqueId(itemstack, "KoH_id");
                    if (uuid != null) {
                        Entity koh = ((WorldServer) world).getEntityFromUuid(uuid);
                        if (!(koh instanceof EntityKingOfHell.EntityCustom) || !koh.isEntityAlive()) {
                            ProcedureUtils.removeUniqueIdTag(itemstack, "KoH_id");
                        }
                    }

                    if (entity instanceof EntityPlayer) {
                        EntityPlayer player = (EntityPlayer) entity;
                        ItemStack helmetStack = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
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

                if (itemstack.getTagCompound().hasKey("amenotejikaraDisable")) { //important as to not switch on the same shift click that stores target 
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

            @SideOnly(Side.CLIENT)
            public ModelBiped getArmorModel(EntityLivingBase living, ItemStack stack, EntityEquipmentSlot slot, ModelBiped defaultModel) {
                ItemDojutsu.ClientModel.ModelHelmetSnug model = (ItemDojutsu.ClientModel.ModelHelmetSnug) super.getArmorModel(living, stack, slot, defaultModel);
                boolean isS06p = isRinnesharinganActivated(stack);
                model.isSo6 = model.hornMiddle.showModel = model.headwearShine = model.onface.showModel = isS06p;
                model.foreheadHide = !isS06p || !(living instanceof EntityPlayer) || PlayerTracker.getNinjaLevel((EntityPlayer) living) < 180d;
                
                return model;
            }

            public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
                if (isRinnesharinganActivated(stack))
                    return "narutomod:textures/rinnesharingantomoehelmet.png";
                return "narutomod:textures/rinnegantomoehelmet.png";
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
                super.addInformation(stack, worldIn, tooltip, flagIn);

                if (isRinnesharinganActivated(stack))
                    tooltip.add(TextFormatting.RED + I18n.translateToLocal("advancements.rinnesharinganactivated.title") + TextFormatting.WHITE);
                tooltip.add(TextFormatting.ITALIC + I18n.translateToLocal("key.mcreator.specialjutsu1") + ": " + TextFormatting.GRAY + I18n.translateToLocal("tooltip.mangekyo.amaterasu.jutsu1"));
                tooltip.add(TextFormatting.ITALIC + I18n.translateToLocal("key.mcreator.specialjutsu2") + ": " + TextFormatting.GRAY + I18n.translateToLocal("entity.susanooclothed.name"));
                tooltip.add(TextFormatting.ITALIC + I18n.translateToLocal("key.mcreator.specialjutsu3") + ": " + TextFormatting.GRAY + I18n.translateToLocal("tooltip.mangekyo.kamui.jutsu1"));
                tooltip.add(TextFormatting.ITALIC + I18n.translateToLocal("key.mcreator.specialjutsu4") + ": " + TextFormatting.GRAY + I18n.translateToLocal("item.ninjutsu.amenotejikara"));
                tooltip.add(TextFormatting.ITALIC + I18n.translateToLocal("key.mcreator.specialjutsu5") + ": " + TextFormatting.GRAY + I18n.translateToLocal("tooltip.rinnegan.jutsu2"));
                tooltip.add(TextFormatting.ITALIC + I18n.translateToLocal("key.mcreator.specialjutsu6") + ": " + TextFormatting.GRAY + I18n.translateToLocal("tooltip.rinnegan.jutsu3"));
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

            public SoundEvent getSound(){
                return Sounds.get("rinnesharingansfx");
            }

            @Override
            public boolean onJutsuKey1(boolean is_pressed, ItemStack stack, EntityPlayer entity) {
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
                    Map<String, Object> $_dependencies = Maps.newHashMap();
                    $_dependencies.put("entity", entity);
                    $_dependencies.put("world", entity.world);
                    ProcedureSusanoo.executeProcedure($_dependencies);
                }
                return true;
            }
            
            
            @Override
            public boolean onJutsuKey3(boolean is_pressed, ItemStack stack, EntityPlayer entity) {
                Map<String, Object> $_dependencies = Maps.newHashMap();
                $_dependencies.put("is_pressed", is_pressed);
                $_dependencies.put("entity", entity);
                $_dependencies.put("world", entity.world);
                if (entity.world.provider.getDimension() == WorldKamuiDimension.DIMID && !entity.isSneaking()) {
                    ProcedureGrabEntity.executeProcedure($_dependencies);
                } else {
                    $_dependencies.put("x", (int)entity.posX);
                    $_dependencies.put("y", (int)entity.posY);
                    $_dependencies.put("z", (int)entity.posZ);
                    ProcedureKamuiJikukanIdo.executeProcedure($_dependencies);
                }
                return true;
            }

            @Override
            public boolean onJutsuKey4(byte pressType, ItemStack stack, EntityPlayer entity) {
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
                } else if (pressType == 0 && which_path == 2) {
                    Map<String, Object> $_dependencies = Maps.newHashMap();
                    $_dependencies.put("is_pressed", pressType == 0);
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
            public boolean onSwitchJutsuKey(boolean is_pressed, ItemStack stack, EntityPlayer entity) {
                if (is_pressed)
                    return false;

                if (entity.getRidingEntity() instanceof EntitySusanooBase) {
                    ProcedureSusanoo.upgrade(entity);
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
        }.setUnlocalizedName("rinnegantomoehelmet").setRegistryName("rinnegantomoehelmet").setCreativeTab(TabModTab.tab));
    }

    public static boolean isRinneganTomoeActivated(ItemStack stack) {
        return stack.hasTagCompound() && stack.getTagCompound().getBoolean("RinneganTomoeActivated");
    }

    public static boolean isTomoe(ItemStack stack) {
        return stack.getItem() == helmet;
    }

    public static boolean isWearing(EntityLivingBase player) {
        return isTomoe(player.getItemStackFromSlot(EntityEquipmentSlot.HEAD));
    }

    @SideOnly(Side.CLIENT)
    public void registerModels(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(helmet, 0, new ModelResourceLocation("narutomod:rinnegantomoehelmet", "inventory"));
    }
}

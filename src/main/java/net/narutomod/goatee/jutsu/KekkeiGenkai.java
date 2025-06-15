package net.narutomod.goatee.jutsu;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.items.ItemHandlerHelper;
import net.narutomod.NarutomodModVariables;
import net.narutomod.goatee.util.AdvancementUtil;
import net.narutomod.gui.GuiScrollGenjutsuGui;
import net.narutomod.item.*;
import net.narutomod.procedure.ProcedureUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public enum KekkeiGenkai {
    //////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////
    // Jutsus
    SHIKOTSUMYAKU(ItemShikotsumyaku.block, "narutomod:shikotsumyaku_acquired"),
    LAVA(ItemYooton.block, "narutomod:yooton_acquired") {
        public void giveExtras(EntityPlayer player) {
            giveItem(ItemKaton.block, player);
            giveItem(ItemDoton.block, player);
        }
    },

    SCORCH(ItemShakuton.block, "narutomod:shakuton_acquired") {
        public void giveExtras(EntityPlayer player) {
            giveItem(ItemKaton.block, player);
            giveItem(ItemFuton.block, player);
        }
    },
    ICE(ItemHyoton.block, "narutomod:hyoton_acquired") {
        public void giveExtras(EntityPlayer player) {
            giveItem(ItemSuiton.block, player);
            giveItem(ItemFuton.block, player);
        }
    },
    MAGNET(ItemJiton.block, "narutomod:jiton_acquired") {
        public void giveExtras(EntityPlayer player) {
            giveItem(ItemDoton.block, player);
            giveItem(ItemFuton.block, player);
        }
    },
    EXPLOSION(ItemBakuton.block, "narutomod:bakuton_acquired") {
        public void giveExtras(EntityPlayer player) {
            giveItem(ItemDoton.block, player);
            giveItem(ItemRaiton.block, player);
        }
    },
    STORM(ItemRanton.block, "narutomod:ranton_acquired") {
        public void giveExtras(EntityPlayer player) {
            giveItem(ItemSuiton.block, player);
            giveItem(ItemRaiton.block, player);
        }
    },
    BOIL(ItemFutton.block, "narutomod:futton_acquired") {
        public void giveExtras(EntityPlayer player) {
            giveItem(ItemKaton.block, player);
            giveItem(ItemSuiton.block, player);
        }
    },
    CRYSTAL(ItemShoton.block, "") {
        public void giveExtras(EntityPlayer player) {
            giveItem(ItemDoton.block, player);
        }
    },
    DUST(ItemJinton.block, "narutomod:kekkei_tota_awakened") {
        public void giveExtras(EntityPlayer player) {
            giveItem(ItemDoton.block, player);
            giveItem(ItemKaton.block, player);
            giveItem(ItemFuton.block, player);
        }
    },
    WOOD(ItemMokuton.block, "narutomod:mokuton_acquired") {
        public void giveExtras(EntityPlayer player) {
            giveItem(ItemDoton.block, player);
            giveItem(ItemSuiton.block, player);
        }
    },

    //////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////
    // Dojutsu
    BYAKUGAN(ItemByakugan.helmet, "narutomod:byakuganopened"),
    TENSEIGAN(ItemTenseigan.helmet, "narutomod:tenseigan_achieved") {
        public ItemStack createItemStack(EntityLivingBase player) {
            ItemStack stack = super.createItemStack(player);
            stack.getTagCompound().setDouble("ByakuganCount", 5);
            return stack;
        }
    },

    SHARINGAN(ItemSharingan.helmet, "narutomod:sharinganopened") {
        public void giveExtras(EntityPlayer player) {
            GuiScrollGenjutsuGui.giveGenjutsu(player);
        }
    },
    MANGEKYO(ItemMangekyoSharingan.helmet, "narutomod:mangekyosharinganopened") {
        public Item getItem() {
            return Math.random() < 0.5 ? ItemMangekyoSharingan.helmet : ItemMangekyoSharinganObito.helmet;
        }
    },
    ETERNAL_MANGEKYO(ItemMangekyoSharinganEternal.helmet, "narutomod:eternalmangekyoachieved"),
    RINNEGAN(ItemRinnegan.helmet, "narutomod:rinneganawakened");

    public Item item;
    public String achievement;

    KekkeiGenkai(Item item, String achievement) {
        this.item = item;
        this.achievement = achievement;
    }

    public void give(EntityPlayer player) {
        if (!AdvancementUtil.has((EntityPlayerMP) player, achievement) || achievement.isEmpty()) {
            giveExtras(player);
            ItemHandlerHelper.giveItemToPlayer(player, createItemStack(player));
            grantAdvancement((EntityPlayerMP) player);
        }
    }

    public void giveExtras(EntityPlayer player) {

    }

    public Item getItem() {
        return item;
    }

    public ItemStack createItemStack(EntityLivingBase player) {
        ItemStack stack = new ItemStack(getItem(), 1);

        if (!stack.hasTagCompound())
            stack.setTagCompound(new NBTTagCompound());

        if (item instanceof ItemDojutsu.Base) {
            ((ItemDojutsu.Base) item).setOwner(stack, player);
            player.getEntityData().setLong(NarutomodModVariables.MostRecentWornDojutsuTime, player.world.getTotalWorldTime());
        } else if (item instanceof ItemJutsu.Base) {
            ((ItemJutsu.Base) item).setOwner(stack, player);
            ((ItemJutsu.Base) item).setIsAffinity(stack, true);
        }

        return stack;
    }

    public void grantAdvancement(EntityPlayerMP player) {
        if (achievement.isEmpty())
            return;

        AdvancementUtil.grant(player, achievement);
        player.world.playSound(player, player.getPosition(), SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.NEUTRAL, 1, 1);
    }

    public void remove(EntityPlayerMP player) {
        if (!achievement.isEmpty())
            AdvancementUtil.revoke(player, achievement);

        player.inventory.clearMatchingItems(item, -1, -1, null);
    }

    public static void giveItem(Item item, EntityPlayer player) {
        if (!ProcedureUtils.hasItem(player, item)) {
            ItemStack stack = new ItemStack(item, 1);
            if (item instanceof ItemJutsu.Base)
                ((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);

            ItemHandlerHelper.giveItemToPlayer(player, stack);
        }
    }

    public static List<KekkeiGenkai> getRandomlyObtainable() {
        List<KekkeiGenkai> list = new ArrayList<>(Arrays.asList(KekkeiGenkai.values()));

        list.removeAll(Arrays.asList(KekkeiGenkai.WOOD, KekkeiGenkai.MANGEKYO, KekkeiGenkai.ETERNAL_MANGEKYO, KekkeiGenkai.RINNEGAN, KekkeiGenkai.TENSEIGAN));
        return list;
    }

    public static KekkeiGenkai getRandom() {
        List<KekkeiGenkai> list = getRandomlyObtainable();

        return list.get(ThreadLocalRandom.current().nextInt(list.size()));
    }
}

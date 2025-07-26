package net.narutomod.goatee.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.narutomod.goatee.data.NarutoData;
import net.narutomod.goatee.data.WheelData;
import net.narutomod.goatee.network.AbstractPacket;
import net.narutomod.item.ItemDojutsu;
import net.narutomod.item.ItemRinnegan;
import net.narutomod.item.ItemRinneganTomoe;

import java.io.IOException;

public final class NarutoWheelData extends AbstractPacket {
    public static enum Type {

    }

    public static final String packetName = "SaveWheelData";
    private int slot;
    private String name;
    private boolean defaultSlotOperation;
    private boolean toInventory;

    public NarutoWheelData() {
    }

    public NarutoWheelData(int wheelSlot, String wheelName) {
        this.slot = wheelSlot;
        this.name = wheelName;
        this.defaultSlotOperation = GuiScreen.isShiftKeyDown();
        this.toInventory = GuiScreen.isCtrlKeyDown();
    }

    @Override
    public String getChannel() {
        return packetName;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        out.writeInt(this.slot);
        out.writeBoolean(defaultSlotOperation);
        out.writeBoolean(toInventory);

        ByteBufUtils.writeUTF8String(out, name);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        slot = in.readInt();
        defaultSlotOperation = in.readBoolean();
        toInventory = in.readBoolean();
        name = ByteBufUtils.readUTF8String(in);

        NarutoData data = NarutoData.get(player);
        WheelData.Segment seg = NarutoData.get(player).dojutsuWheel.get(slot);
        ItemStack helmet = ItemDojutsu.getWorn(player);

        if (seg.stack.isEmpty()) { // putting eye into empty wheel slot
            boolean swapWithHelmet = false;
            ItemStack toSwapWith = player.getHeldItemMainhand().getItem() instanceof ItemDojutsu.Base ? player.getHeldItemMainhand() : (swapWithHelmet = helmet.getItem() instanceof ItemDojutsu.Base) ? helmet : ItemStack.EMPTY;

            if (helmet == toSwapWith && ItemRinneganTomoe.isTomoe(helmet)&& !ItemRinnegan.isRinnesharinganActivated(helmet) && !toInventory)
                ItemRinneganTomoe.setTomoeStatus(helmet, ItemRinneganTomoe.SHARINGAN_OFF,player);
            else if (!toSwapWith.isEmpty()) {
                seg.putToSaved(toSwapWith, seg.stack);

                if (defaultSlotOperation)
                    seg.setDefaultSlot(slot);

                if (swapWithHelmet) {
                    if (!data.getDojutsuSlot().isEmpty())
                        data.dojutsuSlotHandler.setStackInSlot(0, ItemStack.EMPTY);
                    else
                        player.setItemStackToSlot(EntityEquipmentSlot.HEAD, ItemStack.EMPTY);
                }
                else
                    player.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);

                if (ItemRinnegan.isRinnegan(helmet))
                    ItemRinnegan.removeClothes(player);
            }
        } else if (seg.stack.getItem() instanceof ItemDojutsu.Base) { //taking eye from wheel slot
            if (defaultSlotOperation)
                seg.removeDefaultSlot();

            if (toInventory) { //Move to inventory
                if (player.getHeldItemMainhand().isEmpty()) {
                    player.setHeldItem(EnumHand.MAIN_HAND, seg.stack);
                    seg.stack = ItemStack.EMPTY;
                } else if (player.inventory.getFirstEmptyStack() != -1) {
                    player.inventory.addItemStackToInventory(seg.stack);
                    seg.stack = ItemStack.EMPTY;
                } else
                    player.sendMessage(new TextComponentTranslation("dojutsuwheel.inventory_full"));
            } else { //Equip eye in slot
                if (ItemRinneganTomoe.isTomoe(helmet) && ItemRinneganTomoe.get(helmet).getCompatibleStatus(seg.stack) != -1 && !ItemRinnegan.isRinnesharinganActivated(helmet))
                    ItemRinneganTomoe.setTomoeStatus(helmet, ItemRinneganTomoe.get(helmet).getCompatibleStatus(seg.stack), player);
                else {
                    ItemStack targetItem = seg.stack;
                    seg.stack = ItemStack.EMPTY;

                    if (seg.stack.getItem() instanceof ItemRinnegan.Base)
                        ItemRinnegan.giveClothes(targetItem, player);
                    player.setItemStackToSlot(EntityEquipmentSlot.HEAD, targetItem);

                    if (helmet.getItem() instanceof ItemDojutsu.Base) { //SWAP EYES
                        seg.putToSaved(helmet, targetItem);

                        if (ItemRinnegan.isRinnegan(helmet))
                            ItemRinnegan.removeClothes(player);
                    }
                    else {
                        if (player.inventory.getFirstEmptyStack() != -1)
                            player.inventory.addItemStackToInventory(helmet);
                        else {
                            if (helmet.getItem() instanceof ItemDojutsu.Base)
                                seg.put(helmet);
                            else
                                player.dropItem(helmet, false);
                        }
                    }

                    //Fix mc helmet not being removed upon swapping 2 eyes
                    ItemStack newHelmet = player.inventory.armorInventory.get(EntityEquipmentSlot.HEAD.getIndex());
                    if (ItemStack.areItemStacksEqual(newHelmet, helmet))
                        player.inventory.armorInventory.set(EntityEquipmentSlot.HEAD.getIndex(), ItemStack.EMPTY);


                }
            }
        }
        NarutoSyncData.syncTrackingClients(NarutoData.get(player));
    }
}

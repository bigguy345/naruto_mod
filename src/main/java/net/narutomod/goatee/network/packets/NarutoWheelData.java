package net.narutomod.goatee.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.narutomod.goatee.data.NarutoData;
import net.narutomod.goatee.data.WheelData;
import net.narutomod.goatee.network.AbstractPacket;
import net.narutomod.item.ItemDojutsu;
import net.narutomod.item.ItemRinnegan;

import java.io.IOException;

public final class NarutoWheelData extends AbstractPacket {
    public static enum Type {

    }

    public static final String packetName = "SaveWheelData";
    private int wheelSlot;
    private String wheelName;
    private boolean unassignSavedSlot;

    public NarutoWheelData() {
    }

    public NarutoWheelData(int wheelSlot, String wheelName) {
        this.wheelSlot = wheelSlot;
        this.wheelName = wheelName;
        this.unassignSavedSlot = GuiScreen.isShiftKeyDown();
    }

    @Override
    public String getChannel() {
        return packetName;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        out.writeInt(this.wheelSlot);
        out.writeBoolean(unassignSavedSlot);

        ByteBufUtils.writeUTF8String(out, wheelName);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        int slot = in.readInt();
        boolean removeSavedSlot = in.readBoolean();
        String name = ByteBufUtils.readUTF8String(in);

        WheelData.Segment seg = NarutoData.get(player).dojutsuWheel.get(slot);
        ItemStack helmet = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);

        if (seg.stack.isEmpty()) {
            boolean swapWithHelmet = false;
            ItemStack toSwapWith = player.getHeldItemMainhand().getItem() instanceof ItemDojutsu.Base ? player.getHeldItemMainhand() : (swapWithHelmet = helmet.getItem() instanceof ItemDojutsu.Base) ? helmet : ItemStack.EMPTY;

            if (!toSwapWith.isEmpty()) {
                seg.putAndSave(toSwapWith, slot);

                if (swapWithHelmet)
                    player.setItemStackToSlot(EntityEquipmentSlot.HEAD, ItemStack.EMPTY);
                else
                    player.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
            }
        } else if (seg.stack.getItem() instanceof ItemDojutsu.Base) {
            if (removeSavedSlot)
                seg.removeSavedSlot();

            ItemStack removedItem = seg.stack;
            seg.stack = ItemStack.EMPTY;

            if (seg.stack.getItem() instanceof ItemRinnegan.Base)
                ItemRinnegan.giveClothes(removedItem, player);
            player.setItemStackToSlot(EntityEquipmentSlot.HEAD, removedItem);

            if (helmet.getItem() instanceof ItemDojutsu.Base)
                seg.putToSaved(helmet, removedItem);
            else {
                boolean addedToInv = player.inventory.addItemStackToInventory(helmet);
                if (!addedToInv) {
                    if (helmet.getItem() instanceof ItemDojutsu.Base)
                        seg.put(helmet);
                    else
                        player.dropItem(helmet, false);
                }
            }
        }
        NarutoSyncData.syncTrackingClients(NarutoData.get(player));
    }
}

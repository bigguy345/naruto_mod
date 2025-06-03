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
    private int slot;
    private String name;
    private boolean defaultSlotOperation;

    public NarutoWheelData() {
    }

    public NarutoWheelData(int wheelSlot, String wheelName) {
        this.slot = wheelSlot;
        this.name = wheelName;
        this.defaultSlotOperation = GuiScreen.isShiftKeyDown();
    }

    @Override
    public String getChannel() {
        return packetName;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        out.writeInt(this.slot);
        out.writeBoolean(defaultSlotOperation);

        ByteBufUtils.writeUTF8String(out, name);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        slot = in.readInt();
        defaultSlotOperation = in.readBoolean();
        name = ByteBufUtils.readUTF8String(in);

        WheelData.Segment seg = NarutoData.get(player).dojutsuWheel.get(slot);
        ItemStack helmet = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);

        if (seg.stack.isEmpty()) {
            boolean swapWithHelmet = false;
            ItemStack toSwapWith = player.getHeldItemMainhand().getItem() instanceof ItemDojutsu.Base ? player.getHeldItemMainhand() : (swapWithHelmet = helmet.getItem() instanceof ItemDojutsu.Base) ? helmet : ItemStack.EMPTY;

            if (!toSwapWith.isEmpty()) {
                seg.putToSaved(toSwapWith, seg.stack);

                if (defaultSlotOperation)
                    seg.setDefaultSlot(slot);

                if (swapWithHelmet)
                    player.setItemStackToSlot(EntityEquipmentSlot.HEAD, ItemStack.EMPTY);
                else
                    player.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
            }
        } else if (seg.stack.getItem() instanceof ItemDojutsu.Base) {
            if (defaultSlotOperation)
                seg.removeDefaultSlot();

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

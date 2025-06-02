package net.narutomod.goatee.network.packets;

import io.netty.buffer.ByteBuf;
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

    public NarutoWheelData() {
    }

    public NarutoWheelData(int wheelSlot, String wheelName) {
        this.wheelSlot = wheelSlot;
        this.wheelName = wheelName;
    }

    @Override
    public String getChannel() {
        return packetName;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        out.writeInt(this.wheelSlot);
        ByteBufUtils.writeUTF8String(out, wheelName);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        int slot = in.readInt();
        String name = ByteBufUtils.readUTF8String(in);

        WheelData.Segment wheel = NarutoData.get(player).dojutsuWheel.get(slot);
        ItemStack helmet = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);

        if (wheel.stack.isEmpty()) {
            if (player.inventory.getCurrentItem().getItem() instanceof ItemDojutsu.Base) {
                wheel.stack = player.inventory.getCurrentItem();
                player.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
            } else if (helmet.getItem() instanceof ItemDojutsu.Base) {
                wheel.stack = helmet;
                player.setItemStackToSlot(EntityEquipmentSlot.HEAD, ItemStack.EMPTY);
            }
        } else if (wheel.stack.getItem() instanceof ItemDojutsu.Base) {
            if (wheel.stack.getItem() instanceof ItemRinnegan.Base)
                ItemRinnegan.giveClothes(wheel.stack, player);
            
            player.setItemStackToSlot(EntityEquipmentSlot.HEAD, wheel.stack);
            wheel.stack = helmet;

            //            boolean addedToInv = player.inventory.addItemStackToInventory(helmet);
            //            if (!addedToInv) {
            //                if (helmet.getItem() instanceof ItemDojutsu.Base)
            //                    wheel.stack = helmet;
            //                else
            //                    player.dropItem(helmet, false);
            //            }

        }
        NarutoSyncData.syncTrackingClients(NarutoData.get(player));
    }
}

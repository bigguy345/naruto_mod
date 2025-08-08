package net.narutomod.goatee.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.narutomod.goatee.network.AbstractPacket;

import java.io.IOException;

public final class SetOnGroundPacket extends AbstractPacket {
    public static final String packetName = "SetOnGround";

    boolean onGround;

    public SetOnGroundPacket() {
    }

    public SetOnGroundPacket(boolean entityId) {
        onGround = entityId;
    }

    @Override
    public String getChannel() {
        return packetName;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        out.writeBoolean(onGround);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        player.onGround = onGround = in.readBoolean();
    }
}

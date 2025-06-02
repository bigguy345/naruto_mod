package net.narutomod.goatee.network.packets;

import io.netty.buffer.ByteBuf;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.narutomod.goatee.data.NarutoData;
import net.narutomod.goatee.network.AbstractPacket;
import net.narutomod.goatee.network.PacketHandler;
import net.narutomod.goatee.proxy.CommonProxy;

import java.io.IOException;

public final class NarutoSyncData extends AbstractPacket {
    public static final String packetName = "SyncNarutoData";

    private NarutoData data;

    public NarutoSyncData() {
    }

    public NarutoSyncData(NarutoData dbcData) {
        this.data = dbcData;
    }

    @Override
    public String getChannel() {
        return packetName;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        ByteBufUtils.writeUTF8String(out, this.data.player.getName());
        ByteBufUtils.writeTag(out, this.data.writeToNBT());
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        if (CommonProxy.isServer())
            syncTrackingClients(NarutoData.get(player));
        else {
            String playerName = ByteBufUtils.readUTF8String(in);
            EntityPlayer sendingPlayer = Minecraft.getMinecraft().world.getPlayerEntityByName(playerName);
            if (sendingPlayer != null)
                NarutoData.get(sendingPlayer).readFromNBT(ByteBufUtils.readTag(in));
        }
    }

    public static void requestSync(NarutoData data) {
        PacketHandler.Instance.sendToServer(new NarutoSyncData(data));
    }

    public static void syncTrackingClients(NarutoData data) {
        PacketHandler.Instance.sendToPlayer(data.player, new NarutoSyncData(data));
        PacketHandler.Instance.sendToTrackingPlayers(data.player, new NarutoSyncData(data));
    }
}

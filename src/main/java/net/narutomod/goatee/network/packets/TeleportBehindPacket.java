package net.narutomod.goatee.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.narutomod.Chakra;
import net.narutomod.ModConfig;
import net.narutomod.goatee.client.Sounds;
import net.narutomod.goatee.network.AbstractPacket;
import net.narutomod.procedure.ProcedureUtils;

import java.io.IOException;

public final class TeleportBehindPacket extends AbstractPacket {
    public static final String packetName = "TeleportBehind";

    int id;

    public TeleportBehindPacket() {
    }

    public TeleportBehindPacket(int entityId) {
        id = entityId;
    }

    @Override
    public String getChannel() {
        return packetName;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        out.writeInt(id);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        id = in.readInt();

        Entity target = player.world.getEntityByID(id);
        //   RayTraceResult rtr = ProcedureUtils.objectEntityLookingAt(player, 20, 1.5f);
        //   if (!((target = rtr.entityHit) instanceof EntityLivingBase))
        //   return;

        if (target == null)
            return;

        Vec3d entityToPlayer = player.getPositionVector().subtract(target.getPositionVector()).normalize();
        Vec3d entityLookVec = target.getLookVec().normalize();
        double dot = entityLookVec.dotProduct(entityToPlayer);
        boolean teleportBehind = dot > 0;
        double directionMultiplier = teleportBehind ? -1 : 1; // in front → teleport behind; behind → teleport in front


        Vec3d look = target.getLookVec().normalize();
        double distanceBehind = 2.0; // How far behind to teleport

        double x = target.posX + look.x * distanceBehind * directionMultiplier;
        double y = target.posY;
        double z = target.posZ + look.z * distanceBehind * directionMultiplier;

        BlockPos newPos = new BlockPos(x, y, z);
        World world = player.world;

        if (!(isPassable(newPos, world) && isPassable(newPos.up(), world))) {
            player.sendStatusMessage(new TextComponentTranslation("hiraishin.teleport_behind.not_safe"), true);
            return;
        }

        double chakraUsage = ModConfig.TECHNIQUES.HIRAISHIN_TELEPORT_BEHIND_CHAKRA_USAGE;
        Chakra.Pathway chakra = Chakra.pathway(player);
        if (chakra.getAmount() < chakraUsage) {
            chakra.warningDisplay();
            return;
        }
        chakra.consume(chakraUsage);

        player.world.playSound(null, player.posX, player.posY, player.posZ, Sounds.get("swoosh"), SoundCategory.NEUTRAL, 0.8f, player.getRNG().nextFloat() * 0.4f + 0.8f);

        player.setPosition(x, y, z);
        Vec3d vec2 = target.getPositionEyes(1f).subtract(player.getPositionEyes(1f));
        player.rotationYaw = ProcedureUtils.getYawFromVec(vec2);
        player.rotationPitch = ProcedureUtils.getPitchFromVec(vec2);
        player.setPositionAndUpdate(x, y, z);

        player.world.playSound(null, newPos, Sounds.get("swoosh"), SoundCategory.NEUTRAL, 0.8f, player.getRNG().nextFloat() * 0.4f + 0.8f);
    }

    boolean isPassable(BlockPos pos, World world) {
        IBlockState state = world.getBlockState(pos);
        return !state.getMaterial().blocksMovement(); // true for air, grass, flowers, etc.
    }
}

package net.narutomod.procedure;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.narutomod.block.BlockMud;
import net.narutomod.ElementsNarutomodMod;

import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.MobEffects;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.narutomod.entity.EntitySwampPit;
import net.narutomod.goatee.network.PacketHandler;
import net.narutomod.goatee.network.packets.SetOnGroundPacket;

import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureMudMobplayerCollidesBlock extends ElementsNarutomodMod.ModElement {
	public ProcedureMudMobplayerCollidesBlock(ElementsNarutomodMod instance) {
		super(instance, 379);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure MudMobplayerCollidesBlock!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure MudMobplayerCollidesBlock!");
			return;
		}
		Entity entity = (Entity) dependencies.get("entity");
		World world = (World) dependencies.get("world");
		double eyepos = 0;
		(entity).extinguish();

		if (entity.getEntityData().hasKey(EntitySwampPit.EC.Jutsu.ID_KEY) && !entity.isSneaking()) {
			entity.motionY = 0.1D;
			entity.fallDistance = (float) (0);
			entity.onGround = true;

			if (entity instanceof EntityPlayerMP) {
				((EntityPlayerMP) entity).connection.sendPacket(new SPacketEntityVelocity(entity));
				PacketHandler.Instance.sendToPlayer((EntityPlayer) entity, new SetOnGroundPacket(entity.onGround));
			}
			return;
		}
		
		if (entity instanceof EntityLivingBase)
			((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, (int) 10, (int) 5, (false), (false)));
		ProcedureUtils.setVelocity(entity, entity.motionX * -0.1d, -0.01d, entity.motionZ * -0.1d);
		if ((!(world.isRemote))) {
			eyepos = entity.posY + entity.getEyeHeight();
			if (((world.getBlockState(new BlockPos((int) Math.floor((entity.posX)), (int) (eyepos), (int) Math.floor((entity.posZ)))))
					.getBlock() == BlockMud.block.getDefaultState().getBlock())) {
				ProcedureRenderView.changeFog(entity, 1, 10, 10, 0.12f, 0.08f, 0.06f, 2.0f);
				entity.attackEntityFrom(DamageSource.DROWN, (float) 10);
			}
		}
	}
}

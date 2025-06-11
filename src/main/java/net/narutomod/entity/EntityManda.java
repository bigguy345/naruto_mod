
package net.narutomod.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;

import net.narutomod.ElementsNarutomodMod;
import net.narutomod.procedure.ProcedureUtils;

import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class EntityManda extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 360;
	public static final int ENTITYID_RANGED = 361;

	public EntityManda(ElementsNarutomodMod instance) {
		super(instance, 719);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class)
		 .id(new ResourceLocation("narutomod", "manda"), ENTITYID)
		 .name("manda").tracker(128, 3, true).egg(-10092391, -10092340).build());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> {
			return new EntitySnake.RenderSnake<EntityCustom>(renderManager) {
				private final ResourceLocation texture = new ResourceLocation("narutomod:textures/snake_purple.png");
				@Override
				protected ResourceLocation getEntityTexture(EntityCustom entity) {
					return this.texture;
				}
			};
		});
	}

	public static class EntityCustom extends EntitySnake.EntityCustom {
		//private EntityLivingBase summoner;

		public EntityCustom(World world) {
			super(world);
			this.postScaleFixup();
			this.stepHeight = this.height / 3;
		}

		public EntityCustom(EntityLivingBase summonerIn) {
			super(summonerIn);
			this.postScaleFixup();
			this.stepHeight = this.height / 3;
			//this.summoner = summonerIn;
			//this.dontWander(true);
		}

		@Override
		public float getScale() {
			return 18.0f;
		}

		/*@Override
		protected void updateAITasks() {
			super.updateAITasks();
			if (this.summoner != null) {
				EntityLivingBase target = this.summoner.getRevengeTarget();
				if (target != null) {
					this.setAttackTarget(target);
				}
				target = this.getAttackTarget();
				if (target != null && !target.isEntityAlive()) {
					this.setAttackTarget(null);
				}
			}
		}*/

		@Override
		@Nullable
		public EntityLivingBase getControllingPassenger() {
			Entity passenger = super.getControllingPassenger();
			return passenger instanceof EntityLivingBase && this.isSummoner(passenger) ? (EntityLivingBase) passenger : null;
		}

		@Override
		public void travel(float strafe, float vertical, float forward) {
			EntityLivingBase passenger = this.getControllingPassenger();
			if (passenger instanceof EntityPlayer) {
				++this.lifeSpan;
				this.rotationYaw = passenger.rotationYaw;
				this.rotationPitch = passenger.rotationPitch;
				this.setRotation(this.rotationYaw, this.rotationPitch);
				this.renderYawOffset = passenger.renderYawOffset;
				this.rotationYawHead = passenger.getRotationYawHead();
				this.jumpMovementFactor = passenger.getAIMoveSpeed() * 0.15F;
				this.setAIMoveSpeed((float) ProcedureUtils.getModifiedSpeed(this));
				forward = passenger.moveForward;
				strafe = passenger.moveStrafing;
			} else {
				this.jumpMovementFactor = 0.02f;
			}
			super.travel(strafe, vertical, forward);
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			EntityLivingBase summoner = this.getSummoner();
			if (summoner != null && !summoner.isRiding() && this.getAge() == 1) {
				summoner.startRiding(this);
			}
		}
	}
}

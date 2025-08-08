package net.narutomod.goatee.mixin.early.mc;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.client.CPacketVehicleMove;
import net.narutomod.entity.EntitySusanooBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetHandlerPlayServer.class)
public class MixinNetHandlerPlayServer {

    @Shadow
    public EntityPlayerMP player;

    // Change the 100.0D movement threshold
    @ModifyConstant(method = "processVehicleMove", constant = @Constant(doubleValue = 100.0D))
    private double modifyVehicleMoveLimit(double original) {
        Entity vehicle = this.player.getLowestRidingEntity();
        if (vehicle instanceof EntitySusanooBase) {
            return 1000; // disable "moved too quickly" check
        }

        return original;
    }

    // @Inject(method = "processVehicleMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldServer;getCollisionBoxes(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/AxisAlignedBB;)Ljava/util/List;"))
    @ModifyConstant(method = "processVehicleMove", constant = @Constant(doubleValue = 0.0625D, ordinal = 1))
    private double ignoreWrongMoveFlag(double original) { //, @Local(name = "flag1") LocalBooleanRef flag1
        Entity vehicle = this.player.getLowestRidingEntity();
        if (vehicle instanceof EntitySusanooBase) {
            return original * 100; // disable "moved wrongly" check
        }

        return original;
    }
}
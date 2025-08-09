package net.narutomod.goatee.mixin.early.mc;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.narutomod.entity.EntitySusanooBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(NetHandlerPlayServer.class)
public class MixinNetHandlerPlayServer {

    @Shadow
    public EntityPlayerMP player;

    // Change the 100.0D movement threshold
    @ModifyConstant(method = "processVehicleMove", constant = @Constant(doubleValue = 100.0D))
    private double modifyVehicleMoveLimit(double original) {
        if (player.getLowestRidingEntity() instanceof EntitySusanooBase)
            return 1000; // patch "moved too quickly" check

        return original;
    }

    @ModifyConstant(method = "processVehicleMove", constant = @Constant(doubleValue = 0.0625D, ordinal = 1))
    private double ignoreWrongMoveFlag(double original) { //, @Local(name = "flag1") LocalBooleanRef flag1
        if (player.getLowestRidingEntity() instanceof EntitySusanooBase)
            return original * 100; // patch "moved wrongly" check
        
        return original;
    }
}
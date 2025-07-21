//package net.narutomod.goatee.mixin.early.mc;
//
//import net.minecraft.entity.EntityLivingBase;
//import net.minecraft.entity.player.EntityPlayer;
//import net.minecraft.inventory.EntityEquipmentSlot;
//import net.minecraft.item.ItemStack;
//import net.minecraft.nbt.NBTTagCompound;
//import net.minecraft.world.World;
//import net.narutomod.goatee.data.NarutoData;
//import net.narutomod.item.ItemDojutsu;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Shadow;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//
//@Mixin(value = EntityPlayer.class)
//public abstract class MixinEntityPlayer extends EntityLivingBase {
//    @Shadow
//    public abstract void readEntityFromNBT(NBTTagCompound compound);
//
//    @Shadow
//    public abstract void setItemStackToSlot(EntityEquipmentSlot slotIn, ItemStack stack);
//
//    public MixinEntityPlayer(World worldIn) {
//        super(worldIn);
//    }
//
//
////    @Inject(method = "getItemStackFromSlot", at = @At("RETURN"), cancellable = true)
////    private void getDojutsu(EntityEquipmentSlot slotIn, CallbackInfoReturnable<ItemStack> cir) {
////        if (slotIn != EntityEquipmentSlot.HEAD)
////            return;
////
////        NarutoData data = NarutoData.get((EntityPlayer) (Object) this);
////        if (data == null || data.getDojutsuSlot().isEmpty())
////            return;
////
////        cir.setReturnValue(data.getDojutsuSlot());
////    }
//
//    @Inject(method = "setItemStackToSlot", at = @At("RETURN"))
//    private void getDojutsu(EntityEquipmentSlot slotIn, ItemStack stack, CallbackInfo ci) {
//        if (!(slotIn == EntityEquipmentSlot.HEAD && stack.getItem() instanceof ItemDojutsu.Base))
//            return;
//
//        NarutoData data = NarutoData.get((EntityPlayer) (Object) this);
//        if (data == null || !data.getDojutsuSlot().isEmpty())
//            return;
//
//        data.dojutsuSlotHandler.setStackInSlot(0, stack);
//    }
//}
//package net.narutomod.goatee.mixin.early.mc;
//
//import net.minecraft.client.gui.inventory.GuiContainerCreative;
//import net.minecraft.entity.player.EntityPlayer;
//import net.minecraft.inventory.Container;
//import net.minecraft.inventory.Slot;
//import net.minecraft.item.ItemStack;
//import net.minecraftforge.items.ItemStackHandler;
//import net.minecraftforge.items.SlotItemHandler;
//import net.narutomod.goatee.data.NarutoData;
//import net.narutomod.item.ItemDojutsu;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//
//@Mixin(GuiContainerCreative.ContainerCreative.class)
//public abstract class MixinContainerCreative extends Container {
//
//    @Inject(method = "<init>", at = @At("RETURN"))
//    private void onInit(EntityPlayer player, CallbackInfo ci) {
//    
//    }
//
//    @Inject(method = "transferStackInSlot", at = @At("HEAD"), cancellable = true)
//    private void injectTransferStackInSlot(EntityPlayer player, int index, CallbackInfoReturnable<ItemStack> cir) {
//        Slot slot = this.inventorySlots.get(index);
//        if (slot == null || !slot.getHasStack())
//            return;
//
//        ItemStack originalStack = slot.getStack();
//        if (!(originalStack.getItem() instanceof ItemDojutsu.Base))
//            return;
//
//        ItemStackHandler dojutsuHandler = NarutoData.get(player).dojutsuSlotHandler;
//
//        // 1. Shift-clicking FROM custom slot
//        if (slot instanceof SlotItemHandler) {
//            SlotItemHandler slotItemHandler = (SlotItemHandler) slot;
//            if (slotItemHandler.getItemHandler() == dojutsuHandler) {
//                ItemStack stackCopy = originalStack.copy();
//                boolean success = this.mergeItemStack(originalStack, 9, 45, false); // to inventory
//                if (success) {
//                    slot.putStack(originalStack.isEmpty() ? ItemStack.EMPTY : originalStack);
//                    slot.onSlotChanged();
//                    cir.setReturnValue(stackCopy);
//                } else
//                    cir.setReturnValue(ItemStack.EMPTY);
//            }
//        }
//        // 2. Shift-clicking TO custom slot
//        else if (originalStack.getItem() instanceof ItemDojutsu.Base) {
//            if (dojutsuHandler.getStackInSlot(0).isEmpty()) {
//                ItemStack leftover = dojutsuHandler.insertItem(0, originalStack.copy(), false);
//                if (leftover.isEmpty()) {
//                    slot.putStack(ItemStack.EMPTY);
//                    slot.onSlotChanged();
//                    cir.setReturnValue(originalStack);
//                }
//            }
//        }
//    }
//}
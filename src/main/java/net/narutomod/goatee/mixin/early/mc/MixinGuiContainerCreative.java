//package net.narutomod.goatee.mixin.early.mc;
//
//import com.llamalad7.mixinextras.sugar.Local;
//import com.llamalad7.mixinextras.sugar.ref.LocalRef;
//import goatee.dojutsuslot.client.DojutsuTextureHandler;
//import goatee.dojutsuslot.data.inventory.DojutsuSlot;
//import net.minecraft.client.gui.inventory.GuiContainer;
//import net.minecraft.client.gui.inventory.GuiContainerCreative;
//import net.minecraft.creativetab.CreativeTabs;
//import net.minecraft.inventory.Container;
//import net.minecraft.inventory.Slot;
//import net.narutomod.goatee.data.NarutoData;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Shadow;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//
//@Mixin(GuiContainerCreative.class)
//public abstract class MixinGuiContainerCreative extends GuiContainer {
//
//    @Shadow
//    private static int selectedTabIndex;
//
//    public MixinGuiContainerCreative(Container container) {
//        super(container);
//    }
//
//    @Inject(method = "drawGuiContainerBackgroundLayer", at = @At("TAIL"))
//    private void drawSlotBG(float partialTicks, int mouseX, int mouseY, CallbackInfo ci) {
//        if (selectedTabIndex == CreativeTabs.INVENTORY.getTabIndex())
//            DojutsuTextureHandler.drawSlot(guiLeft + 126, guiTop + 19);
//    }
//
//    @Inject(method = "setCurrentCreativeTab", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/inventory/GuiContainerCreative;destroyItemSlot:Lnet/minecraft/inventory/Slot;", ordinal = 1, shift = At.Shift.AFTER))
//    private void addSlot(CreativeTabs tab, CallbackInfo ci, @Local(name = "guicontainercreative$containercreative") LocalRef<GuiContainerCreative.ContainerCreative> container) {
//        NarutoData data = NarutoData.getClient();
//        if (data != null) {
//            int index = -1;
//            for (Slot slot : data.player.inventoryContainer.inventorySlots) {
//                if (slot instanceof DojutsuSlot) {
//                    index = slot.slotNumber;
//                    break;
//                }
//            }
//
//            if (index != -1) {
//                Slot eye = container.get().inventorySlots.get(index);
//                eye.xPos = 127;
//                eye.yPos = 20;
//            }
//        }
//    }
//}
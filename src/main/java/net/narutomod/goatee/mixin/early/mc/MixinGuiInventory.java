//package net.narutomod.goatee.mixin.early.mc;
//
//import goatee.dojutsuslot.client.DojutsuTextureHandler;
//import net.minecraft.client.gui.inventory.GuiContainer;
//import net.minecraft.client.gui.inventory.GuiInventory;
//import net.minecraft.inventory.Container;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//
//@Mixin(GuiInventory.class)
//public abstract class MixinGuiInventory extends GuiContainer {
//
//    public MixinGuiInventory(Container inventorySlotsIn) {
//        super(inventorySlotsIn);
//    }
//
//    @Inject(method = "drawGuiContainerBackgroundLayer", at = @At("TAIL"))
//    private void onDrawScreen(float partialTicks, int mouseX, int mouseY, CallbackInfo ci) {
//        DojutsuTextureHandler.drawSlot(guiLeft + 76, guiTop + 7);
//    }
//}
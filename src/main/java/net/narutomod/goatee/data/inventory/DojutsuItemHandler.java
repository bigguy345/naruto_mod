package net.narutomod.goatee.data.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.items.ItemStackHandler;
import net.narutomod.item.ItemDojutsu;

import javax.annotation.Nonnull;

public class DojutsuItemHandler extends ItemStackHandler {
    public DojutsuItemHandler() {
        super(1); // only one slot
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return stack.getItem() instanceof ItemDojutsu.Base;
    }

    public void setSlot(ItemStack stack, EntityPlayer player) {
        ItemStack prev = getStackInSlot(0);
        setStackInSlot(0, stack);
        if (!player.world.isRemote) {
            if (ItemStack.areItemStacksEqual(prev, stack))
                return;
            
            MinecraftForge.EVENT_BUS.post(new LivingEquipmentChangeEvent(player, EntityEquipmentSlot.HEAD, prev, stack));

            if (!prev.isEmpty()) {
                player.getAttributeMap().removeAttributeModifiers(prev.getAttributeModifiers(EntityEquipmentSlot.HEAD));
            }

            if (!stack.isEmpty()) {
                player.getAttributeMap().applyAttributeModifiers(stack.getAttributeModifiers(EntityEquipmentSlot.HEAD));
            }
        }
    }
}
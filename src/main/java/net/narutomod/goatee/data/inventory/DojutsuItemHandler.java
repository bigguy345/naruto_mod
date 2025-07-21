package net.narutomod.goatee.data.inventory;

import net.minecraft.item.ItemStack;
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
}
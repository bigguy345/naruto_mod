package net.narutomod.goatee.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemStackHandler;

import java.util.Arrays;
import java.util.List;

public class InventoryJutsu extends ItemStackHandler {
    public final NonNullList<ItemStack> dojutsuInventory = NonNullList.<ItemStack>withSize(4, ItemStack.EMPTY);
    public final NonNullList<ItemStack> scrollInventory = NonNullList.<ItemStack>withSize(9, ItemStack.EMPTY);
    public final NonNullList<ItemStack> toolInventory = NonNullList.<ItemStack>withSize(4, ItemStack.EMPTY);
    private final List<NonNullList<ItemStack>> allInventories;

    public EntityPlayer player;

    public InventoryJutsu(EntityPlayer playerIn) {
        this.allInventories = Arrays.asList(this.dojutsuInventory, this.scrollInventory, this.toolInventory);
        this.player = playerIn;
    }
}

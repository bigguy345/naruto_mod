package net.narutomod.goatee.data.inventory;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.narutomod.item.ItemDojutsu;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.UUID;

public class DojutsuItemHandler extends ItemStackHandler {
    protected static final UUID DOJUTSU_SLOT_MODIFIER =  UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

    private EntityPlayer player;

    public DojutsuItemHandler(EntityPlayer player) {
        super(1); // only one slot
        this.player = player;
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return stack.getItem() instanceof ItemDojutsu.Base;
    }

    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        ItemStack prev = stacks.get(0);

        this.validateSlotIndex(slot);
        this.stacks.set(slot, stack);
        this.onContentsChanged(slot);

        if (!player.world.isRemote) {
            if (ItemStack.areItemStacksEqual(prev, stack))
                return;

            MinecraftForge.EVENT_BUS.post(new LivingEquipmentChangeEvent(player, EntityEquipmentSlot.HEAD, prev, stack));

            if (!prev.isEmpty()) {
                player.getAttributeMap().removeAttributeModifiers(intercept(prev.getAttributeModifiers(EntityEquipmentSlot.HEAD)));
            }

            if (!stack.isEmpty()) {
                player.getAttributeMap().applyAttributeModifiers(intercept(stack.getAttributeModifiers(EntityEquipmentSlot.HEAD)));
            }
        }
    }

    /*
     * Changes UUID of all modifiers into a unique dojutsu slot ID, to allow stacking of modifiers with vanilla helmet slot ones
     */
    private static Multimap<String, AttributeModifier> intercept(Multimap<String, AttributeModifier> original) {
        Multimap<String, AttributeModifier> result = HashMultimap.create();
        for (Map.Entry<String, AttributeModifier> entry : original.entries()) {
            AttributeModifier modifier = entry.getValue();

            AttributeModifier newMod = new AttributeModifier(DOJUTSU_SLOT_MODIFIER, modifier.getName(), modifier.getAmount(), modifier.getOperation());
            result.put(entry.getKey(), newMod);
        }
        return result;
    }

    @Nonnull
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        } else {
            this.validateSlotIndex(slot);
            ItemStack existing = (ItemStack) this.stacks.get(slot);
            int limit = this.getStackLimit(slot, stack);
            if (!existing.isEmpty()) {
                if (!ItemHandlerHelper.canItemStacksStack(stack, existing)) {
                    return stack;
                }

                limit -= existing.getCount();
            }

            if (limit <= 0) {
                return stack;
            } else {
                boolean reachedLimit = stack.getCount() > limit;
                if (!simulate) {
                    if (existing.isEmpty()) {
                        setStackInSlot(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
                    } else {
                        existing.grow(reachedLimit ? limit : stack.getCount());
                    }

                    this.onContentsChanged(slot);
                }

                return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
            }
        }
    }

    @Nonnull
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0) {
            return ItemStack.EMPTY;
        } else {
            this.validateSlotIndex(slot);
            ItemStack existing = (ItemStack) this.stacks.get(slot);
            if (existing.isEmpty()) {
                return ItemStack.EMPTY;
            } else {
                int toExtract = Math.min(amount, existing.getMaxStackSize());
                if (existing.getCount() <= toExtract) {
                    if (!simulate) {
                        setStackInSlot(slot, ItemStack.EMPTY);
                        this.onContentsChanged(slot);
                    }

                    return existing;
                } else {
                    if (!simulate) {
                        setStackInSlot(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
                        this.onContentsChanged(slot);
                    }

                    return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
                }
            }
        }
    }

    public void deserializeNBT(NBTTagCompound nbt) {
        int newSize = nbt.hasKey("Size", 3) ? nbt.getInteger("Size") : -1;
        if (newSize > stacks.size())
            this.setSize(newSize);
        NBTTagList tagList = nbt.getTagList("Items", 10);

        for (int i = 0; i < tagList.tagCount(); ++i) {
            NBTTagCompound itemTags = tagList.getCompoundTagAt(i);
            int slot = itemTags.getInteger("Slot");
            if (slot >= 0 && slot < this.stacks.size()) {
                setStackInSlot(slot, new ItemStack(itemTags));
            }
        }

        this.onLoad();
    }

    public String toString() {
        return stacks.get(0).toString();
    }
}
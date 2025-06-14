package net.narutomod.goatee.data;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Mod.EventBusSubscriber
public class WheelData {
    public String NAME;
    public EntityPlayer player;
    private Segment[] wheelSegments = new Segment[6];
    


    public WheelData(String name) {
        NAME = name;
        for (int i = 0; i < wheelSegments.length; i++)
            wheelSegments[i] = new Segment(this, i);
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound wheelNbt = new NBTTagCompound();
        for (int i = 0; i < wheelSegments.length; i++)
            wheelSegments[i].writeToNBT(wheelNbt);

        compound.setTag(NAME, wheelNbt);
        return compound;
    }

    public void readFromNBT(NBTTagCompound compound) {
        NBTTagCompound wheelNbt = compound.getCompoundTag(NAME);
        for (int i = 0; i < wheelSegments.length; i++)
            wheelSegments[i].readFromNBT(wheelNbt.getCompoundTag(i + ""));
    }

    public Segment get(int slot) {
        return wheelSegments[slot];
    }

    public int getEmptySlot() {
        for (int i = 0; i < wheelSegments.length; i++)
            if (wheelSegments[i].stack.isEmpty())
                return i;

        return -1;
    }

    public void swapSlots(Segment seg, int slot) {
        swapSlots(seg, wheelSegments[slot]);
    }

    public void swapSlots(Segment seg1, Segment seg2) {
        if (seg1.slot != seg2.slot) {
            int seg1Slot = seg1.slot;

            wheelSegments[seg1Slot] = seg2;
            wheelSegments[seg2.slot] = seg1;


            seg1.slot = seg2.slot;
            seg2.slot = seg1Slot;
        }
    }

    public void putToDefaultSlot(Segment seg) {
        int savedSlot = seg.getDefaultSlot();
        Segment target = wheelSegments[savedSlot];
        if (seg == target)
            return;

        if (!target.stack.isEmpty()) {
            if (target.getDefaultSlot() == savedSlot) {
                if (player != null)
                    player.sendMessage(new TextComponentTranslation("dojutsuwheel.slot_is_occupied", TextFormatting.RED + "" + savedSlot, target.stack.getItem().getItemStackDisplayName(target.stack)));
            } else
                swapSlots(seg, target);
            return;
        }

        swapSlots(seg, savedSlot);
    }

    public List<ItemStack> dropAll(EntityPlayer owner, Predicate<ItemStack> condition) {
        List<ItemStack> toDrop = new ArrayList<>();
        for (Segment data : wheelSegments) {
            ItemStack item = data.drop(owner, condition);
            if (!item.isEmpty())
                toDrop.add(item);
        }

        return toDrop;
    }

    public boolean hasAny(Predicate<ItemStack> condition) {
        for (Segment data : wheelSegments)
            if (condition.test(data.stack))
                return true;

        return false;
    }
    
    public static class Segment {
        public WheelData parent;
        public int slot = -1;
        public ItemStack stack = ItemStack.EMPTY;

        public Segment() {
        }

        public Segment(WheelData parent, int slot) {
            this.slot = slot;
            this.parent = parent;
        }

        public ItemStack drop(EntityPlayer owner, Predicate<ItemStack> condition) {
            if (condition.test(stack)) {
                ItemStack toDrop = stack.copy();
                owner.dropItem(toDrop, true, true);
                stack = ItemStack.EMPTY;
                return toDrop;
            }
            return ItemStack.EMPTY;
        }
        public NBTTagCompound writeToNBT(NBTTagCompound compound) {
            compound.setTag(slot + "", stack.writeToNBT(new NBTTagCompound()));
            return compound;
        }

        public void readFromNBT(NBTTagCompound compound) {
            stack = new ItemStack(compound);
        }

        public static boolean hasDefaultSlot(ItemStack stack, String wheelName) {
            if (stack.isEmpty())
                return false;

            return stack.getTagCompound().hasKey(wheelName + "Slot");
        }

        public static int getDefaultSlot(ItemStack stack, String wheelName) {
            return stack.getTagCompound().getByte(wheelName + "Slot");
        }

        public void reset() {
            stack = null;
        }

        public boolean hasDefaultSlot() {
            return hasDefaultSlot(stack, parent.NAME);
        }

        public int getDefaultSlot() {
            return getDefaultSlot(stack, parent.NAME);
        }

        public void setDefaultSlot(int slot) {
            stack.getTagCompound().setByte(parent.NAME + "Slot", (byte) slot);

            if (parent.player != null)
                parent.player.sendMessage(new TextComponentTranslation("dojutsuwheel.set_default", stack.getItem().getItemStackDisplayName(stack), TextFormatting.GREEN + "" + slot));
        }

        public void removeDefaultSlot() {
            if (!hasDefaultSlot()){
                parent.player.sendMessage(new TextComponentTranslation("dojutsuwheel.not_assigned", stack.getItem().getItemStackDisplayName(stack)));
                return;
            }
                
            
            int saveSlot = getDefaultSlot();
            stack.getTagCompound().removeTag(parent.NAME + "Slot");

            if (parent.player != null)
                parent.player.sendMessage(new TextComponentTranslation("dojutsuwheel.remove_default", stack.getItem().getItemStackDisplayName(stack), TextFormatting.RED + "" + saveSlot));
        }

        public void putAndSave(ItemStack stack, int defaultSlot) {
            if (!put(stack))
                return;

            if (!hasDefaultSlot())
                setDefaultSlot(defaultSlot);

            parent.putToDefaultSlot(this);
        }

        public void putToSaved(ItemStack stack, ItemStack removed) {
            if (!put(stack))
                return;

            if (hasDefaultSlot())
                parent.putToDefaultSlot(this);
        }

        public boolean put(ItemStack stack) {
            if (stack == null || stack.isEmpty())
                return false;

            this.stack = stack;
            return true;
        }
        
    }
}

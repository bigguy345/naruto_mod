package net.narutomod.goatee.data;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.narutomod.goatee.data.sidedata.SideData;
import net.narutomod.item.ItemDojutsu;

import java.util.ArrayList;
import java.util.List;

import static net.narutomod.item.ItemDojutsu.is;

public class DojutsuData {
    public final ItemDojutsu.Base eye;
    public final List<JutsuKey> jutsus = new ArrayList<>();
    public final JutsuKey switchJutsu = new JutsuKey(-1);

    public DojutsuData(ItemDojutsu.Base eye) {
        this.eye = eye;

        for (int i = 0; i < 7; i++)
            jutsus.add(new JutsuKey(i));
    }

    public JutsuKey getKey(int key) {
        return jutsus.get(MathHelper.clamp(key - 1, 0, jutsus.size()));
    }

    public JutsuKey getSwitchJutsuKey() {
        return switchJutsu;
    }

    public SideData left(ItemStack stack) {
        return getLeft(stack);
    }

    public SideData right(ItemStack stack) {
        return getRight(stack);
    }

    public static SideData getLeft(ItemStack stack) {
        if (!is(stack))
            return null;

        ItemDojutsu.Base eye = (ItemDojutsu.Base) stack.getItem();
        return eye.getSideData(SideData.Side.LEFT, stack);
    }

    public static SideData getRight(ItemStack stack) {
        if (!is(stack))
            return null;

        ItemDojutsu.Base eye = (ItemDojutsu.Base) stack.getItem();
        return eye.getSideData(SideData.Side.RIGHT, stack);
    }

    public static void setItemIcon(ItemStack stack, String tex) {
        if (!stack.hasTagCompound())
            stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound().setString("itemIcon", tex);
    }

    public static String getItemIcon(ItemStack stack) {
        if (stack.getTagCompound() == null || !stack.getTagCompound().hasKey("itemIcon"))
            return null;
        return stack.getTagCompound().getString("itemIcon");
    }

    public static void setEyeTexture(ItemStack stack, String texture) {
        getRight(stack).setTexture(texture);
        getLeft(stack).setTexture(texture);
        useAdvancedModel(stack, true);
    }
    
    
    public boolean useAdvancedModel(ItemStack stack) {
        return eye.useAdvancedModel() || stack.hasTagCompound() && stack.getTagCompound().getBoolean("useAdvancedModel");
    }

    public static void useAdvancedModel(ItemStack stack, boolean use) {
        if (stack.hasTagCompound())
            stack.getTagCompound().setBoolean("useAdvancedModel", use);
    }
}

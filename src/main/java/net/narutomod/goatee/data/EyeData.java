package net.narutomod.goatee.data;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public enum EyeData {
    LEFT('L'),
    RIGHT('R');
    public char letter;

    EyeData(char letter) {
        this.letter = letter;
    }

    public int getColor(ItemStack stack) {
        return stack.hasTagCompound() && stack.getTagCompound().hasKey("color" + letter) ? stack.getTagCompound().getInteger("color" + letter) : 0xffffff;
    }

    public void setColor(ItemStack stack, int color) {
        stack.getTagCompound().setInteger("color" + letter, (color & 0x00FFFFFF) | 0x20000000);
    }

    public boolean hasColor(ItemStack stack) {
        return stack.hasTagCompound() && stack.getTagCompound().hasKey("color" + letter);
    }

    /**
     * Tint the item icon with eye color
     */
    public static void createColoredIcon(Item item) {
        IItemColor colorHandler = (stack, tintIndex) -> {
            if (tintIndex == 0)
                return EyeData.RIGHT.getColor(stack);

            if (tintIndex == 1)
                return EyeData.LEFT.getColor(stack);

            return 0xFFFFFF; // Fallback
        };

        Minecraft.getMinecraft().getItemColors().registerItemColorHandler(colorHandler, item);
    }
}

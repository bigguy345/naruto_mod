package net.narutomod.goatee.data;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class JutsuKey {

    public final int keyId;
    public JutsuKeyFunction task;
    public String name, taskTooltip; //i.e "Amaterasu", "&c&l chattext.amaterasu"

    public JutsuKey(int keyId) {
        this.keyId = keyId;
    }

    public JutsuKey setName(String name) {
        this.name = name;
        return this;
    }

    public JutsuKey setTask(JutsuKeyFunction task) {
        this.task = task;
        return this;
    }

    public JutsuKey setTooltip(String tooltip) {
        this.taskTooltip = tooltip;
        return this;
    }

    public boolean hasTask() {
        return task != null;
    }

    public boolean fire(boolean isPressed, ItemStack stack, EntityPlayer player) {
        boolean result = false;

        try {
            if (task != null)
                result = task.apply(isPressed, stack, player);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return result;
    }

    public interface JutsuKeyFunction {
        boolean apply(boolean isPressed, ItemStack stack, EntityPlayer player);
    }
}



package net.narutomod.goatee.data.sidedata;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.narutomod.goatee.util.NbtUtils;
import net.narutomod.item.ItemRinneganTomoe;

import java.util.*;

public class RinneganTomoeSideData extends SideData {
    protected final ItemRinneganTomoe.Base tomoe;

    Map<Integer, Integer> stateColors = new HashMap<>();
    Map<Integer, String> stateTextures = new HashMap<>();

    public RinneganTomoeSideData(Side side, ItemStack stack, ItemRinneganTomoe.Base item) {
        super(side, stack, item);
        tomoe = item;
        
        deserialize(tag);
    }

    public void serialiseToTag(String tagKey, int tomoeState, Object value) {
        boolean hasTag = tag.hasKey(tagKey, 10);
        NBTTagCompound tag = hasTag ? this.tag.getCompoundTag(tagKey) : new NBTTagCompound();
        NbtUtils.writeValue(tag, String.valueOf(tomoeState), value);
        if (!hasTag)
            this.tag.setTag(tagKey, tag);
    }

    public void deserialize(NBTTagCompound compound) {
        if (compound.hasKey("stateColors"))
            stateColors = NbtUtils.deserializeIntIntMap(tag, "stateColors");

        if (compound.hasKey("stateTextures"))
            stateTextures = NbtUtils.deserializeIntStringMap(tag, "stateTextures");
    }

    public int getColor(int tomoeState) {
        return stateColors.get(tomoeState);
    }

    public SideData setColor(int tomoeState, int col) {
        stateColors.put(tomoeState, col);
        serialiseToTag("stateColors", tomoeState, col);
        return this;
    }

    public String getTexture(int tomoeState) {
        return stateTextures.get(tomoeState);
    }

    public SideData setTexture(int tomoeState, String texture) {
        stateTextures.put(tomoeState, texture);
        serialiseToTag("stateTextures", tomoeState, texture);
        return this;
    }

    public int getFinalColor(EntityLivingBase entity) {
        int state = getTomoeState();
        if (stateColors.containsKey(state))
            return stateColors.get(state);

        return super.getFinalColor(entity);
    }

    public String getFinalTexture(EntityLivingBase entity) {
        String tex = stateTextures.get(getTomoeState());
        if (tex != null && !tex.isEmpty())
            return tex;

        return super.getFinalTexture(entity);
    }

    public int getTomoeState() {
        return ItemRinneganTomoe.getTomoeStatus(stack);
    }
}

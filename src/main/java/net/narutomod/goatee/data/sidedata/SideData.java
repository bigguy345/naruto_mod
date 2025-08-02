package net.narutomod.goatee.data.sidedata;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.narutomod.item.ItemDojutsu;

public class SideData {
    public enum Side {
        LEFT,
        RIGHT;
    }

    public final Side side;
    public final ItemStack stack;
    public final ItemDojutsu.Base item;
    protected NBTTagCompound tag;

    protected int color = 0xffffff;
    protected String texture = "", eyeBaseTexture = "";
    protected boolean isTextureHD;

    protected float offsetX, offsetY;
    protected float scale = 1;

    public boolean inactive;
    public String inactiveTexture;
    public int inactiveColor;

    public SideData(Side side, ItemStack stack, ItemDojutsu.Base item) {
        this.side = side;
        this.stack = stack;
        this.item = item;


        String Side = side.toString();
        if (stack.getTagCompound().hasKey(Side))
            deserialize(tag = stack.getTagCompound().getCompoundTag(Side));
        else
            stack.getTagCompound().setTag(Side, tag = new NBTTagCompound());
    }

    public void deserialize(NBTTagCompound compound) {
        if (compound.hasKey("color"))
            color = compound.getInteger("color");

        if (compound.hasKey("texture"))
            texture = compound.getString("texture");
    }

    public boolean hasColor() {
        return color != 0xffffff;
    }

    public int getColor() {
        return color;
    }

    public SideData setColor(int col) {
        tag.setInteger("color", color = col);
        return this;
    }

    public int getFinalColor(EntityLivingBase entity) {
        return hasColor() ? getColor() : 0xffffff;
    }

    public boolean isTextureHD() {
        return isTextureHD;
    }

    public SideData setTextureHD(boolean bo) {
        tag.setBoolean("isTextureHD", isTextureHD = bo);
        return this;
    }

    public boolean hasTexture() {
        return texture != null && !texture.isEmpty();
    }

    public String getTexture() {
        return texture;
    }

    public SideData setTexture(String tex) {
        tag.setString("texture", texture = tex);
        return this;
    }

    public SideData setTexture(ItemStack stack) {
        tag.setString("texture", texture = getEyeTexture(stack, null));
        return this;
    }
    
    public String getFinalTexture(EntityLivingBase entity){
        if (hasTexture())
            return getTexture();


        if (side == Side.RIGHT)
            return item.getRightEyeTexture(stack, entity, this);
        else
            return item.getLeftEyeTexture(stack, entity, this);
    }

    public boolean hasEyeBaseTexture() {
        return !eyeBaseTexture.isEmpty();
    }

    public String getEyeBaseTexture() {
        return eyeBaseTexture;
    }

    public SideData setEyeBaseTexture(String tex) {
        tag.setString("eyeBaseTexture", eyeBaseTexture = tex);
        return this;
    }

    public float getOffsetX() {
        return offsetX;
    }

    public SideData setOffsetX(float offsetX) {
        tag.setFloat("offsetX", this.offsetX = offsetX);
        return this;
    }

    public float getOffsetY() {
        return offsetY;
    }

    public SideData setOffsetY(float offsetY) {
        tag.setFloat("offsetY", this.offsetY = offsetY);
        return this;
    }

    public float getScale() {
        return scale;
    }

    public SideData setScale(float scale) {
        tag.setFloat("scale", this.scale = scale);
        return this;
    }

    //Just in case entity is needed
    public static String getEyeTexture(ItemStack stack, Entity entity) {
        return stack.getItem().getArmorTexture(stack, entity, EntityEquipmentSlot.HEAD, null);
    }
}

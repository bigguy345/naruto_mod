package net.narutomod.goatee.client.hud.wheel;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.narutomod.ModConfig;
import net.narutomod.goatee.client.Sounds;
import net.narutomod.goatee.data.NarutoData;
import net.narutomod.goatee.data.WheelData;
import net.narutomod.goatee.network.packets.NarutoSyncData;
import net.narutomod.item.*;
import net.narutomod.keybind.JutsuKeys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.lang.reflect.Field;

import static org.lwjgl.opengl.GL11.*;

public class HUDItemStackWheel extends GuiScreen {

    ScaledResolution scaledResolution;
    public ItemStackWheelSegment[] wheelSlot = new ItemStackWheelSegment[6];

    private float rotation;
    float guiAnimationScale = 0, animStartValue = 1, undoMCScaling = 1;
    long timeOpened, timeClosed;

    public int hoveredSlot = -1;
    boolean keyDown;
    boolean configureEnabled;

    public NarutoData data;

    public boolean isClosing;

    public int mouseX;
    public int mouseY;
    public static boolean IS_OPEN;

    public static final int OPEN_TIME = 2500, CLOSE_TIME = 400;
    public ItemStack selectedItem;

    public float easeOutExpo(double x) {
        return x >= 1 ? 1 : (float) (1 - Math.pow(2, -10 * x));
    }

    public HUDItemStackWheel(WheelData wheelData) {
        mc = Minecraft.getMinecraft();
        data = NarutoData.getClient();

        for (int i = 0; i < 6; i++) {
            wheelSlot[i] = new ItemStackWheelSegment(this, i);
            wheelSlot[i].setItem(wheelData.get(i));
        }
        NarutoSyncData.requestSync(data);
        
        // Stops the GUI from un-pressing all keys for you.
        mc.inGameHasFocus = false;
        mc.mouseHelper.ungrabMouseCursor();
    }

    @Override
    public void initGui() {
        super.initGui();
        // Prevents replaying the open animation on screen resize
        if (timeOpened == 0)
            timeOpened = Minecraft.getSystemTime();

        scaledResolution = new ScaledResolution(mc);


        float factor = scaledResolution.getScaleFactor();
        undoMCScaling = 1f / factor * 2f;
        if (factor == 1) {
            if (mc.displayHeight < 260)
                undoMCScaling = 0.3f;
            else if (mc.displayHeight < 350)
                undoMCScaling = 0.45f;
            else if (mc.displayHeight < 720)
                undoMCScaling = 0.7f;
            else if (mc.displayWidth < 650)
                undoMCScaling = 1f;
        } else if (factor == 2) {
            if (mc.displayHeight < 530)
                undoMCScaling = 0.413f;
            else if (mc.displayHeight < 600)
                undoMCScaling = 0.475f;
            else if (mc.displayHeight < 720)
                undoMCScaling = 1f / scaledResolution.getScaleFactor() * 1;
            else
                undoMCScaling = 1;
        } else if (factor == 3) {
            if (mc.displayHeight < 730)
                undoMCScaling = 1f / 3;
            else if (mc.displayHeight < 930)
                undoMCScaling = 0.425f;
            else if (mc.displayHeight < 1000)
                undoMCScaling = 0.6f;
            else if (mc.displayWidth < 1300)
                undoMCScaling = 0.8f;
            else if (mc.displayHeight < 1250) {
                undoMCScaling = 0.75f;
                glTranslatef(0, -15, 0);
            } else
                undoMCScaling = 0.99f;
        }


        int x = (int) ((this.width / 2) * undoMCScaling + 190);
        int y = (this.height / 2) - 100;
        if (undoMCScaling < 1) {
            x += 20;
            y -= 25;
        }

        mc.gameSettings.keyBindForward.setKeyConflictContext(KeyConflictContext.UNIVERSAL);
        mc.gameSettings.keyBindBack.setKeyConflictContext(KeyConflictContext.UNIVERSAL);
        mc.gameSettings.keyBindLeft.setKeyConflictContext(KeyConflictContext.UNIVERSAL);
        mc.gameSettings.keyBindRight.setKeyConflictContext(KeyConflictContext.UNIVERSAL);
        mc.gameSettings.keyBindJump.setKeyConflictContext(KeyConflictContext.UNIVERSAL);
        mc.gameSettings.keyBindSprint.setKeyConflictContext(KeyConflictContext.UNIVERSAL);
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 1)
            selectSlot(-1);
    }
    
    public void selectSlot(int slotID) {
        if (hoveredSlot == slotID)
            return;

        if (hoveredSlot != -1)
            wheelSlot[hoveredSlot].setHoveredState(false);
        if (slotID != -1) {
            wheelSlot[slotID].setHoveredState(true);
            Minecraft.getMinecraft().player.playSound(Sounds.get("wheel_select"), 1, 1);
        }
        hoveredSlot = slotID;
    }

    public void calculateHoveredSlot(float HALF_WIDTH, float HALF_HEIGHT, boolean configureEnabled) {
        if (isClosing)
            return;
        final float deltaX = HALF_WIDTH - mouseX;
        final float deltaY = HALF_HEIGHT - mouseY;
        float radius = 98;
        radius *= undoMCScaling * guiAnimationScale;
        if (Math.sqrt(deltaX * deltaX + deltaY * deltaY) > radius) {
            final float radians = (float) Math.atan2(deltaY, deltaX);
            final float degree = Math.round(radians * (180 / Math.PI));

            int hoveredSlot = (int) ((degree - 180) / -60) - 1;
            if (hoveredSlot == -1)
                hoveredSlot = 5;

            boolean justOpened = Minecraft.getSystemTime() - timeOpened < 50;
            if (!justOpened && hoveredSlot != this.hoveredSlot && !configureEnabled) {
                selectSlot(hoveredSlot);

                if (!isClosing)
                    selectedItem = wheelSlot[hoveredSlot].data.stack;
            }
        }
    }

    public void update() {
        if (isClosing && guiAnimationScale >= 0) {
            float updateTime = (float) (Minecraft.getSystemTime() - timeClosed) / CLOSE_TIME;
            float inSine = (float) (1 - Math.cos((updateTime * Math.PI) / 2));
            guiAnimationScale = lerp(animStartValue, 0, inSine);

            if (guiAnimationScale <= 0.05)
                onClose(POST_CLOSE);
        } else if (guiAnimationScale < 1) {
            float updateTime = (float) (Minecraft.getSystemTime() - timeOpened) / OPEN_TIME;
            guiAnimationScale = (float) easeOutExpo(updateTime);
        }

        calculateHoveredSlot((float) this.width / 2, (float) this.height / 2, configureEnabled);
        
        int code = JutsuKeys.dojutsuWheel.getKeyCode();
        keyDown = JutsuKeys.dojutsuWheel.getKeyCode() < 0 ? Mouse.isButtonDown(code + 100) : Keyboard.isKeyDown(code);
        if (!keyDown && !configureEnabled && !isClosing)
            onClose(PRE_CLOSE);
        
    }

    public static float lerp(float start, float end, float alpha) {
        return start + (end - start) * alpha;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;

        update();

        int gradientColor = ((int) (255 * 0.2f * guiAnimationScale) << 24);
        this.drawGradientRect(0, 0, this.width, this.height, gradientColor, gradientColor);
        drawGradientRectWithFade(0, 0, width, height, 0x66000000, 0xff000000, guiAnimationScale);

        final float HALF_WIDTH = (float) this.width / 2;
        final float HALF_HEIGHT = (float) this.height / 2;

        glPushMatrix();

        GlStateManager.disableBlend();
        glPushMatrix();
        GL11.glTranslatef(HALF_WIDTH, HALF_HEIGHT, 0);
        GL11.glScalef(undoMCScaling, undoMCScaling, undoMCScaling);
        float guiVariantScale = (ItemStackWheelSegment.variant == 0 ? 0.75f : 0.9f);
        float playerScale = guiAnimationScale * guiVariantScale * (1.5f);
        GL11.glScalef(playerScale, playerScale, playerScale);
        GL11.glTranslatef(-HALF_WIDTH, -HALF_HEIGHT + (8), 0);
        renderPlayer(mouseX, mouseY, partialTicks);
        glPopMatrix();


        glPushMatrix();
        GL11.glTranslatef(HALF_WIDTH, HALF_HEIGHT, 0);
        GL11.glScalef(undoMCScaling, undoMCScaling, 0);
        GL11.glScalef(guiAnimationScale, guiAnimationScale, 0);
        float wheelDiameter = 1.4f;
        GL11.glScalef(wheelDiameter, wheelDiameter, 0);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        for (int i = 0; i < 6; i++) {
            glPushMatrix();
            GL11.glRotatef(i * -60, 0, 0, 1);
            float segmentScale = 1f + 0.1f * wheelSlot[i].getSegmentScale();
            GL11.glScalef(segmentScale, segmentScale, 0);

            if (i % 3 == 0) {
                GL11.glTranslatef(0, -80f, 0);
            } else {
                GL11.glTranslatef(0, -95f, 0);
            }


            GL11.glRotatef(i * 60, 0, 0, 1);
            if (i == 1 || i == 2) {
                GL11.glTranslatef(10, 0, 0);
            } else if (i == 4 || i == 5) {
                GL11.glTranslatef(-10, 0, 0);
            }
            wheelSlot[i].draw(mc.fontRenderer);


            glPopMatrix();
        }
        glPopMatrix();
        glPopMatrix();


        int interpolatedAlpha = (int) MathHelper.clamp(255 * guiAnimationScale, 0, 255);
        if (interpolatedAlpha < 5)
            return;

        int color = (interpolatedAlpha << 24) | (0xff << 16) | (0xff << 8) | 0xff;
        if (!GuiScreen.isShiftKeyDown())
            fontRenderer.drawString(I18n.format("dojutsuwheel.gui.display_info"), 4, height - 15, color);
        else {
            fontRenderer.drawString(I18n.format("dojutsuwheel.gui.deselect_slot"), 4, height - 55, color);
            fontRenderer.drawString(I18n.format("dojutsuwheel.gui.how_to_insert"), 4, height - 45, color);
            fontRenderer.drawString(I18n.format("dojutsuwheel.gui.insert_info"), 4, height - 35, color);
            fontRenderer.drawString(I18n.format("dojutsuwheel.gui.extract_info"), 4, height - 25, color);
            fontRenderer.drawString(I18n.format("dojutsuwheel.gui.extract_to_inventory"), 4, height - 15, color);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }


    public boolean isMouseOverRenderer(int x, int y) {
        int width = this.width / 2;
        int height = this.height / 2;
        return x >= width - 60 && x <= width + 60 && y >= height - 90 && y <= height + 90;
    }

    public void renderPlayer(int i, int j, float partialTicks) {
        if (isMouseOverRenderer(i, j) && Mouse.isButtonDown(0)) {
            rotation -= Mouse.getDX() * 0.75f;
        }

        EntityPlayer entity = mc.player;
        IS_OPEN = hoveredSlot != -1;

        int l = this.width / 2;
        int i1 = this.height / 2 + 60;

        float oldLimbSwing = entity.limbSwingAmount;
        boolean isInvisible = entity.isInvisible(), isImmunetoFire = false, oldFlying = entity.capabilities.isFlying, oldSneaking = entity.isSneaking();
        int oldArrowCount = entity.getArrowCountInEntity();
        Entity oldRidingEntity = null;
        entity.limbSwingAmount = entity.prevLimbSwingAmount = 0; // Removes moving animation
        entity.setInvisible(false); // Removes invisibility
        entity.setArrowCountInEntity(0);
        entity.capabilities.isFlying = true;
        entity.setSneaking(false);
        try {
            Field field = ReflectionHelper.findField(Entity.class, "isImmuneToFire", "field_70178_ae");
            field.setAccessible(true);
            isImmunetoFire = field.getBoolean(entity);
            field.setBoolean(entity, true);

            field = ReflectionHelper.findField(Entity.class, "ridingEntity", "field_184239_as");
            field.setAccessible(true);
            oldRidingEntity = (Entity) field.get(entity);
            field.set(entity, null);
        } catch (Exception e) {
            e.printStackTrace();
        }


        ItemStack oldItem = entity.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
        ItemStack oldOffhand = entity.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND);
        ItemStack oldHelmet = ItemDojutsu.getWorn(entity);
        ItemStack oldChest = entity.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        ItemStack oldLegs = entity.getItemStackFromSlot(EntityEquipmentSlot.LEGS);

        if (!(oldItem.getItem() instanceof ItemJutsu.Base))
            entity.inventory.mainInventory.set(entity.inventory.currentItem, ItemStack.EMPTY);

        if (!(oldOffhand.getItem() instanceof ItemJutsu.Base))
            entity.inventory.offHandInventory.set(0, ItemStack.EMPTY);

        int oldTomoeSlot = -1;
        if (hoveredSlot != -1) {
            if (ItemRinneganTomoe.isTomoe(oldHelmet) && ItemRinneganTomoe.getCompatibleStatus(selectedItem) != -1 && !ItemRinnegan.isRinnesharinganActivated(oldHelmet)) {
                oldTomoeSlot = ItemRinneganTomoe.getTomoeStatus(oldHelmet);
                ItemRinneganTomoe.setTomoeStatus(oldHelmet, ItemRinneganTomoe.getCompatibleStatus(selectedItem));
            } else {
                entity.setItemStackToSlot(EntityEquipmentSlot.HEAD, selectedItem);

                if (ItemTenseigan.isTenseigan(selectedItem)) {
                    entity.setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(ItemTenseigan.body));
                    entity.setItemStackToSlot(EntityEquipmentSlot.LEGS, new ItemStack(ItemTenseigan.legs));
                } else if (ItemRinnegan.isRinnesharinganActivated(selectedItem)) {
                    entity.setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(ItemRinnegan.body));
                    entity.setItemStackToSlot(EntityEquipmentSlot.LEGS, new ItemStack(ItemRinnegan.legs));
                } else {
                    if (oldChest.getItem() == ItemRinnegan.body || oldChest.getItem() == ItemTenseigan.body)
                        entity.setItemStackToSlot(EntityEquipmentSlot.CHEST, ItemStack.EMPTY);
                    if (oldLegs.getItem() == ItemRinnegan.legs || oldLegs.getItem() == ItemTenseigan.legs)
                        entity.setItemStackToSlot(EntityEquipmentSlot.LEGS, ItemStack.EMPTY);
                }
            }
        }

        boolean oldRun = ModConfig.NARUTO_RUN;
        ModConfig.NARUTO_RUN = false;

        GlStateManager.enableColorMaterial();
        glPushMatrix();
        GL11.glTranslatef(l, i1, 60F);
        GL11.glScalef(-220, 220, 70);
        GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
        float f2 = entity.renderYawOffset;
        float f3 = entity.rotationYaw;
        float f4 = entity.rotationPitch;
        float f7 = entity.rotationYawHead;
        float f5 = (float) (l) - i;
        float f6 = (float) (i1 - 50) - j;
        GL11.glRotatef(135F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GL11.glRotatef(-135F, 0.0F, 1.0F, 0.0F);
        //   GL11.glRotatef(-(float) Math.atan(f6 / 800F) * 20F, 1.0F, 0.0F, 0.0F);
        entity.prevRenderYawOffset = entity.renderYawOffset = rotation;
        entity.prevRotationYaw = entity.rotationYaw = (float) Math.atan(f5 / 80F) * 10F + rotation;
        entity.rotationPitch = entity.prevRotationPitch = -(float) Math.atan(f6 / 80F) * 10F;
        entity.prevRotationYawHead = entity.rotationYawHead = entity.rotationYaw;

        GL11.glTranslatef(0.0F, -0.125f - 1.2f, 1F);
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        rendermanager.setPlayerViewY(180.0F);
        rendermanager.setRenderShadow(false);
        rendermanager.renderEntity(entity, 0.0, 0.0, 1f, 0, partialTicks, false);
        rendermanager.setRenderShadow(true);

        entity.prevRenderYawOffset = entity.renderYawOffset = f2;
        entity.prevRotationYaw = entity.rotationYaw = f3;
        entity.rotationPitch = entity.prevRotationPitch = f4;
        entity.prevRotationYawHead = entity.rotationYawHead = f7;

        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        glPopMatrix();

        ModConfig.NARUTO_RUN = oldRun;
        entity.limbSwingAmount = entity.prevLimbSwingAmount = oldLimbSwing;
        entity.setInvisible(isInvisible);
        entity.capabilities.isFlying = oldFlying;
        entity.setSneaking(oldSneaking);
        entity.setArrowCountInEntity(oldArrowCount);

        try {
            Field field = ReflectionHelper.findField(Entity.class, "isImmuneToFire", "field_70178_ae");
            field.setAccessible(true);
            field.setBoolean(entity, isImmunetoFire);

            field = ReflectionHelper.findField(Entity.class, "ridingEntity", "field_184239_as");
            field.setAccessible(true);
            field.set(entity, oldRidingEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (oldTomoeSlot != -1)
            ItemRinneganTomoe.setTomoeStatus(oldHelmet, oldTomoeSlot);

        entity.inventory.mainInventory.set(entity.inventory.currentItem, oldItem);
        entity.inventory.offHandInventory.set(0, oldOffhand);
        entity.inventory.armorInventory.set(EntityEquipmentSlot.HEAD.getIndex(), oldHelmet);
        entity.inventory.armorInventory.set(EntityEquipmentSlot.CHEST.getIndex(), oldChest);
        entity.inventory.armorInventory.set(EntityEquipmentSlot.LEGS.getIndex(), oldLegs);
        
        IS_OPEN = false;
    }

    public void drawDefaultBackground() {
    }

    protected void drawGradientRectWithFade(int left, int top, int right, int bottom, int startColor, int endColor, float fade) {
        int red = (startColor >> 16) & 0xFF;
        int green = (startColor >> 8) & 0xFF;
        int blue = (startColor) & 0xFF;
        int alpha = (startColor >> 24 & 255);
        int newAlpha = Math.min(255, Math.max(0, (int) (alpha * fade)));
        int newStart = (newAlpha << 24) | (red << 16) | (green << 8) | blue;

        red = (endColor >> 16) & 0xFF;
        green = (endColor >> 8) & 0xFF;
        blue = (endColor) & 0xFF;
        alpha = (endColor >> 24 & 255);
        newAlpha = Math.min(255, Math.max(0, (int) (alpha * fade)));
        int newEnd = (newAlpha << 24) | (red << 16) | (green << 8) | blue;

        drawGradientRect(left, top, right, bottom, newStart, newEnd);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    protected final int PRE_CLOSE = 0, POST_CLOSE = 1;

    public void onClose(int closeType) {
        if (closeType == PRE_CLOSE) {
            if (hoveredSlot != -1) {
                wheelSlot[hoveredSlot].selectItem();
                selectedItem = wheelSlot[hoveredSlot].data.stack;
            }

            mc.inGameHasFocus = true;
            mc.mouseHelper.grabMouseCursor();
            timeClosed = Minecraft.getSystemTime();
            animStartValue = guiAnimationScale;
            isClosing = true;
        } else {
            Keyboard.enableRepeatEvents(false);
            mc.displayGuiScreen(null);
            this.mc.setIngameFocus();

            mc.gameSettings.keyBindForward.setKeyConflictContext(KeyConflictContext.IN_GAME);
            mc.gameSettings.keyBindBack.setKeyConflictContext(KeyConflictContext.IN_GAME);
            mc.gameSettings.keyBindLeft.setKeyConflictContext(KeyConflictContext.IN_GAME);
            mc.gameSettings.keyBindRight.setKeyConflictContext(KeyConflictContext.IN_GAME);
            mc.gameSettings.keyBindJump.setKeyConflictContext(KeyConflictContext.IN_GAME);
            mc.gameSettings.keyBindSprint.setKeyConflictContext(KeyConflictContext.IN_GAME);
        }
    }

    public void handleInput() throws IOException {
        super.handleInput();
        KeyBinding.updateKeyBindState();
    }
}

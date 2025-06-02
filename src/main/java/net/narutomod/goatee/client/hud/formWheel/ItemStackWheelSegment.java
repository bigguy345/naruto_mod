package net.narutomod.goatee.client.hud.formWheel;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.narutomod.goatee.client.hud.WheelSegment;
import net.narutomod.goatee.data.WheelData;
import net.narutomod.goatee.network.PacketHandler;
import net.narutomod.goatee.network.packets.NarutoWheelData;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.glScaled;
import static org.lwjgl.opengl.GL11.glTranslatef;

class ItemStackWheelSegment extends WheelSegment {
    public HUDItemStackWheel parent;
    public WheelData.Segment data = new WheelData.Segment();

    ItemStackWheelSegment(HUDItemStackWheel parent, int index) {
        this(parent, 0, 0, index);
    }

    ItemStackWheelSegment(HUDItemStackWheel parent, int posX, int posY, int index) {
        super(index);
        this.parent = parent;
        this.posX = posX;
        this.posY = posY;
    }

    public void selectItem() {
        PacketHandler.Instance.sendToServer(new NarutoWheelData(index, ""));
    }

    public void setItem(WheelData.Segment data, boolean updateServer) {
        this.data = data;
        if (updateServer)
            PacketHandler.Instance.sendToServer(new NarutoWheelData(index, ""));
    }

    @Override
    protected void drawWheelItem(FontRenderer fontRenderer) {
        if (data.stack.isEmpty())
            return;

        if (index == 1 || index == 5) {
            glTranslatef(0, 10, 0);
        } else if (index == 2 || index == 4) {
            glTranslatef(0, -10, 0);
        }

        switch (index) {
            case 0:
                glTranslatef(-1, -6, 0);
                break;
            case 1:
                glTranslatef(-8, -1, 0);
                break;
            case 2:
                glTranslatef(-7, 11, 0);
                break;
            case 3:
                glTranslatef(0, 14f, 0);
                break;
            case 4:
                glTranslatef(3, 11, 0);
                break;
            default:
                glTranslatef(6, -2, 0);
        }

        glTranslatef(-15, (float) -20, 0);
        GL11.glPushMatrix();
        glScaled(2, 2, 2);
        RenderHelper.enableStandardItemLighting();
        Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(data.stack, 0, 0);
        GL11.glPopMatrix();


        glTranslatef(0, 16, 0);
        String name = data.stack.getDisplayName();
        if (name.contains("'s"))
            name = name.substring(name.indexOf("'s") + 2);
        if (name.contains("(creative)"))
            name = name.substring(0, name.indexOf("(creative)"));
        drawCenteredString(fontRenderer, name, 17, 17, 0xFFFFFFFF);
    }
}

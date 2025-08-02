package net.narutomod.goatee.client;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.narutomod.goatee.client.model.ModelDojutsu;
import net.narutomod.goatee.data.sidedata.SideData;

public class RenderUtils {

    public static void disableLightMap() {
        GlStateManager.disableLighting();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
    }

    public static void enableLightmap(Entity entity) {
        int i = entity.getBrightnessForRender();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) (i % 65536), (float) (i / 65536));
        GlStateManager.enableLighting();
    }


    public static ModelDojutsu convertToHD(ModelDojutsu model, SideData side) {
        if (side.side == SideData.Side.RIGHT) {
            model.textureWidth = 10;
            model.textureHeight = 5;

            model.rightEye = new ModelRenderer(model);
            model.rightEye.setRotationPoint(0.0F, 0.0F, 0.0F);
            model.setBox(model.rightEye, 0, 0, -5.1F + side.getOffsetX(), -5.6F - side.getOffsetY(), -4.11F, 5, 5, 0, 0.11F, false);
        }

        if (side.side == SideData.Side.LEFT) {
            model.textureWidth = 10;
            model.textureHeight = 5;

            model.leftEye = new ModelRenderer(model);
            model.leftEye.setRotationPoint(0.0F, 0.0F, 0.0F);
            model.setBox(model.leftEye, 5, 0, -0.0F + side.getOffsetX(), -5.6F - side.getOffsetY(), -4.11F, 5, 5, 0, 0.11F, false);
        }

        return model;
    }
}

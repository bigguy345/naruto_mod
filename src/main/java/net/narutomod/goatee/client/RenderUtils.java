package net.narutomod.goatee.client;

import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;

import java.lang.reflect.Field;

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

    public static void flipQuads(ModelBox box) {
        try {
            Field quadListField = ModelBox.class.getDeclaredField("quadList");
            quadListField.setAccessible(true);
            TexturedQuad[] quads = (TexturedQuad[]) quadListField.get(box);
            for (TexturedQuad quad : quads)
                quad.flipFace();
        } catch (Exception e) {

        }
    }
}

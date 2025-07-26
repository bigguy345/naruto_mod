package net.narutomod.goatee.client.model;

import com.google.common.collect.Maps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.narutomod.goatee.client.RenderUtils;
import net.narutomod.goatee.data.SideData;
import net.narutomod.item.ItemDojutsu;
import org.lwjgl.opengl.GL11;

import java.util.Map;

public class ModelDojutsu extends ModelBiped {
    private static final Map<String, ResourceLocation> DOJUTSU_TEXTURE_RES_MAP = Maps.newHashMap();

    public ModelRenderer onface;
    public ModelRenderer hornRight;
    public ModelRenderer hornLeft;
    public ModelRenderer hornMiddle;
    public ModelRenderer forehead; //kekkei mora rinnesharin

    public ModelRenderer leftEye, rightEye; //actual eyes
    public ModelRenderer eyeBaseL, eyeBaseR, eyeBaseBoth; //just the eyes base without the dojutsu

    public boolean headwearHide;
    public boolean headwearShine;
    public boolean highlightHide;
    public boolean foreheadHide;

    public String rightTexture, leftTexture, eyeBaseTexture;
    public String rinnesharinganTexture; // full s06p helmet texture with horns and all

    public boolean isS06P;
    public int leftColor = 0xffffff, rightColor = 0xffffff;

    public ModelDojutsu() {
        this.textureWidth = 64;
        this.textureHeight = 16;

        bipedHead = new ModelRenderer(this);
        bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
        bipedHead.cubeList.add(new ModelBox(this.bipedHead, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.01F, false));

        onface = new ModelRenderer(this);
        onface.setRotationPoint(0.0F, 0.0F, 0.0F);
        bipedHead.addChild(onface);
        onface.cubeList.add(new ModelBox(onface, 32, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.01F, false));

        hornRight = new ModelRenderer(this);
        hornRight.setRotationPoint(-2.5F, -6.0F, -4.0F);
        bipedHead.addChild(hornRight);
        setRotationAngle(hornRight, 0.5236F, 0.3491F, -0.1309F);
        hornRight.cubeList.add(new ModelBox(hornRight, 0, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.1F, false));

        ModelRenderer bone2 = new ModelRenderer(this);
        bone2.setRotationPoint(0.0F, -1.0F, 0.0F);
        hornRight.addChild(bone2);
        setRotationAngle(bone2, -0.1745F, 0.0F, 0.0F);
        bone2.cubeList.add(new ModelBox(bone2, 0, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.2F, false));

        ModelRenderer bone3 = new ModelRenderer(this);
        bone3.setRotationPoint(0.0F, -1.0F, 0.0F);
        bone2.addChild(bone3);
        setRotationAngle(bone3, -0.1745F, 0.0F, 0.0F);
        bone3.cubeList.add(new ModelBox(bone3, 0, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.1F, false));

        ModelRenderer bone4 = new ModelRenderer(this);
        bone4.setRotationPoint(0.0F, -0.9F, 0.0F);
        bone3.addChild(bone4);
        setRotationAngle(bone4, -0.1745F, 0.0F, 0.0F);
        bone4.cubeList.add(new ModelBox(bone4, 0, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.0F, false));

        hornLeft = new ModelRenderer(this);
        hornLeft.setRotationPoint(2.5F, -6.0F, -4.0F);
        bipedHead.addChild(hornLeft);
        setRotationAngle(hornLeft, 0.5236F, -0.3491F, 0.1309F);
        hornLeft.cubeList.add(new ModelBox(hornLeft, 0, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.1F, true));

        ModelRenderer bone5 = new ModelRenderer(this);
        bone5.setRotationPoint(0.0F, -1.0F, 0.0F);
        hornLeft.addChild(bone5);
        setRotationAngle(bone5, -0.1745F, 0.0F, 0.0F);
        bone5.cubeList.add(new ModelBox(bone5, 0, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.2F, true));

        ModelRenderer bone6 = new ModelRenderer(this);
        bone6.setRotationPoint(0.0F, -1.0F, 0.0F);
        bone5.addChild(bone6);
        setRotationAngle(bone6, -0.1745F, 0.0F, 0.0F);
        bone6.cubeList.add(new ModelBox(bone6, 0, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.1F, true));

        ModelRenderer bone7 = new ModelRenderer(this);
        bone7.setRotationPoint(0.0F, -0.9F, 0.0F);
        bone6.addChild(bone7);
        setRotationAngle(bone7, -0.1745F, 0.0F, 0.0F);
        bone7.cubeList.add(new ModelBox(bone7, 0, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.0F, true));

        bipedHeadwear = new ModelRenderer(this);
        bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
        bipedHeadwear.cubeList.add(new ModelBox(this.bipedHeadwear, 32, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.4F, false));

        hornMiddle = new ModelRenderer(this);
        hornMiddle.setRotationPoint(0.0F, -6.25F, -3.75F);
        bipedHeadwear.addChild(hornMiddle);
        setRotationAngle(hornMiddle, 0.5236F, 0.0F, 0.0F);


        ModelRenderer bone13 = new ModelRenderer(this);
        bone13.setRotationPoint(0.0F, 0.0F, 0.0F);
        hornMiddle.addChild(bone13);
        setRotationAngle(bone13, 0.0F, -0.7854F, 0.0F);
        bone13.cubeList.add(new ModelBox(bone13, 0, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.1F, false));

        ModelRenderer bone8 = new ModelRenderer(this);
        bone8.setRotationPoint(0.0F, -1.0F, 0.0F);
        bone13.addChild(bone8);
        setRotationAngle(bone8, -0.0873F, 0.0F, 0.0873F);
        bone8.cubeList.add(new ModelBox(bone8, 0, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.2F, false));

        ModelRenderer bone9 = new ModelRenderer(this);
        bone9.setRotationPoint(0.0F, -1.0F, 0.0F);
        bone8.addChild(bone9);
        setRotationAngle(bone9, -0.0873F, 0.0F, 0.0873F);
        bone9.cubeList.add(new ModelBox(bone9, 0, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.1F, false));

        ModelRenderer bone10 = new ModelRenderer(this);
        bone10.setRotationPoint(0.0F, -0.9F, 0.0F);
        bone9.addChild(bone10);
        setRotationAngle(bone10, -0.0873F, 0.0F, 0.0873F);
        bone10.cubeList.add(new ModelBox(bone10, 0, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.0F, false));

        ModelRenderer bone11 = new ModelRenderer(this);
        bone11.setRotationPoint(0.0F, -0.8F, 0.0F);
        bone10.addChild(bone11);
        setRotationAngle(bone11, -0.0873F, 0.0F, 0.0873F);
        bone11.cubeList.add(new ModelBox(bone11, 0, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.15F, false));

        ModelRenderer bone12 = new ModelRenderer(this);
        bone12.setRotationPoint(0.0F, -0.6F, 0.0F);
        bone11.addChild(bone12);
        setRotationAngle(bone12, -0.0873F, 0.0F, 0.0873F);
        bone12.cubeList.add(new ModelBox(bone12, 0, 4, -0.5F, -0.9F, -0.5F, 1, 1, 1, -0.3F, false));

        this.forehead = new ModelRenderer(this);
        this.forehead.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.forehead.cubeList.add(new ModelBox(this.forehead, 0, 0, -1.94F, -6.62F, -4.15F, 4, 4, 0, 0.0F, false));

        //////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////
        // eyes
        eyeBaseBoth = new ModelRenderer(this);
        eyeBaseBoth.setRotationPoint(0.0F, 0.0F, 0.0F);
        
        eyeBaseL = new ModelRenderer(this);
        eyeBaseL.setRotationPoint(0.0F, 0.0F, 0.0F);

        eyeBaseR = new ModelRenderer(this);
        eyeBaseR.setRotationPoint(0.0F, 0.0F, 0.0F);

        rightEye = new ModelRenderer(this);
        rightEye.setRotationPoint(0.0F, 0.0F, 0.0F);

        leftEye = new ModelRenderer(this);
        leftEye.setRotationPoint(0.0F, 0.0F, 0.0F);

        this.eyeBaseR.cubeList.add(new ModelBox(this.eyeBaseR, 8, 8, -4F, -8F, -4.1F, 4, 8, 0, 0.1F, false));
        this.rightEye.cubeList.add(new ModelBox(this.rightEye, 24, 0, -4F, -8F, -4.1F, 4, 8, 0, 0.1F, false));

        this.eyeBaseL.cubeList.add(new ModelBox(eyeBaseL, 12, 8, 0f, -8F, -4.1f, 4, 8, 0, 0.1f, false));
        this.leftEye.cubeList.add(new ModelBox(this.leftEye, 28, 0, 0F, -8F, -4.1F, 4, 8, 0, 0.1F, false));

        this.eyeBaseBoth.cubeList.add(new ModelBox(eyeBaseBoth, 8, 8, -4f, -8F, -4.09f, 8, 8, 0, 0.1f, false));
    }

    public boolean markDirty;

    public void setBox(ModelRenderer model, int texU, int texV, float x, float y, float z, int dx, int dy, int dz, float delta, boolean mirror) {
        model.cubeList.clear();
        model.setTextureSize(textureWidth, textureHeight);
        model.cubeList.add(new ModelBox(model, texU, texV, x, y, z, dx, dy, dz, delta, mirror));
        markDirty = true;
    }

    public void reset() {
        this.textureWidth = 64;
        this.textureHeight = 16;

        setBox(this.eyeBaseR, 8, 8, -4F, -8F, -4.1F, 4, 8, 0, 0.1F, false);
        setBox(this.rightEye, 24, 0, -4F, -8F, -4.1F, 4, 8, 0, 0.1F, false);

        setBox(eyeBaseL, 4, 8, 0.1f, -8F, -4.2f, 4, 8, 0, 0.01f, false);
        setBox(this.leftEye, 28, 0, 0F, -8F, -4.1F, 4, 8, 0, 0.1F, false);

        markDirty = false;
    }

    @Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.alphaFunc(GL11.GL_GEQUAL, 0.01f);
        GlStateManager.color(1, 1, 1, 1);

        if (entityIn.isSneaking())
            GlStateManager.translate(0.0F, 0.2F, 0.0F);

        if (isS06P) {
            RenderUtils.disableLightMap();
            GlStateManager.depthMask(false);
            


            bindTexture(rinnesharinganTexture);
            this.bipedHead.render(scale);

            if (!this.foreheadHide) {
                this.copyModelAngles(this.bipedHead, this.forehead);
                this.forehead.render(scale);
            }

            if (!this.headwearHide) {
                this.copyModelAngles(this.bipedHead, this.bipedHeadwear);
                this.bipedHeadwear.render(scale);
            }

            RenderUtils.enableLightmap(entityIn);
            GlStateManager.depthMask(true);
        }

        if (!this.highlightHide) {
            this.copyModelAngles(this.bipedHead, this.eyeBaseBoth);
            this.copyModelAngles(this.bipedHead, this.eyeBaseR);
            this.copyModelAngles(this.bipedHead, this.eyeBaseL);
            this.copyModelAngles(this.bipedHead, this.rightEye);
            this.copyModelAngles(this.bipedHead, this.leftEye);

            if (eyeBaseTexture != null) {
                bindTexture(eyeBaseTexture);
                eyeBaseBoth.render(scale);
            }

            //   leftTexture = "narutomod:textures/rinneganhelmet.png";
            //   leftTexture = "narutomod:textures/rinneganhelmet.png";
            //   leftTexture = "narutomod:textures/rinneganhelmet.png";
            //   leftTexture = "narutomod:textures/rinneganhelmet.png";

            bindTexture(leftTexture);
            eyeBaseL.render(scale);

            RenderUtils.disableLightMap();
            float r = (float) (leftColor >> 16 & 255) / 255.0F;
            float g = (float) (leftColor >> 8 & 255) / 255.0F;
            float b = (float) (leftColor & 255) / 255.0F;
            GlStateManager.color(r, g, b, 1.0F);
            this.leftEye.render(scale);
            RenderUtils.enableLightmap(entityIn);

            //            rightTexture = "narutomod:textures/mangekyosharinganhelmet_obito.png";
            //            rightTexture = "narutomod:textures/rinneganhelmet.png";
            //            rightTexture = "narutomod:textures/sharinganhelmet.png";
            //            rightTexture = "narutomod:textures/tenseiganhelmet.png";
            //            rightTexture = "narutomod:textures/byakuganhelmet.png";
            bindTexture(rightTexture);
            GlStateManager.color(1, 1, 1, 1);
            eyeBaseR.render(scale);

            RenderUtils.disableLightMap();
            r = (float) (rightColor >> 16 & 255) / 255.0F;
            g = (float) (rightColor >> 8 & 255) / 255.0F;
            b = (float) (rightColor & 255) / 255.0F;
            GlStateManager.color(r, g, b, 1.0F);
            this.rightEye.render(scale);
            RenderUtils.enableLightmap(entityIn);
        }


        GlStateManager.alphaFunc(0x204, 0.1f);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        if (markDirty)
            reset();

    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
    
    public void bindTexture(String tex) {
        Minecraft.getMinecraft().renderEngine.bindTexture(getTexture(tex));
    }

    public ResourceLocation getTexture(String tex) {
        ResourceLocation resourcelocation = DOJUTSU_TEXTURE_RES_MAP.get(tex);

        if (resourcelocation == null)
            DOJUTSU_TEXTURE_RES_MAP.put(tex, resourcelocation = new ResourceLocation(tex));

        return resourcelocation;
    }

    @SideOnly(Side.CLIENT)
    public static ModelDojutsu getModel(EntityLivingBase living, ItemStack stack, ItemDojutsu.Base eye) {
        if (eye.model == null)
            eye.model = new ModelDojutsu();

        ModelDojutsu model = eye.model;

        model.isSneak = living.isSneaking();
        model.isRiding = living.isRiding();
        model.isChild = living.isChild();
       // model.headwearShine = false;

        SideData left = eye.data.getLeft(stack);
        SideData right = eye.data.getRight(stack);

        model.leftTexture = left.hasTexture() ? left.getTexture() : eye.getLeftEyeTexture(stack, living, left);
        model.rightTexture = right.hasTexture() ? right.getTexture() : eye.getRightEyeTexture(stack, living, right);
        model.rinnesharinganTexture = eye.getRinnesharinganTexture(stack, living);

        if (left.hasColor())
            model.leftColor = left.getColor();

        if (right.hasColor())
            model.rightColor = right.getColor();

        return model;
    }

}

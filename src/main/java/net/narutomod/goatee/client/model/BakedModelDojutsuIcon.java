package net.narutomod.goatee.client.model;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.narutomod.ModConfig;
import net.narutomod.goatee.data.DojutsuData;
import net.narutomod.item.ItemDojutsu;

import java.util.*;

@Mod.EventBusSubscriber(Side.CLIENT)
public class BakedModelDojutsuIcon implements IBakedModel {
    private final IBakedModel base;
    private TextureAtlasSprite texture;

    public BakedModelDojutsuIcon(IBakedModel original) {
        this.base = original;
    }

    private static final Map<String, List<BakedQuad>> RETEXTURED_QUAD_CACHE = new HashMap<>();

    @Override
    public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
        if (side != null) {
            return Collections.emptyList();
        }

        String texName = texture.getIconName();
        return RETEXTURED_QUAD_CACHE.computeIfAbsent(texName, key -> {
            List<BakedQuad> newQuads = new ArrayList<>();
            for (BakedQuad quad : base.getQuads(state, null, rand)) {
                newQuads.add(retextureQuad(quad, texture));
            }
            return newQuads;
        });
    }

    private static BakedQuad retextureQuad(BakedQuad quad, TextureAtlasSprite newSprite) {
        int[] vertexData = quad.getVertexData().clone();
        int stride = vertexData.length / 4;

        // Get the original sprite to normalize the old UVs relative to it
        TextureAtlasSprite oldSprite = quad.getSprite();

        for (int i = 0; i < 4; i++) {
            int uvIndex = i * stride + 4; // U
            int vvIndex = i * stride + 5; // V

            float oldU = Float.intBitsToFloat(vertexData[uvIndex]);
            float oldV = Float.intBitsToFloat(vertexData[vvIndex]);

            // Normalize from atlas coords → 0–16 relative to old sprite
            float relU = (oldU - oldSprite.getMinU()) / (oldSprite.getMaxU() - oldSprite.getMinU()) * 16f;
            float relV = (oldV - oldSprite.getMinV()) / (oldSprite.getMaxV() - oldSprite.getMinV()) * 16f;

            // Map into new sprite’s atlas coords
            float newU = newSprite.getInterpolatedU(relU);
            float newV = newSprite.getInterpolatedV(relV);

            vertexData[uvIndex] = Float.floatToRawIntBits(newU);
            vertexData[vvIndex] = Float.floatToRawIntBits(newV);
        }

        return new BakedQuad(vertexData, quad.getTintIndex(), quad.getFace(), newSprite, quad.shouldApplyDiffuseLighting(), quad.getFormat());
    }

    // Delegate the rest to original model
    @Override
    public boolean isAmbientOcclusion() {
        return base.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return base.isGui3d();
    }

    @Override
    public boolean isBuiltInRenderer() {
        return base.isBuiltInRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return base.getParticleTexture();
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return base.getItemCameraTransforms();
    }

    private final ItemOverrideList dynamicOverrides = new DynamicItemOverrideList();

    @Override
    public ItemOverrideList getOverrides() {
        return dynamicOverrides;
    }

    public class DynamicItemOverrideList extends ItemOverrideList {

        public DynamicItemOverrideList() {
            super(Collections.emptyList());
        }

        @Override
        public IBakedModel handleItemState(IBakedModel model, ItemStack stack, World world, EntityLivingBase entity) {
            if (!(model instanceof BakedModelDojutsuIcon))
                return model;

            BakedModelDojutsuIcon dojutsuModel = (BakedModelDojutsuIcon) model;

            String texPath;
            if (stack.getItem() instanceof ItemDojutsu.Base) {
                String customIcon = DojutsuData.getItemIcon(stack);
                texPath = customIcon != null ? customIcon : ((ItemDojutsu.Base) stack.getItem()).getItemTexture();
            } else {
                texPath = "narutomod:blocks/sharingan";
            }

            dojutsuModel.texture = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(texPath);
            return model;
        }
    }

    @SubscribeEvent
    public static void onModelBake(ModelBakeEvent event) {
        ModelResourceLocation mrl = new ModelResourceLocation("narutomod:mangekyosharinganeternalhelmet", "inventory");
        IBakedModel baseModel = event.getModelRegistry().getObject(mrl);
        if (baseModel != null) {
            event.getModelRegistry().putObject(mrl, new BakedModelDojutsuIcon(baseModel));
        }
    }

    @SubscribeEvent
    public static void onTextureStitch(TextureStitchEvent.Pre event) {
        TextureMap map = event.getMap();

        map.registerSprite(new ResourceLocation("narutomod:blocks/mangekyosharingan_eterna"));
        
        String dir = "narutomod:eye/item/";
        map.registerSprite(new ResourceLocation(dir + "indrams"));
        map.registerSprite(new ResourceLocation(dir + "madaraemss"));
        map.registerSprite(new ResourceLocation(dir + "eternalindra"));
        map.registerSprite(new ResourceLocation(dir + "eternalnaori"));
        map.registerSprite(new ResourceLocation(dir + "madaraemss"));
        map.registerSprite(new ResourceLocation(dir + "izunams"));
        map.registerSprite(new ResourceLocation(dir + "indraems"));
    }
}
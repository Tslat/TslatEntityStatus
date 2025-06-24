package net.tslat.tes.api.util.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.BlitRenderState;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.tslat.tes.api.object.TESHudRenderContext;
import net.tslat.tes.api.util.TESRenderUtil;
import org.joml.Matrix3x2f;
import org.joml.Matrix4f;

import java.util.function.Function;

/**
 * Factory-based class used for rendering textures
 */
public class TextureRenderHelper {
    private final ResourceLocation texture;
    private final TextureAtlasSprite sprite;
    private final GpuTextureView textureView;
    private final float uScale;
    private final float vScale;
    private int lightLevel = LightTexture.FULL_BRIGHT;
    private float width;
    private float height;
    private float uWidth;
    private float vHeight;
    private float uMin;
    private float vMin;
    private int colour = 0xFFFFFFFF;

    TextureRenderHelper(ResourceLocation texture) {
        this.texture = texture;
        this.sprite = null;
        this.textureView = Minecraft.getInstance().getTextureManager().getTexture(texture).getTextureView();
        this.uScale = 1f / this.textureView.getWidth(0);
        this.vScale = 1f / this.textureView.getHeight(0);
    }

    TextureRenderHelper(TextureAtlasSprite sprite) {
        this.texture = sprite.atlasLocation();
        this.sprite = sprite;
        this.textureView = Minecraft.getInstance().getTextureManager().getTexture(this.texture).getTextureView();
        this.width = this.uWidth = sprite.contents().width();
        this.height = this.vHeight = sprite.contents().height();
        this.uScale = 1f / this.width;
        this.vScale = 1f / this.height;
    }

    public static TextureRenderHelper of(ResourceLocation texture) {
        return new TextureRenderHelper(texture);
    }

    public static TextureRenderHelper ofSprite(ResourceLocation texture) {
        return of(TESRenderUtil.getAtlasSprite(texture));
    }

    public static TextureRenderHelper of(TextureAtlasSprite sprite) {
        return new TextureRenderHelper(sprite);
    }

    public TextureRenderHelper sized(float width, float height) {
        this.width = width;
        this.height = height;

        return this;
    }

    public TextureRenderHelper width(float width) {
        this.width = width;

        return this;
    }

    public TextureRenderHelper height(float height) {
        this.height = height;

        return this;
    }

    public TextureRenderHelper uWidth(float pixels) {
        this.uWidth = pixels;

        return this;
    }

    public TextureRenderHelper uWidth(Float2FloatFunction pixels) {
        this.uWidth = pixels.apply(1 / this.uScale);

        return this;
    }

    public TextureRenderHelper vHeight(float pixels) {
        this.vHeight = pixels;

        return this;
    }

    public TextureRenderHelper vHeight(Float2FloatFunction pixels) {
        this.vHeight = pixels.apply(1 / this.vScale);

        return this;
    }

    public TextureRenderHelper uOffset(float uMin) {
        this.uMin = uMin;

        return this;
    }

    public TextureRenderHelper uOffset(Float2FloatFunction uMin) {
        this.uMin = uMin.apply(1 / this.uScale);

        return this;
    }

    public TextureRenderHelper vOffset(float vMin) {
        this.vMin = vMin;

        return this;
    }

    public TextureRenderHelper vOffset(Float2FloatFunction vMin) {
        this.vMin = vMin.apply(1 / this.vScale);

        return this;
    }

    public TextureRenderHelper lightLevel(int packedLight) {
        this.lightLevel = packedLight;

        return this;
    }

    public TextureRenderHelper colour(int colour) {
        this.colour = colour;

        return this;
    }

    public TextureRenderHelper colour(int red, int green, int blue, int alpha) {
        return colour(ARGB.color(alpha, red, green, blue));
    }

    public TextureRenderHelper colour(float red, float green, float blue, float alpha) {
        return colour(ARGB.colorFromFloat(alpha, red, green, blue));
    }

    public TextureRenderHelper colour(float red, float green, float blue) {
        return colour(red, green, blue, 1f);
    }

    public TextureRenderHelper colour(int red, int green, int blue) {
        return colour(red, green, blue, 255);
    }

    public void render(TESHudRenderContext renderContext, float x, float y) {
        render(renderContext, RenderPipelines.GUI_TEXTURED, RenderType::entityTranslucent, x, y);
    }

    public void render(TESHudRenderContext renderContext, RenderPipeline renderPipeline, Function<ResourceLocation, RenderType> renderTypeFunction, float x, float y) {
        if (renderContext.isInWorld()) {
            renderInWorld(renderTypeFunction, renderContext.getPoseStack(), x, y);
        }
        else {
            renderForHud(renderContext.getGuiGraphics(), renderPipeline, x, y);
        }
    }

    public void renderForHud(GuiGraphics guiGraphics, float x, float y) {
        renderForHud(guiGraphics, RenderPipelines.GUI_TEXTURED, x, y);
    }

    public void renderForHud(GuiGraphics guiGraphics, RenderPipeline renderPipeline, float x, float y) {
        if (ARGB.alpha(this.colour) == 0)
            return;

        final int xMin = Mth.floor(x);
        final int yMin = Mth.floor(y);
        final int xMax = Math.round(xMin + this.width);
        final int yMax = Math.round(yMin + this.height);
        float uMin = this.uMin * this.uScale;
        float uMax = (this.uMin + this.uWidth) * this.uScale;
        float vMin = this.vMin * this.vScale;
        float vMax = (this.vMin + this.vHeight) * this.vScale;

        if (this.sprite != null) {
            uMin = this.sprite.getU(uMin);
            vMin = this.sprite.getV(vMin);
            uMax = this.sprite.getU(uMax);
            vMax = this.sprite.getV(vMax);
        }

        guiGraphics.guiRenderState.submitGuiElement(new BlitRenderState(renderPipeline, TextureSetup.singleTexture(this.textureView), new Matrix3x2f(guiGraphics.pose()),
                                                                        xMin, yMin, xMax, yMax,
                                                                        uMin, uMax, vMin, vMax,
                                                                        this.colour, guiGraphics.scissorStack.peek()));
    }

    public void renderInWorld(PoseStack poseStack, float x, float y) {
        renderInWorld(RenderType::entityTranslucent, poseStack, x, y);
    }

    public void renderInWorld(Function<ResourceLocation, RenderType> renderTypeFunction, PoseStack poseStack, float x, float y) {
        if (ARGB.alpha(this.colour) == 0)
            return;

        final int xMin = Mth.floor(x);
        final int yMin = Mth.floor(y);
        final int xMax = Math.round(xMin + this.width);
        final int yMax = Math.round(yMin + this.height);
        float uMin = this.uMin * this.uScale;
        float uMax = (this.uMin + this.uWidth) * this.uScale;
        float vMin = this.vMin * this.vScale;
        float vMax = (this.vMin + this.vHeight) * this.vScale;

        if (this.sprite != null) {
            uMin = this.sprite.getU(uMin);
            uMax = this.sprite.getU(uMax);
            vMin = this.sprite.getV(vMin);
            vMax = this.sprite.getV(vMax);
        }

        final Matrix4f pose = poseStack.last().pose();
        final VertexConsumer buffer = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(renderTypeFunction.apply(this.texture));

        buffer.addVertex(pose, xMin, yMin, 0).setUv(uMin, vMin).setColor(this.colour).setLight(this.lightLevel).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 1, 0);
        buffer.addVertex(pose, xMin, yMax, 0).setUv(uMin, vMax).setColor(this.colour).setLight(this.lightLevel).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 1, 0);
        buffer.addVertex(pose, xMax, yMax, 0).setUv(uMax, vMax).setColor(this.colour).setLight(this.lightLevel).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 1, 0);
        buffer.addVertex(pose, xMax, yMin, 0).setUv(uMax, vMin).setColor(this.colour).setLight(this.lightLevel).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 1, 0);
    }
}

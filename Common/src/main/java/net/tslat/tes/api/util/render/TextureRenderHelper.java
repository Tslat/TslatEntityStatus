package net.tslat.tes.api.util.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTextureView;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.BlitRenderState;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.tslat.tes.api.object.TESHudRenderContext;
import net.tslat.tes.api.util.TESRenderUtil;
import org.joml.Matrix3x2f;

import java.util.function.Function;

/**
 * Factory-based class used for rendering textures
 */
public class TextureRenderHelper {
    private final Identifier texture;
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

    TextureRenderHelper(Identifier texture) {
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

    public static TextureRenderHelper of(Identifier texture) {
        return new TextureRenderHelper(texture);
    }

    public static TextureRenderHelper ofSprite(Identifier texture) {
        return of(TESRenderUtil.getGuiAtlasSprite(texture));
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
        render(renderContext, RenderPipelines.GUI_TEXTURED, RenderTypes::entityTranslucent, x, y);
    }

    public void render(TESHudRenderContext renderContext, RenderPipeline renderPipeline, Function<Identifier, RenderType> renderTypeFunction, float x, float y) {
        renderContext.forGui(args -> renderForHud(args, renderPipeline, x, y))
                .forInWorld(args -> renderInWorld(args, renderTypeFunction, x, y));
    }

    public void renderForHud(TESHudRenderContext.InGuiArgs args, float x, float y) {
        renderForHud(args, RenderPipelines.GUI_TEXTURED, x, y);
    }

    public void renderForHud(TESHudRenderContext.InGuiArgs args, RenderPipeline renderPipeline, float x, float y) {
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
        final GuiGraphics guiGraphics = args.guiGraphics();

        if (this.sprite != null) {
            uMin = this.sprite.getU(uMin);
            vMin = this.sprite.getV(vMin);
            uMax = this.sprite.getU(uMax);
            vMax = this.sprite.getV(vMax);
        }

        guiGraphics.guiRenderState.submitGuiElement(new BlitRenderState(renderPipeline, TextureSetup.singleTexture(this.textureView, RenderSystem.getSamplerCache().getRepeat(FilterMode.NEAREST)), new Matrix3x2f(guiGraphics.pose()),
                                                                        xMin, yMin, xMax, yMax,
                                                                        uMin, uMax, vMin, vMax,
                                                                        this.colour, guiGraphics.scissorStack.peek()));
    }

    public void renderInWorld(TESHudRenderContext.InWorldArgs args, float x, float y) {
        renderInWorld(args, RenderTypes::entityTranslucent, x, y);
    }

    public void renderInWorld(TESHudRenderContext.InWorldArgs args, Function<Identifier, RenderType> renderTypeFunction, float x, float y) {
        if (ARGB.alpha(this.colour) == 0)
            return;

        final int xMin = Mth.floor(x);
        final int yMin = Mth.floor(y);
        final int xMax = Math.round(xMin + this.width);
        final int yMax = Math.round(yMin + this.height);

        args.renderTasks().submitCustomGeometry(args.poseStack(), renderTypeFunction.apply(this.texture), (pose, vertexConsumer) -> {
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

            vertexConsumer.addVertex(pose, xMin, yMin, 0).setUv(uMin, vMin).setColor(this.colour).setLight(this.lightLevel).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 1, 0);
            vertexConsumer.addVertex(pose, xMin, yMax, 0).setUv(uMin, vMax).setColor(this.colour).setLight(this.lightLevel).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 1, 0);
            vertexConsumer.addVertex(pose, xMax, yMax, 0).setUv(uMax, vMax).setColor(this.colour).setLight(this.lightLevel).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 1, 0);
            vertexConsumer.addVertex(pose, xMax, yMin, 0).setUv(uMax, vMin).setColor(this.colour).setLight(this.lightLevel).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 1, 0);
        });
    }
}

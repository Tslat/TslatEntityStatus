package net.tslat.tes.api.util.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.tslat.tes.api.object.TESHudRenderContext;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import org.joml.Matrix4f;

/**
 * Factory-based class used for rendering coloured regions
 */
public class ColouredRectRenderHelper {
    private final RenderPipeline renderPipeline;
    private float width = 16f;
    private float height = 16f;
    private int colour0 = 0xFFFFFFFF;
    private int colour1 = 0xFFFFFFFF;
    private int colour2 = 0xFFFFFFFF;
    private int colour3 = 0xFFFFFFFF;

    ColouredRectRenderHelper(RenderPipeline renderPipeline) {
        this.renderPipeline = renderPipeline;
    }

    public static ColouredRectRenderHelper of() {
        return of(RenderPipelines.GUI);
    }

    public static ColouredRectRenderHelper of(RenderPipeline renderPipeline) {
        return new ColouredRectRenderHelper(renderPipeline);
    }

    public ColouredRectRenderHelper sized(float width, float height) {
        this.width = width;
        this.height = height;

        return this;
    }

    public ColouredRectRenderHelper colour(int colour) {
        this.colour0 = colour;

        if (this.colour1 == 0xFFFFFFFF)
            this.colour1 = colour;

        if (this.colour2 == 0xFFFFFFFF)
            this.colour2 = colour;

        if (this.colour3 == 0xFFFFFFFF)
            this.colour3 = colour;

        return this;
    }

    public ColouredRectRenderHelper bottomLeftColour(int colour) {
        this.colour1 = colour;

        return this;
    }

    public ColouredRectRenderHelper bottomRightColour(int colour) {
        this.colour2 = colour;

        return this;
    }

    public ColouredRectRenderHelper topRightColour(int colour) {
        this.colour3 = colour;

        return this;
    }

    public ColouredRectRenderHelper colour(int red, int green, int blue, int alpha) {
        return colour(ARGB.color(alpha, red, green, blue));
    }

    public ColouredRectRenderHelper colour(float red, float green, float blue, float alpha) {
        return colour(ARGB.colorFromFloat(alpha, red, green, blue));
    }

    public ColouredRectRenderHelper colour(float red, float green, float blue) {
        return colour(red, green, blue, 1f);
    }

    public ColouredRectRenderHelper colour(int red, int green, int blue) {
        return colour(red, green, blue, 255);
    }

    public void render(TESHudRenderContext renderContext, float x, float y) {
        if (renderContext.isInWorld()) {
            renderInWorld(renderContext.getPoseStack(), x, y);
        }
        else {
            renderForHud(renderContext.getGuiGraphics(), x, y);
        }
    }

    public void renderForHud(GuiGraphics guiGraphics, float x, float y) {
        if (ARGB.alpha(this.colour0) == 0 && ARGB.alpha(this.colour1) == 0 && ARGB.alpha(this.colour2) == 0 && ARGB.alpha(this.colour3) == 0)
            return;

        final int xMin = Mth.floor(x);
        final int yMin = Mth.floor(y);
        final int xMax = Math.round(xMin + this.width);
        final int yMax = Math.round(yMin + this.height);

        guiGraphics.guiRenderState.submitGuiElement(new RenderState(this.renderPipeline, TextureSetup.noTexture(), new Matrix3x2f(guiGraphics.pose()), xMin, yMin, xMax, yMax,
                                                                    this.colour0, this.colour1, this.colour2, this.colour3, guiGraphics.scissorStack.peek()));
    }

    public void renderInWorld(PoseStack poseStack, float x, float y) {
        if (ARGB.alpha(this.colour0) == 0 && ARGB.alpha(this.colour1) == 0 && ARGB.alpha(this.colour2) == 0 && ARGB.alpha(this.colour3) == 0)
            return;

        final int xMin = Mth.floor(x);
        final int yMin = Mth.floor(y);
        final int xMax = Math.round(xMin + this.width);
        final int yMax = Math.round(yMin + this.height);
        final Matrix4f pose = poseStack.last().pose();
        final VertexConsumer buffer = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.textBackgroundSeeThrough());

        buffer.addVertex(pose, xMin, yMin, 0).setColor(this.colour0);
        buffer.addVertex(pose, xMin, yMax, 0).setColor(this.colour1);
        buffer.addVertex(pose, xMax, yMax, 0).setColor(this.colour2);
        buffer.addVertex(pose, xMax, yMin, 0).setColor(this.colour3);
    }

    record RenderState(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose, int x0, int y0, int x1, int y1, int colour1, int colour2, int colour3, int colour4,
                       @Nullable ScreenRectangle scissorArea, @Nullable ScreenRectangle bounds) implements GuiElementRenderState {
        RenderState(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose, int x0, int y0, int x1, int y1, int colour1, int colour2, int colour3, int colour4,
                    @Nullable ScreenRectangle scissorRegion) {
            this(pipeline, textureSetup, pose, x0, y0, x1, y1, colour1, colour2, colour3, colour4, scissorRegion, getBounds(x0, y0, x1, y1, pose, scissorRegion));
        }

        @Override
        public void buildVertices(VertexConsumer vertexConsumer) {
            vertexConsumer.addVertexWith2DPose(this.pose(), (float)this.x0(), (float)this.y0()).setColor(this.colour1);
            vertexConsumer.addVertexWith2DPose(this.pose(), (float)this.x0(), (float)this.y1()).setColor(this.colour2);
            vertexConsumer.addVertexWith2DPose(this.pose(), (float)this.x1(), (float)this.y1()).setColor(this.colour3);
            vertexConsumer.addVertexWith2DPose(this.pose(), (float)this.x1(), (float)this.y0()).setColor(this.colour4);
        }

        @Nullable
        private static ScreenRectangle getBounds(int x0, int y0, int x1, int y1, Matrix3x2f pose, @Nullable ScreenRectangle scissorRegion) {
            ScreenRectangle rect = new ScreenRectangle(x0, y0, x1 - x0, y1 - y0).transformMaxBounds(pose);

            return scissorRegion != null ? scissorRegion.intersection(rect) : rect;
        }
    }
}
package net.tslat.tes.api.object;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import org.apache.commons.lang3.function.TriConsumer;
import org.joml.Matrix3x2fStack;

import java.util.function.Consumer;

/**
 * Container class for rendering elements that may render in either a GUI context or in-world context.
 * <p>
 * To use, chain calls of {@link #forGui(Consumer)} and {@link #forInWorld(TriConsumer)} to render in the appropriate context.
 */
public record TESHudRenderContext(Either<GuiGraphics, InWorldArgs> args) {
    /**
     * Create a new RenderContext for GUI rendering.
     */
    public static TESHudRenderContext guiContext(GuiGraphics guiGraphics) {
        return new TESHudRenderContext(Either.left(guiGraphics));
    }

    /**
     * Create a new RenderContext for in-world rendering.
     */
    public static TESHudRenderContext inWorldContext(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, int packedLight) {
        return new TESHudRenderContext(Either.right(new InWorldArgs(poseStack, bufferSource, packedLight)));
    }

    public record InWorldArgs(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, int packedLight) {}

    /**
     * Returns whether this render context is for in-world rendering.
     */
    public boolean isInWorld() {
        return this.args.right().isPresent();
    }

    /**
     * Apply the given consumer if the current render context is GUI
     */
    public TESHudRenderContext forGui(Consumer<GuiGraphics> guiConsumer) {
        this.args.ifLeft(guiConsumer);

        return this;
    }

    /**
     * Apply the given consumer if the current render context is in-world
     */
    public TESHudRenderContext forInWorld(TriConsumer<PoseStack, MultiBufferSource.BufferSource, Integer> inWorldConsumer) {
        this.args.ifRight(inWorldArgs -> inWorldConsumer.accept(inWorldArgs.poseStack, inWorldArgs.bufferSource, inWorldArgs.packedLight));

        return this;
    }

    /**
     * Push the current matrix pose onto the stack, regardless of the current render context.
     */
    public void pushMatrix() {
        if (this.isInWorld()) {
            this.args.right().get().poseStack.pushPose();
        }
        else {
            this.args().left().get().pose().pushMatrix();
        }
    }

    /**
     * Pop the current matrix pose from the stack, regardless of the current render context.
     */
    public void popMatrix() {
        if (this.isInWorld()) {
            this.args.right().get().poseStack.popPose();
        }
        else {
            this.args().left().get().pose().popMatrix();
        }
    }

    /**
     * Perform a matrix translation on the current matrix pose, regardless of the current render context.
     * <p>
     * Z-depth values will be ignored for gui context rendering as the gui handles its own depth values
     *
     * @see PoseStack#translate(float, float, float)
     * @see Matrix3x2fStack#translate(float, float)
     */
    public void translate(float x, float y, float z) {
        if (this.isInWorld()) {
            this.args.right().get().poseStack.translate(x, y, z);
        }
        else {
            this.args().left().get().pose().translate(x, y);
        }
    }

    /**
     * Perform a matrix scale on the current matrix pose, regardless of the current render context.
     * <p>
     * Z-depth values will be ignored for gui context rendering as the gui handles its own depth values
     *
     * @see PoseStack#scale(float, float, float)
     * @see Matrix3x2fStack#scale(float, float)
     */
    public void scale(float x, float y, float z) {
        if (this.isInWorld()) {
            this.args.right().get().poseStack.scale(x, y, z);
        }
        else {
            this.args().left().get().pose().scale(x, y);
        }
    }

    /**
     * Shortcut method to return the GuiGraphics instance this RenderContext holds.
     * <p>
     * <b><u>NOTE: </u></b> This method WILL crash if called outside of a GUI context.
     */
    public GuiGraphics getGuiGraphics() {
        if (isInWorld())
            throw new IllegalStateException("Cannot get GuiGraphics from in-world render context");

        return this.args.left().get();
    }

    /**
     * Shortcut method to return the PoseStack instance this RenderContext holds.
     * <p>
     * <b><u>NOTE: </u></b> This method WILL crash if called inside of a GUI context.
     */
    public PoseStack getPoseStack() {
        if (!isInWorld())
            throw new IllegalStateException("Cannot get PoseStack from in-world render context");

        return this.args.right().get().poseStack;
    }

    /**
     * Shortcut method to return the BufferSource instance this RenderContext holds.
     * <p>
     * <b><u>NOTE: </u></b> This method WILL crash if called inside of a GUI context.
     */
    public MultiBufferSource.BufferSource getBufferSource() {
        if (!isInWorld())
            throw new IllegalStateException("Cannot get BufferSource from in-world render context");

        return this.args.right().get().bufferSource;
    }

    /**
     * Shortcut method to return the packed light value this RenderContext holds.
     * <p>
     * <b><u>NOTE: </u></b> This method WILL crash if called inside of a GUI context.
     */
    public int getPackedLight() {
        if (!isInWorld())
            throw new IllegalStateException("Cannot get BufferSource from in-world render context");

        return this.args.right().get().packedLight;
    }
}
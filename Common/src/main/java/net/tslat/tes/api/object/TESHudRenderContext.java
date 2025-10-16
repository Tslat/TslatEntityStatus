package net.tslat.tes.api.object;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.state.CameraRenderState;
import org.joml.Matrix3x2fStack;

import java.util.function.Consumer;

/**
 * Container class for rendering elements that may render in either a GUI context or in-world context.
 * <p>
 * To use, chain calls of {@link #forGui(Consumer)} and {@link #forInWorld(Consumer)} to render in the appropriate context.
 */
public record TESHudRenderContext(Either<InGuiArgs, InWorldArgs> args) {
    /**
     * Create a new RenderContext for GUI rendering.
     */
    public static TESHudRenderContext guiContext(GuiGraphics guiGraphics, float partialTick) {
        return new TESHudRenderContext(Either.left(new InGuiArgs(guiGraphics, partialTick)));
    }

    /**
     * Create a new RenderContext for in-world rendering.
     */
    public static TESHudRenderContext inWorldContext(PoseStack poseStack, OrderedSubmitNodeCollector renderTasks, CameraRenderState cameraRenderState, float partialTick, int packedLight) {
        return new TESHudRenderContext(Either.right(new InWorldArgs(poseStack, renderTasks, cameraRenderState, partialTick, packedLight)));
    }

    public record InGuiArgs(GuiGraphics guiGraphics, float partialTick) {}

    public record InWorldArgs(PoseStack poseStack, OrderedSubmitNodeCollector renderTasks, CameraRenderState cameraRenderState, float partialTick, int packedLight) {}

    /**
     * Returns whether this render context is for in-world rendering.
     */
    public boolean isInWorld() {
        return this.args.right().isPresent();
    }

    /**
     * Apply the given consumer if the current render context is GUI
     */
    public TESHudRenderContext forGui(Consumer<InGuiArgs> guiConsumer) {
        this.args.ifLeft(guiConsumer);

        return this;
    }

    /**
     * Apply the given consumer if the current render context is in-world
     */
    public TESHudRenderContext forInWorld(Consumer<InWorldArgs> inWorldConsumer) {
        this.args.ifRight(inWorldConsumer);

        return this;
    }

    /**
     * Push the current matrix pose onto the stack, regardless of the current render context.
     */
    public void pushMatrix() {
        this.args.ifLeft(inGuiArgs -> inGuiArgs.guiGraphics.pose().pushMatrix())
                .ifRight(inWorldArgs -> inWorldArgs.poseStack.pushPose());
    }

    /**
     * Pop the current matrix pose from the stack, regardless of the current render context.
     */
    public void popMatrix() {
        this.args.ifLeft(inGuiArgs -> inGuiArgs.guiGraphics.pose().popMatrix())
                .ifRight(inWorldArgs -> inWorldArgs.poseStack.popPose());
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
        this.args.ifLeft(inGuiArgs -> inGuiArgs.guiGraphics.pose().translate(x, y))
                .ifRight(inWorldArgs -> inWorldArgs.poseStack.translate(x, y, z));
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
        this.args.ifLeft(inGuiArgs -> inGuiArgs.guiGraphics.pose().scale(x, y))
                .ifRight(inWorldArgs -> inWorldArgs.poseStack.scale(x, y, z));
    }

    /**
     * Return a copy of this RenderContext with the given render order index for in-world rendering
     * <p>
     * This is really only used for rendering layers on the same element to avoid z-clipping/fighting<br>
     * Does nothing if not in world-context
     * <p>
     * Subsequent <code>withRenderOrder</code> calls cannot be made on the returned object, so ensure this is only being passed to places where
     * only the direct rendering will take place
     *
     * @param index The render index. Smaller numbers render first
     * @see SubmitNodeStorage#order(int)
     */
    public TESHudRenderContext withRenderOrder(int index) {
        if (!isInWorld())
            return this;

        final InWorldArgs args = this.args.right().get();

        if (!(args.renderTasks instanceof SubmitNodeStorage storage))
            return this;

        return TESHudRenderContext.inWorldContext(args.poseStack, storage.order(index), args.cameraRenderState, args.partialTick, args.packedLight);
    }

    /**
     * Returns the partial tick value for the current render context.
     */
    public float getPartialTick() {
        return this.args.map(InGuiArgs::partialTick, InWorldArgs::partialTick);
    }

    /**
     * Shortcut method to return the GuiGraphics instance this RenderContext holds.
     * <p>
     * <b><u>NOTE: </u></b> This method WILL crash if called outside of a GUI context.
     */
    public GuiGraphics getGuiGraphics() {
        if (isInWorld())
            throw new IllegalStateException("Cannot get GuiGraphics from in-world render context");

        return this.args.left().get().guiGraphics();
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
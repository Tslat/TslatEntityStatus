package net.tslat.tes.api.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.data.AtlasIds;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.tslat.tes.api.TESAPI;
import net.tslat.tes.api.TESConfig;
import net.tslat.tes.api.TESTextures;
import net.tslat.tes.api.object.TESHudRenderContext;
import net.tslat.tes.api.util.render.TextureRenderHelper;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.function.BiConsumer;

/**
 * Helper class for various rendering-related tasks.
 */
public final class TESRenderUtil {
    /**
     * Get the TextureAtlasSprite instance for the given texture location
     * <p>
     * <b><u>NOTE:</u></b> Only supports GUI sprites. Other atlases must be retrieved manually
     */
    public static TextureAtlasSprite getGuiAtlasSprite(Identifier texture) {
        return Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.GUI).getSprite(texture);
    }

    /**
     * Draw some text on screen at a given position, offset for the text's height and width
     */
    public static void centerTextForRender(Component text, float x, float y, BiConsumer<Float, Float> renderRunnable) {
        renderRunnable.accept(x - Minecraft.getInstance().font.width(text) / 2f, y + (Minecraft.getInstance().font.lineHeight - 1) / 2f);
    }

    /**
     * Return a colour value ranging from red to green depending on the given value is to its minimum and maximum values
     * <p>
     * Can be used as a visual colour gradiant or grading system
     */
    public static int colourGradeForLerp(double value, double minValue, double maxValue) {
        return colourGradeForValue((value - minValue) / (maxValue - minValue));
    }

    /**
     * Return a colour value ranging from red to green, with 0 being red and 1 being green.
     * <p>
     * Can be used as a visual colour gradiant or grading system
     */
    public static int colourGradeForValue(double value) {
        return Mth.hsvToArgb((float)Mth.clamp(0.35f * value, 0, 0.35f), 1f, 1f, 255);
    }

    /**
     * Translate the given {@link PoseStack} to face the game camera
     */
    public static void positionFacingCamera(PoseStack poseStack) {
        poseStack.scale(-1, -1, -1);
        poseStack.mulPose(Minecraft.getInstance().gameRenderer.getMainCamera().rotation());
        poseStack.mulPose(Axis.YP.rotationDegrees(180));
    }

    /**
     * Render a complete bar, automatically handling all layers and transitions.
     *
     * @param renderContext The render context to render the bar in
     * @param x The x position to render at
     * @param y The y position to render at
     * @param barWidth The render-width of the bar
     * @param completionPercentage The percentage value that the bar is full
     * @param transitionPercentage The percentage value that the bar is full for its transition display (for transitioning between values)
     * @param opacity The opaqueness of the bar
     * @param background The background sprite for the bar
     * @param emptyBar The empty bar sprite for the bar
     * @param filledBar The filled bar sprite for the bar
     * @param overlayBar The overlay notches or detail sprite for the bar, if applicable
     */
    public static void renderBar(TESHudRenderContext renderContext, int x, int y, int barWidth, float completionPercentage, float transitionPercentage, float opacity,
                                 TextureAtlasSprite background, TextureAtlasSprite emptyBar, TextureAtlasSprite filledBar, @Nullable TextureAtlasSprite overlayBar) {
        renderBarLayer(renderContext, x, y, background, barWidth, 1f, opacity);

        renderContext.translate(0, 0, 0.01f);
        renderBarLayer(renderContext.withRenderOrder(1), x, y, emptyBar, barWidth, transitionPercentage, opacity);
        renderContext.translate(0, 0, 0.01f);
        renderBarLayer(renderContext.withRenderOrder(2), x, y, filledBar, barWidth, completionPercentage, opacity);

        if (overlayBar != null) {
            renderContext.translate(0, 0, 0.01f);
            renderBarLayer(renderContext.withRenderOrder(3), x, y, overlayBar, barWidth, 1f, 0.75f * opacity);
        }
    }

    /**
     * Render a single bar layer, splitting it into 3 sections to nicely preserve its edge shapes
     */
    public static void renderBarLayer(TESHudRenderContext renderContext, int x, int y, TextureAtlasSprite sprite, float barWidth, float filledPercent, float opacity) {
        float pixelWidth = sprite.contents().width();
        float barPercent = Math.round(barWidth * filledPercent);
        float pixelPercent = Math.round(pixelWidth * filledPercent);
        float midBarWidth = barWidth - 10;
        float leftEndPixels = Math.min(5, Math.min(barPercent, pixelPercent));
        float rightEndPixels = Math.min(5, 5 - (pixelWidth - pixelPercent));

        TextureRenderHelper barLeft = TextureRenderHelper.of(sprite).uWidth(leftEndPixels).width(leftEndPixels).colour(ARGB.white(opacity));
        TextureRenderHelper barMiddle = pixelWidth <= 10 || midBarWidth <= 0 || barPercent <= 10 ? null :
                                        TextureRenderHelper.of(sprite).uWidth(pixelPercent - 10).uOffset(5).width(barPercent >= barWidth - 5 ? midBarWidth : barPercent - 10).colour(ARGB.white(opacity));
        TextureRenderHelper barRight = rightEndPixels <= 0 || barPercent - 5 <= 0 ? null :
                                       TextureRenderHelper.of(sprite).uWidth(rightEndPixels).uOffset(pixelWidth - 5).width(Math.min(5, rightEndPixels)).colour(ARGB.white(opacity));

        if (renderContext.isInWorld()) {
            int packedLight = renderContext.getPackedLight();

            barLeft.lightLevel(packedLight);

            if (barMiddle != null)
                barMiddle.lightLevel(packedLight);

            if (barRight != null)
                barRight.lightLevel(packedLight);
        }

        barLeft.render(renderContext, RenderPipelines.GUI_TEXTURED, RenderTypes::entityTranslucent, x, y);

        if (barMiddle != null)
            barMiddle.render(renderContext, RenderPipelines.GUI_TEXTURED, RenderTypes::entityTranslucent, x + 5, y);

        if (barRight != null)
            barRight.render(renderContext, RenderPipelines.GUI_TEXTURED, RenderTypes::entityTranslucent, x + 5 + midBarWidth, y);
    }

    /**
     * Render a statically-positioned view of an {@link LivingEntity entity} instance, optionally including the frame TES usually renders with
     */
    public static void renderEntityIcon(TESHudRenderContext.InGuiArgs args, Minecraft mc, LivingEntity entity, float opacity, boolean includeFrame) {
        final TESConfig config = TESAPI.getConfig();
        final float scale = 0.04f * (float)Math.pow(Math.min(30 / entity.getBbWidth(), 40 / entity.getBbHeight()), 0.95f) * -20;
        final boolean scissor = config.hudPreventEntityOverflow();
        final GuiGraphics guiGraphics = args.guiGraphics();

        if (scissor)
            guiGraphics.enableScissor(2, 2, 36, 47);

        if (includeFrame)
            TextureRenderHelper.ofSprite(TESTextures.ENTITY_ICON_FRAME).colour(ARGB.white(0.5f * opacity)).renderForHud(args, 2, 2);

        float yBodyRotPrev = entity.yBodyRot;
        float yRotPrev = entity.getYRot();
        float xRotPrev = entity.getXRot();
        float yHeadRotOldPrev = entity.yHeadRotO;
        float yHeadRotPrev = entity.yHeadRot;
        int hurtTicks = entity.hurtTime;
        float attackTimePrev = entity.attackAnim;
        float attackTimeOldPrev = entity.oAttackAnim;
        Component displayName = entity.getCustomName();

        entity.setYRot(22.5f);
        entity.setXRot(0);
        entity.yBodyRot = 22.5f;
        entity.yHeadRot = entity.getYRot();
        entity.yHeadRotO = entity.getYRot();
        entity.hurtTime = config.hudEntityDamageOverlay() ? entity.hurtTime : 0;
        entity.attackAnim = 0;
        entity.oAttackAnim = 0;
        entity.setCustomName(null);

        EntityRenderState renderState = mc.getEntityRenderDispatcher().getRenderer(entity).createRenderState(entity, 1f);
        renderState.lightCoords = LightTexture.FULL_BRIGHT;

        guiGraphics.submitEntityRenderState(renderState, scale / entity.getScale(), new Vector3f(0, entity.getBbHeight() * -0.5f, 0),
                                            new Quaternionf(), null, 2, 2, 36, 47);

        entity.setCustomName(displayName);
        entity.setYRot(yRotPrev);
        entity.setXRot(xRotPrev);
        entity.yBodyRot = yBodyRotPrev;
        entity.yHeadRot = yHeadRotPrev;
        entity.yHeadRotO = yHeadRotOldPrev;
        entity.hurtTime = hurtTicks;
        entity.attackAnim = attackTimePrev;
        entity.oAttackAnim = attackTimeOldPrev;

        if (scissor)
            guiGraphics.disableScissor();
    }
}

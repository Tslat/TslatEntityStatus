package net.tslat.tslatentitystatus.api.client.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.data.AtlasIds;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Brightness;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.tslat.tslatentitystatus.api.TESConfig;
import net.tslat.tslatentitystatus.api.client.constant.TESTextures;
import net.tslat.tslatentitystatus.api.client.object.TESHudRenderContext;
import net.tslat.tslatentitystatus.api.client.object.renderhelper.TextureRenderHelper;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

import java.util.function.BiConsumer;

/// Helper class for various rendering-related tasks
public final class TESRenderUtil {
    /// Get the global font instance that `Minecraft` uses for rendering
    ///
    /// Note that this may be different within GUIs, and you should attempt to use [Screen#getFont()] when in a screen
    public static Font getFont() {
        return Minecraft.getInstance().font;
    }

    /// Get the [TextureAtlasSprite] instance for the given texture location
    ///
    /// **<u>NOTE:</u>** Only supports GUI sprites. Other atlases must be retrieved manually
    public static TextureAtlasSprite getGuiAtlasSprite(Identifier texture) {
        return Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.GUI).getSprite(texture);
    }

    /// Draw some text on screen at a given position, offset for the text's height and width
    public static void centerTextForRender(Component text, float x, float y, BiConsumer<Float, Float> renderRunnable) {
        renderRunnable.accept(x - getFont().width(text) / 2f, y + (getFont().lineHeight - 1) / 2f);
    }

    /// Return a colour value ranging from red to green depending on the given value is to its minimum and maximum values
    ///
    /// Can be used as a visual colour gradiant or grading system
    public static int colourGradeForLerp(double value, double minValue, double maxValue) {
        return colourGradeForValue((value - minValue) / (maxValue - minValue));
    }

    /// Return a colour value ranging from red to green, with 0 being red and 1 being green
    ///
    /// Can be used as a visual colour gradiant or grading system
    public static int colourGradeForValue(double value) {
        return Mth.hsvToArgb(Mth.clamp(0.35f * (float)value, 0, 0.35f), 1f, 1f, 255);
    }

    /// Translate the given [PoseStack] to face the game camera
    public static void positionFacingCamera(PoseStack poseStack) {
        poseStack.scale(-1, -1, -1);
        poseStack.rotate(Minecraft.getInstance().gameRenderer.mainCamera().rotation());
        poseStack.rotateDegrees(Axis.YP, 180);
    }

    /// Render a complete bar, automatically handling all layers and transitions
    ///
    /// @param renderContext        The render context to render the bar in
    /// @param x                    The x position to render at
    /// @param y                    The y position to render at
    /// @param barWidth             The render-width of the bar
    /// @param completionPercentage The percentage value that the bar is full
    /// @param transitionPercentage The percentage value that the bar is full for its transition display (for transitioning between values)
    /// @param opacity              The opaqueness of the bar
    /// @param background           The background sprite for the bar
    /// @param emptyBar             The empty bar sprite for the bar
    /// @param filledBar            The filled bar sprite for the bar
    /// @param overlayBar           The overlay notches or detail sprite for the bar, if applicable
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

    /// Render a single bar layer, splitting it into 3 sections to nicely preserve its edge shapes
    public static void renderBarLayer(TESHudRenderContext renderContext, int x, int y, TextureAtlasSprite sprite, float barWidth, float filledPercent, float opacity) {
        final float pixelWidth = sprite.contents().width();
        final float barPercent = Math.round(barWidth * filledPercent);
        final float pixelPercent = Math.round(pixelWidth * filledPercent);
        final float midBarWidth = barWidth - 10;
        final float leftEndPixels = Math.min(5, Math.min(barPercent, pixelPercent));
        final float rightEndPixels = Math.min(5, 5 - (pixelWidth - pixelPercent));
        final TextureRenderHelper barLeft = TextureRenderHelper.of(sprite).uWidth(leftEndPixels).width(leftEndPixels).colour(ARGB.white(opacity));
        final TextureRenderHelper barMiddle = pixelWidth <= 10 || midBarWidth <= 0 || barPercent <= 10 ? null :
                                        TextureRenderHelper.of(sprite).uWidth(pixelPercent - 10).uOffset(5).width(barPercent >= barWidth - 5 ? midBarWidth : barPercent - 10).colour(ARGB.white(opacity));
        final TextureRenderHelper barRight = rightEndPixels <= 0 || barPercent - 5 <= 0 ? null :
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

    /// Render a statically-positioned view of a [LivingEntity] instance, optionally including the frame TES usually renders with
    public static void renderEntityIcon(TESHudRenderContext.InGuiArgs args, Minecraft mc, LivingEntity entity, float opacity, boolean includeFrame) {
        final TESConfig config = TESClientUtil.getConfig();
        final float scale = 0.04f * (float)Math.pow(Math.min(30 / entity.getBbWidth(), 40 / entity.getBbHeight()), 0.95f) * 20;
        final boolean scissor = config.hudPreventEntityOverflow();
        final GuiGraphicsExtractor guiGraphics = args.guiGraphics();
        final EntityRenderState renderState = createEntityIconExtract(entity, config);
        final Quaternionf rotation = new Quaternionf().rotateZ(Mth.PI).rotateY(Mth.PI + 45 * Mth.DEG_TO_RAD);

        if (scissor)
            guiGraphics.enableScissor(2, 2, 36, 47);

        if (includeFrame)
            TextureRenderHelper.ofSprite(TESTextures.ENTITY_ICON_FRAME).colour(ARGB.white(0.5f * opacity)).renderForHud(args, 2, 2);

        guiGraphics.entity(renderState, scale / entity.getScale(), new Vector3f(0, renderState.boundingBoxHeight / 2f + 0.0625f, 0),
                                            rotation, null, 2, 2, 36, 47);

        if (scissor)
            guiGraphics.disableScissor();
    }

    private static EntityRenderState createEntityIconExtract(LivingEntity entity, TESConfig config) {
        final EntityRenderState renderState = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entity).createRenderState(entity, 1f);
        renderState.shadowRadius = 0;
        renderState.lightCoords = Brightness.FULL_BRIGHT.pack();
        renderState.shadowPieces.clear();

        if (renderState instanceof LivingEntityRenderState livingEntityRenderState) {
            livingEntityRenderState.deathTime = 0;
            livingEntityRenderState.nameTag = null;
            livingEntityRenderState.xRot = 0;
            livingEntityRenderState.bodyRot = 22.5f;

            if (!config.hudEntityDamageOverlay())
                livingEntityRenderState.hasRedOverlay = false;

            if (renderState instanceof AvatarRenderState avatarRenderState) {
                avatarRenderState.swingAnimation = 0f;
                avatarRenderState.currentSwing = null;
            }
        }

        return renderState;
    }
}

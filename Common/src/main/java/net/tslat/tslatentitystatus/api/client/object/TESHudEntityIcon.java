package net.tslat.tslatentitystatus.api.client.object;

import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.tslat.tslatentitystatus.api.client.constant.TESTextures;
import net.tslat.tslatentitystatus.api.client.object.renderhelper.TextureRenderHelper;
import net.tslat.tslatentitystatus.core.client.hud.BuiltinHudElements;

import java.util.function.Predicate;

/// Interface for rendering entity property icons in [BuiltinHudElements#submitEntityIcons]
///
/// At this stage, the GUI sprite atlas has been bound, and you can safely render icons from that atlas
/// If you bind another texture, ensure you re-bind the [sprite atlas][TESTextures#SPRITES_ATLAS] after rendering
public interface TESHudEntityIcon {
    /// @return `true` if this icon should be rendered for the provided [TESEntityRenderState] for the current render pass
    boolean shouldRender(TESEntityRenderState renderState);

    /// Check and render the icon for this instance
    ///
    /// All icons should be 8x8 in size
    ///
    /// @param renderContext    The renderContext instance for rendering
    /// @param tesRenderState   The TES render state data for this entity for this render pass
    /// @param x                The x position to render at. This should not be adjusted
    /// @param y                The y position to render at. This should not be adjusted
    /// @param opacity          The percentage of opaqueness to render the icon at. 1 is fully opaque
    void render(TESHudRenderContext renderContext, TESEntityRenderState tesRenderState, float x, float y, float opacity);

    /// Builtin renderer for easily rendering an appropriately sized and positioned icon
    static void genericSpriteRender(TESHudRenderContext renderContext, Identifier sprite, float x, float y, float opacity) {
        TextureRenderHelper renderer = TextureRenderHelper.ofSprite(sprite).colour(ARGB.white(opacity));

        if (renderContext.isInWorld())
            renderer.lightLevel(renderContext.getPackedLight());

        renderer.render(renderContext, x, y);
    }

    /// Quickly create a `TESHudEntityIcon` instance with a generic rendering implementation
    static TESHudEntityIcon makeGeneric(Identifier sprite, Predicate<TESEntityRenderState> shouldRender) {
        return new TESHudEntityIcon() {
            @Override
            public boolean shouldRender(TESEntityRenderState tesRenderState) {
                return shouldRender.test(tesRenderState);
            }

            @Override
            public void render(TESHudRenderContext renderContext, TESEntityRenderState tesRenderState, float x, float y, float opacity) {
                genericSpriteRender(renderContext, sprite, x, y, opacity);
            }
        };
    }
}

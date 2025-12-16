package net.tslat.tes.core.hud.element;

import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.LivingEntity;
import net.tslat.tes.api.object.TESHudRenderContext;
import net.tslat.tes.api.util.render.TextureRenderHelper;

import java.util.function.Predicate;

/**
 * Interface for rendering entity property icons in {@link BuiltinHudElements#renderEntityIcons}
 * <p>
 * At this stage, the GUI sprite atlas has been bound, and you can safely render icons from that atlas.
 * If you bind another texture, ensure you re-bind the {@link net.tslat.tes.api.TESTextures#SPRITES_ATLAS sprite atlas} after rendering
 */
public interface TESHudEntityIcon {
    /**
     * Return whether this icon should be rendered for the provided entity at the current time.
     */
    boolean shouldRender(LivingEntity entity);

    /**
     * Check and render the icon for this instance.
     * <p>
     * All icons should be 8x8 in size
     *
     * @param renderContext The renderContext instance for rendering
     * @param entity The entity to render the icon for
     * @param x The x position to render at. This should not be adjusted
     * @param y The y position to render at. This should not be adjusted
     * @param opacity The percentage of opaqueness to render the icon at. 1 is fully opaque.
     */
    void render(TESHudRenderContext renderContext, LivingEntity entity, float x, float y, float opacity);

    /**
     * Builtin renderer for easily rendering an appropriately sized and positioned icon
     */
    static void genericSpriteRender(TESHudRenderContext renderContext, Identifier sprite, float x, float y, float opacity) {
        TextureRenderHelper renderer = TextureRenderHelper.ofSprite(sprite).colour(ARGB.white(opacity));

        if (renderContext.isInWorld())
            renderer.lightLevel(renderContext.getPackedLight());

        renderer.render(renderContext, x, y);
    }

    static TESHudEntityIcon makeGeneric(Identifier sprite, Predicate<LivingEntity> shouldRender) {
        return new TESHudEntityIcon() {
            @Override
            public boolean shouldRender(LivingEntity entity) {
                return shouldRender.test(entity);
            }

            @Override
            public void render(TESHudRenderContext renderContext, LivingEntity entity, float x, float y, float opacity) {
                genericSpriteRender(renderContext, sprite, x, y, opacity);
            }
        };
    }
}

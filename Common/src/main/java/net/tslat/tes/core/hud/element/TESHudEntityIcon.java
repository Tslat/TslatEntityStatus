package net.tslat.tes.core.hud.element;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.tslat.tes.api.util.TESClientUtil;

import java.util.function.Predicate;

/**
 * Interface for rendering entity property icons in {@link BuiltinHudElements#renderEntityIcons}
 * <p>
 *     At this stage, the GUI sprite atlas has been bound, and you can safely render icons from that atlas.
 *     If you bind another texture, ensure you re-bind the {@link net.tslat.tes.api.util.TESClientUtil#SPRITES_ATLAS sprite atlas} after rendering
 * </p>
 */
@FunctionalInterface
public interface TESHudEntityIcon {
    /**
     * Check and render the icon for this instance.
     * <p>All icons should be 8x8 in size</p>
     *
     * @param guiGraphics The GuiGraphics instance for rendering
     * @param entity The entity to render the icon for
     * @param x The x position to render at. This should not be adjusted
     * @param y The y position to render at. This should not be adjusted
     * @return Whether the icon was rendered or not
     */
    boolean renderIfApplicable(GuiGraphics guiGraphics, LivingEntity entity, int x, int y);

    /**
     * Builtin renderer for easily rendering an appropriately sized and positioned icon
     */
    static boolean genericSpriteRender(GuiGraphics guiGraphics, ResourceLocation sprite, int x, int y) {
        TESClientUtil.drawSprite(RenderType::guiTextured, guiGraphics, TESClientUtil.getAtlasSprite(sprite), x, y, 8, 8, 0, 0, 8, 8, 8, 8, -1);

        return true;
    }

    static TESHudEntityIcon makeGeneric(ResourceLocation sprite, Predicate<LivingEntity> shouldRender) {
        return ((guiGraphics, entity, x, y) -> {
            if (!shouldRender.test(entity))
                return false;

            return genericSpriteRender(guiGraphics, sprite, x, y);
        });
    }
}

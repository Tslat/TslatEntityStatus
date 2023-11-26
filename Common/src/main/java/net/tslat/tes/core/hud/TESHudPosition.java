package net.tslat.tes.core.hud;

import net.minecraft.client.gui.GuiGraphics;
import net.tslat.tes.api.TESAPI;
import net.tslat.tes.api.TESConfig;

import java.util.function.Consumer;

/**
 * Helper class for positioning the TES HUD at render time based on the user's preferences
 */
public enum TESHudPosition {
    TOP_LEFT(guiGraphics -> {}),
    CENTER_TOP(guiGraphics -> guiGraphics.pose().translate(guiGraphics.guiWidth() / 2f - 21 - TESAPI.getConfig().hudHealthBarLength() / 2f, 0, 0)),
    TOP_RIGHT(guiGraphics -> guiGraphics.pose().translate(guiGraphics.guiWidth() - 42 - TESAPI.getConfig().hudHealthBarLength(), 0, 0)),
    CENTER_RIGHT(guiGraphics -> guiGraphics.pose().translate(guiGraphics.guiWidth() - 42 - TESAPI.getConfig().hudHealthBarLength(), guiGraphics.guiHeight() / 2f - 22.5f, 0)),
    BOTTOM_RIGHT(guiGraphics -> guiGraphics.pose().translate(guiGraphics.guiWidth() - 42 - TESAPI.getConfig().hudHealthBarLength(), guiGraphics.guiHeight() - 50, 0)),
    BOTTOM_LEFT(guiGraphics -> guiGraphics.pose().translate(0, guiGraphics.guiHeight() - 50, 0)),
    CENTER_LEFT(guiGraphics -> guiGraphics.pose().translate(0, guiGraphics.guiHeight() / 2f - 22.5f, 0));

    private final Consumer<GuiGraphics> renderAdjuster;

    TESHudPosition(Consumer<GuiGraphics> renderAdjuster) {
        this.renderAdjuster = renderAdjuster;
    }

    public void adjustRenderForHudPosition(GuiGraphics guiGraphics) {
        final TESConfig config = TESAPI.getConfig();

        this.renderAdjuster.accept(guiGraphics);
        guiGraphics.pose().translate(config.hudPositionLeftAdjust(), config.hudPositionTopAdjust(), 0);
    }
}

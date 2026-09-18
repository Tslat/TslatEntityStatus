package net.tslat.tslatentitystatus.core.client.hud;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.tslat.tslatentitystatus.api.TESConfig;
import net.tslat.tslatentitystatus.api.TESConstants;

import java.util.function.Consumer;

/// Positioning enumerator for the [TESHud], handling the on-screen position of the GUI-HUD
public enum TESHudPosition {
    TOP_LEFT(_ -> {}),
    CENTER_TOP(guiGraphics -> guiGraphics.pose().translate(guiGraphics.guiWidth() / 2f - 21 - TESConstants.getConfig().hudHealthBarLength() / 2f, 0)),
    TOP_RIGHT(guiGraphics -> guiGraphics.pose().translate(guiGraphics.guiWidth() - 42 - TESConstants.getConfig().hudHealthBarLength(), 0)),
    CENTER_RIGHT(guiGraphics -> guiGraphics.pose().translate(guiGraphics.guiWidth() - 42 - TESConstants.getConfig().hudHealthBarLength(), guiGraphics.guiHeight() / 2f - 22.5f)),
    BOTTOM_RIGHT(guiGraphics -> guiGraphics.pose().translate(guiGraphics.guiWidth() - 42 - TESConstants.getConfig().hudHealthBarLength(), guiGraphics.guiHeight() - 50)),
    BOTTOM_LEFT(guiGraphics -> guiGraphics.pose().translate(0, guiGraphics.guiHeight() - 50)),
    CENTER_LEFT(guiGraphics -> guiGraphics.pose().translate(0, guiGraphics.guiHeight() / 2f - 22.5f));

    private final Consumer<GuiGraphicsExtractor> renderAdjuster;

    TESHudPosition(Consumer<GuiGraphicsExtractor> renderAdjuster) {
        this.renderAdjuster = renderAdjuster;
    }

    /// Apply pre-positioning and rendering adjustments for this [TESHudPosition]
    public void adjustRenderForHudPosition(GuiGraphicsExtractor guiGraphics) {
        final TESConfig config = TESConstants.getConfig();

        this.renderAdjuster.accept(guiGraphics);
        guiGraphics.pose().translate(config.hudPositionLeftAdjust(), config.hudPositionTopAdjust());
    }
}

package net.tslat.tslatentitystatus.api.client.constant;

import net.tslat.tslatentitystatus.api.client.object.renderhelper.TextRenderHelper;

/// Side-agnostic enum for various text rendering styles
///
/// Is used by [TextRenderHelper.Style] to determine the style of font rendering
public enum TextRenderStyle {
    NORMAL,
    DROP_SHADOW,
    GLOWING,
    OUTLINED;
}

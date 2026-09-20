package net.tslat.tslatentitystatus.api.client.object.renderhelper;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.font.EmptyArea;
import net.minecraft.client.gui.font.TextRenderable;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.util.Brightness;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.tslat.tslatentitystatus.api.client.constant.TextRenderStyle;
import net.tslat.tslatentitystatus.api.client.object.TESHudRenderContext;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * Factory-based class used for rendering text
 */
@SuppressWarnings("UnusedReturnValue")
public class TextRenderHelper {
    private final Component component;
    private TextRenderStyle style = TextRenderStyle.NORMAL;
    private Font font = Minecraft.getInstance().font;
    private int textColour = -1;
    private int secondaryColour = -1;
    private int packedLight = Brightness.FULL_BRIGHT.pack();
    private boolean centered = false;
    private int wrapWidth = -1;
    private int backdropColour = 0;

    TextRenderHelper(Component component) {
        this.component = component;
    }

    public static TextRenderHelper of(String text) {
        return of(Component.literal(text));
    }

    public static TextRenderHelper of(Component component) {
        return new TextRenderHelper(component);
    }

    public TextRenderHelper style(TextRenderStyle style) {
        this.style = style;

        return this;
    }

    public TextRenderHelper colour(int colour) {
        this.textColour = colour;

        return this;
    }

    public TextRenderHelper centered() {
        return centered(true);
    }

    public TextRenderHelper centered(boolean centered) {
        this.centered = centered;

        return this;
    }

    public TextRenderHelper wrapWidth(int wrapWidth) {
        this.wrapWidth = wrapWidth;

        return this;
    }

    public TextRenderHelper font(Font font) {
        this.font = font;

        return this;
    }

    public TextRenderHelper lightLevel(int packedLight) {
        this.packedLight = packedLight;

        return this;
    }

    public TextRenderHelper withBackdrop() {
        return withBackdrop(Minecraft.getInstance().options.getBackgroundColor(0f));
    }

    public TextRenderHelper withBackdrop(int colour) {
        this.backdropColour = colour;

        return this;
    }

    public TextRenderHelper colour(int red, int green, int blue, int alpha) {
        return colour(ARGB.color(alpha, red, green, blue));
    }

    public TextRenderHelper colour(float red, float green, float blue, float alpha) {
        return colour(ARGB.colorFromFloat(alpha, red, green, blue));
    }

    public TextRenderHelper colour(float red, float green, float blue) {
        return colour(red, green, blue, 1f);
    }

    public TextRenderHelper colour(int red, int green, int blue) {
        return colour(red, green, blue, 255);
    }

    public TextRenderHelper secondaryColour(int colour) {
        this.secondaryColour = colour;

        return this;
    }

    public TextRenderHelper secondaryColour(int red, int green, int blue, int alpha) {
        return secondaryColour(ARGB.color(alpha, red, green, blue));
    }

    public TextRenderHelper secondaryColour(float red, float green, float blue, float alpha) {
        return secondaryColour(ARGB.colorFromFloat(alpha, red, green, blue));
    }

    public TextRenderHelper secondaryColour(float red, float green, float blue) {
        return secondaryColour(red, green, blue, 1f);
    }

    public TextRenderHelper secondaryColour(int red, int green, int blue) {
        return secondaryColour(red, green, blue, 255);
    }

    public void submit(TESHudRenderContext renderContext, float x, float y) {
        renderContext.forGui(args -> hudSubmit(args, x, y))
                .forInWorld(args -> inWorldSubmit(args, x, y));
    }

    public void hudSubmit(TESHudRenderContext.InGuiArgs args, float x, float y) {
        if (ARGB.alpha(this.textColour) == 0 && ARGB.alpha(this.secondaryColour) == 0)
            return;

        final Style style = Style.fromTextRenderStyle(this.style);
        final int shadowColour = this.secondaryColour == -1 ? style.colourFunction.apply(this.textColour) : this.secondaryColour;
        final int stringWidth = this.font.width(this.component);
        final int width = this.wrapWidth == -1 ? stringWidth : Math.min(stringWidth, this.wrapWidth);
        final int posX = Mth.floor(x) - (this.centered ? width / 2 : 0);
        final int posY = Mth.floor(y);
        final GuiGraphicsExtractor guiGraphics = args.guiGraphics();
        final Matrix3x2f pose = new Matrix3x2f(guiGraphics.pose());
        final List<Font.PreparedText> lines = style.hudExtractor.extractHud(this, posX, posY, shadowColour);

        renderHudLines(lines, pose, posX, posY, width, shadowColour, guiGraphics);
    }

    public void inWorldSubmit(TESHudRenderContext.InWorldArgs args, float x, float y) {
        if (ARGB.alpha(this.textColour) == 0 && ARGB.alpha(this.secondaryColour) == 0)
            return;

        final Style style = Style.fromTextRenderStyle(this.style);
        final int shadowColour = this.secondaryColour == -1 ? style.colourFunction.apply(this.textColour) : this.secondaryColour;
        final int stringWidth = this.font.width(this.component);
        final int width = this.wrapWidth == -1 ? stringWidth : Math.min(stringWidth, this.wrapWidth);
        final int posX = Mth.floor(x) - (this.centered ? width / 2 : 0);
        final int posY = Mth.floor(y);

        style.inWorldExtractor.submit(args.renderTasks(), this, args.poseStack(), posX, posY, shadowColour);
    }

    private void renderHudLines(List<Font.PreparedText> args, Matrix3x2f pose, int posX, int posY, int width, int shadowColour, GuiGraphicsExtractor guiGraphics) {
        for (Font.PreparedText text : args) {
            final ScreenRectangle scissor = guiGraphics.scissorStack.peek();
            final GuiTextRenderState renderState = new GuiTextRenderState(text, this.font, this.component, pose, posX, posY, this.textColour, shadowColour, scissor);

            if (this.backdropColour != 0)
                guiGraphics.fill(posX - 1, posY - 1, posX + width + 1, posY + 8, this.backdropColour);

            guiGraphics.guiRenderState.addText(renderState);
        }
    }

    public enum Style {
        NORMAL(     Style::extractHudDefault,       Style::submitDefault,    _ -> 0xFF000000),
        DROP_SHADOW(Style::extractHudDropShadow,    Style::submitDropShadow, colour -> ARGB.scaleRGB(colour, 0.25f)),
        GLOWING(    Style::extractHudOutlined,      Style::submitOutlined,   colour -> ARGB.scaleRGB(colour, 0.5f)),
        OUTLINED(   Style::extractHudOutlined,      Style::submitOutlined,   _ -> 0xFF000000);

        final static float OUTLINE_WEIGHT = 0.6f;
        final HudExtractor hudExtractor;
        final InWorldExtractor inWorldExtractor;
        final Int2IntFunction colourFunction;

        Style(HudExtractor hudExtractor, InWorldExtractor inWorldExtractor, Int2IntFunction defaultShadowColour) {
            this.hudExtractor = hudExtractor;
            this.inWorldExtractor = inWorldExtractor;
            this.colourFunction = defaultShadowColour;
        }

        public static Style fromTextRenderStyle(TextRenderStyle style) {
            return switch (style) {
                case NORMAL -> NORMAL;
                case DROP_SHADOW -> DROP_SHADOW;
                case GLOWING -> GLOWING;
                case OUTLINED -> OUTLINED;
            };
        }

        @FunctionalInterface
        interface HudExtractor {
            List<Font.PreparedText> extractHud(TextRenderHelper renderHelper, float x, float y, int secondaryColour);
        }

        @FunctionalInterface
        interface InWorldExtractor {
            void submit(OrderedSubmitNodeCollector submits, TextRenderHelper renderHelper, PoseStack poseStack, float x, float y, int secondaryColour);
        }

        private static List<Font.PreparedText> extractHudDefault(TextRenderHelper renderHelper, float x, float y, int secondaryColour) {
            return extractHudSingular(renderHelper, x, y, secondaryColour, false);
        }

        private static List<Font.PreparedText> extractHudDropShadow(TextRenderHelper renderHelper, float x, float y, int secondaryColour) {
            return extractHudSingular(renderHelper, x, y, secondaryColour, true);
        }

        private static List<Font.PreparedText> extractHudSingular(TextRenderHelper renderHelper, float x, float y, int secondaryColour, boolean dropShadow) {
            return ObjectArrayList.of(configurablePreparedText(renderHelper.component.getVisualOrderText(), renderHelper.font, x, y, renderHelper.textColour, secondaryColour, renderHelper.backdropColour, dropShadow));
        }

        private static List<Font.PreparedText> extractHudOutlined(TextRenderHelper renderHelper, float x, float y, int secondaryColour) {
            final FormattedCharSequence charSequence = renderHelper.component.getVisualOrderText();
            final Font.PreparedTextBuilder outlineText = configurablePreparedText(null, renderHelper.font, 0, 0, secondaryColour, 0, renderHelper.backdropColour, false);

            for (int xO = -1; xO <= 1; xO++) {
                for (int yO = -1; yO <= 1; yO++) {
                    if (xO != 0 || yO != 0) {
                        final float[] cumulativeXOffset = new float[] {x};
                        final int xOffset = xO;
                        final int yOffset = yO;

                        charSequence.accept((charIndex, style, character) -> {
                            BakedGlyph glyph = renderHelper.font.getGlyph(character, style);
                            float shadowOffset = glyph.info().getShadowOffset() * OUTLINE_WEIGHT;
                            outlineText.x = cumulativeXOffset[0] + xOffset * shadowOffset;
                            outlineText.y = y + yOffset * shadowOffset;
                            cumulativeXOffset[0] += glyph.info().getAdvance(style.isBold());

                            return outlineText.accept(charIndex, style.withColor(renderHelper.backdropColour), character);
                        });
                    }
                }
            }

            final Font.PreparedTextBuilder preppedText = configurablePreparedText(charSequence, renderHelper.font, x, y, renderHelper.textColour, 0, renderHelper.backdropColour, false);

            return ObjectArrayList.of(outlineText, preppedText);
        }

        private static void submitDefault(OrderedSubmitNodeCollector submits, TextRenderHelper renderHelper, PoseStack poseStack, float x, float y, int secondaryColour) {
            submitSingular(submits, renderHelper, poseStack, x, y, secondaryColour, false);
        }

        private static void submitDropShadow(OrderedSubmitNodeCollector submits, TextRenderHelper renderHelper, PoseStack poseStack, float x, float y, int secondaryColour) {
            submitSingular(submits, renderHelper, poseStack, x, y, secondaryColour, true);
        }

        private static void submitSingular(OrderedSubmitNodeCollector submits, TextRenderHelper renderHelper, PoseStack poseStack, float x, float y, int secondaryColour, boolean dropShadow) {
            /*if (ARGB.alpha(renderHelper.backdropColour) != 0) {
                ColouredRectRenderHelper.of()
                        .colour(renderHelper.backdropColour)
                        .renderInWorld();
            }*/

            final CustomRenderSubmitsExtension tesSubmits = submits instanceof SubmitNodeStorage storage ? storage.order(1) : submits;

            tesSubmits.tslatentitystatus$submitCustomWorldspaceText(
                    poseStack.last().pose(),
                    renderHelper.packedLight,
                    ARGB.alpha(renderHelper.textColour) < 255 ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.POLYGON_OFFSET,
                    !dropShadow,
                    _ -> configurablePreparedText(renderHelper.component.getVisualOrderText(), renderHelper.font, x, y, renderHelper.textColour, secondaryColour, renderHelper.backdropColour, dropShadow),
                    Font.PreparedText::visit,
                    (_, context) -> context
            );
        }

        private static void submitOutlined(OrderedSubmitNodeCollector submits, TextRenderHelper renderHelper, PoseStack poseStack, float x, float y, int secondaryColour) {
            final FormattedCharSequence charSequence = renderHelper.component.getVisualOrderText();
            final CustomRenderSubmitsExtension tesSubmits = submits instanceof SubmitNodeStorage storage ? storage.order(1) : submits;

            tesSubmits.tslatentitystatus$submitCustomWorldspaceText(
                    poseStack.last().pose(),
                    renderHelper.packedLight,
                    ARGB.alpha(secondaryColour) < 255 ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.POLYGON_OFFSET,
                    false,
                    _ -> {
                        final Font.PreparedTextBuilder outlineText = configurablePreparedText(null, renderHelper.font, 0, 0, secondaryColour, 0, renderHelper.backdropColour, false);

                        for (int xO = -1; xO <= 1; xO++) {
                            for (int yO = -1; yO <= 1; yO++) {
                                if (xO != 0 || yO != 0) {
                                    final float[] cumulativeXOffset = new float[] {x};
                                    final int xOffset = xO;
                                    final int yOffset = yO;

                                    charSequence.accept((charIndex, style, character) -> {
                                        BakedGlyph glyph = renderHelper.font.getGlyph(character, style);
                                        float shadowOffset = glyph.info().getShadowOffset() * OUTLINE_WEIGHT;
                                        outlineText.x = cumulativeXOffset[0] + xOffset * shadowOffset;
                                        outlineText.y = y + yOffset * shadowOffset;
                                        cumulativeXOffset[0] += glyph.info().getAdvance(style.isBold());

                                        return outlineText.accept(charIndex, style.withColor(renderHelper.backdropColour), character);
                                    });
                                }
                            }
                        }

                        outlineText.discardEffects();

                        return outlineText;
                    },
                    Font.PreparedText::visit,
                    (_, context) -> context
            );
            poseStack.pushPose();
            poseStack.translate(0, 0, 0.006f);
            tesSubmits.tslatentitystatus$submitCustomWorldspaceText(
                    poseStack.last().pose(),
                    renderHelper.packedLight,
                    ARGB.alpha(secondaryColour) < 255 ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.POLYGON_OFFSET,
                    false,
                    _ -> configurablePreparedText(charSequence, renderHelper.font, x, y, renderHelper.textColour, 0, renderHelper.backdropColour, false),
                    Font.PreparedText::visit,
                    (_, context) -> context
            );
            poseStack.popPose();
        }
    }

    static Font.PreparedTextBuilder configurablePreparedText(@Nullable FormattedCharSequence charSequence, Font font, float x, float y, int colour, int shadowColour, int backgroundColour, boolean dropShadow) {
        final var builder = font.new PreparedTextBuilder(x, y, colour, backgroundColour, dropShadow, false) {
            @Override
            public int getShadowColor(net.minecraft.network.chat.Style textStyle, int textColour) {
                if (dropShadow && shadowColour != 0)
                    return shadowColour;

                Integer textStyleColour = textStyle.getShadowColor();

                if (textStyleColour != null) {
                    float textAlpha = ARGB.alphaFloat(textColour);
                    float shadowAlpha = ARGB.alphaFloat(textStyleColour);

                    return textAlpha != 1f ? ARGB.color(ARGB.as8BitChannel(textAlpha * shadowAlpha), textStyleColour) : textStyleColour;
                }

                return 0;
            }

            @Override
            public void visit(Font.GlyphVisitor glyphVisitor) {
                if (ARGB.alpha(this.backgroundColor) != 0)
                    glyphVisitor.acceptEffect(font.provider.effect().createEffect(this.backgroundLeft, this.backgroundTop, this.backgroundRight, this.backgroundBottom - 1,
                                                                                  -0.01f, this.backgroundColor, 0, 0));

                for (TextRenderable.Styled renderable : this.glyphs) {
                    glyphVisitor.acceptGlyph(renderable);
                }

                if (this.effects != null) {
                    for (TextRenderable effect : this.effects) {
                        glyphVisitor.acceptEffect(effect);
                    }
                }

                if (this.emptyAreas != null) {
                    for (EmptyArea emptyArea : this.emptyAreas) {
                        glyphVisitor.acceptEmptyArea(emptyArea);
                    }
                }
            }
        };

        if (charSequence != null)
            charSequence.accept(builder);

        return builder;
    }

    static class GuiTextRenderState extends net.minecraft.client.renderer.state.gui.GuiTextRenderState {
        public GuiTextRenderState(Font.PreparedText preparedText, Font font, Component text, Matrix3x2fc pose,
                                  int x, int y, int colour, int secondaryColour, @Nullable ScreenRectangle scissor) {
            super(font, text.getVisualOrderText(), pose, x, y, colour, secondaryColour, false, false, scissor);

            this.preparedText = preparedText;
        }

        @Override
        public Font.PreparedText ensurePrepared() {
            //noinspection DataFlowIssue
            this.bounds = this.preparedText.bounds();

            if (this.bounds != null) {
                this.bounds = this.bounds.transformMaxBounds(pose);

                if (scissor != null)
                    this.bounds = scissor.intersection(this.bounds);
            }

            return this.preparedText;
        }
    }
}
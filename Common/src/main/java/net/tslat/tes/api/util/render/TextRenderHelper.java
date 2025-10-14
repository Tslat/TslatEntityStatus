package net.tslat.tes.api.util.render;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.font.TextRenderable;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.state.GuiTextRenderState;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.tslat.tes.api.object.TESHudRenderContext;
import net.tslat.tes.api.object.TextRenderStyle;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import org.joml.Matrix4f;

import java.util.List;

/**
 * Factory-based class used for rendering text
 */
public class TextRenderHelper {
    private final Component component;
    private TextRenderStyle style = TextRenderStyle.NORMAL;
    private Font font = Minecraft.getInstance().font;
    private int textColour = -1;
    private int secondaryColour = -1;
    private int packedLight = LightTexture.FULL_BRIGHT;
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

    public void render(TESHudRenderContext renderContext, float x, float y) {
        renderContext.forGui(args -> renderForHud(args, x, y))
                .forInWorld(args -> renderInWorld(args, x, y));
    }

    public void renderForHud(TESHudRenderContext.InGuiArgs args, float x, float y) {
        if (ARGB.alpha(this.textColour) == 0 && ARGB.alpha(this.secondaryColour) == 0)
            return;

        final Style style = Style.fromTextRenderStyle(this.style);
        final int shadowColour = this.secondaryColour == -1 ? style.colourFunction.apply(this.textColour) : this.secondaryColour;
        final int stringWidth = this.font.width(this.component);
        final int width = this.wrapWidth == -1 ? stringWidth : Math.min(stringWidth, this.wrapWidth);
        final int posX = Mth.floor(x) - (this.centered ? width / 2 : 0);
        final int posY = Mth.floor(y);
        final FormattedCharSequence charSequence = this.component.getVisualOrderText();
        final GuiGraphics guiGraphics = args.guiGraphics();
        final Matrix3x2f pose = new Matrix3x2f(guiGraphics.pose());

        for (Pair<Font.PreparedText, @Nullable ScreenRectangle> text : style.renderFunction.prepare(this.font, charSequence, posX, posY, this.textColour, shadowColour, this.backdropColour,
                                                                                                         this.packedLight, Either.left(pose), guiGraphics.scissorStack.peek(), null)) {
            RenderState renderState = new RenderState(text.getFirst(), this.font, charSequence, pose, posX, posY, this.textColour, shadowColour, guiGraphics.scissorStack.peek());

            renderState.bounds = text.getSecond();

            guiGraphics.guiRenderState.submitText(renderState);
        }
    }

    public void renderInWorld(TESHudRenderContext.InWorldArgs args, float x, float y) {
        if (ARGB.alpha(this.textColour) == 0 && ARGB.alpha(this.secondaryColour) == 0)
            return;

        final Style style = Style.fromTextRenderStyle(this.style);
        final int shadowColour = this.secondaryColour == -1 ? style.colourFunction.apply(this.textColour) : this.secondaryColour;
        final int stringWidth = this.font.width(this.component);
        final int width = this.wrapWidth == -1 ? stringWidth : Math.min(stringWidth, this.wrapWidth);
        final int posX = Mth.floor(x) - (this.centered ? width / 2 : 0);
        final int posY = Mth.floor(y);
        final FormattedCharSequence charSequence = this.component.getVisualOrderText();

        args.renderTasks().submitCustomGeometry(args.poseStack(), RenderType.textBackgroundSeeThrough(), (pose, vertexConsumer) -> {
            style.renderFunction.prepare(this.font, charSequence, posX, posY, this.textColour, shadowColour, this.backdropColour,
                                         this.packedLight, Either.right(pose.pose()), null, Minecraft.getInstance().renderBuffers().bufferSource());
        });
    }

    public enum Style {
        NORMAL(Style::renderDefault, colour -> 0xFF000000),
        DROP_SHADOW(Style::renderDropShadow, colour -> ARGB.scaleRGB(colour, 0.25f)),
        GLOWING(Style::renderOutlined, colour -> ARGB.scaleRGB(colour, 0.5f)),
        OUTLINED(Style::renderOutlined, colour -> 0xFF000000);

        final RenderFunction renderFunction;
        final Int2IntFunction colourFunction;

        Style(RenderFunction renderFunction, Int2IntFunction defaultShadowColour) {
            this.renderFunction = renderFunction;
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
        interface RenderFunction {
            List<Pair<Font.PreparedText, @Nullable ScreenRectangle>> prepare(Font font, FormattedCharSequence charSequence, float x, float y,
                                                                             int colour, int secondaryColour, int backgroundColour, int packedLight,
                                                                             Either<Matrix3x2f, Matrix4f> pose, @Nullable ScreenRectangle scissor, @Nullable MultiBufferSource.BufferSource bufferSource);
        }

        private static List<Pair<Font.PreparedText, @Nullable ScreenRectangle>> renderDefault(Font font, FormattedCharSequence charSequence, float x, float y,
                                                                                              int colour, int secondaryColour, int backgroundColour, int packedLight,
                                                                                              Either<Matrix3x2f, Matrix4f> pose, @Nullable ScreenRectangle scissor, @Nullable MultiBufferSource.BufferSource bufferSource) {
            return renderDefault(font, charSequence, x, y, colour, secondaryColour, backgroundColour, packedLight, false, pose, scissor, bufferSource);
        }

        private static List<Pair<Font.PreparedText, @Nullable ScreenRectangle>> renderDropShadow(Font font, FormattedCharSequence charSequence, float x, float y,
                                                                                                 int colour, int secondaryColour, int backgroundColour, int packedLight,
                                                                                                 Either<Matrix3x2f, Matrix4f> pose, @Nullable ScreenRectangle scissor, @Nullable MultiBufferSource.BufferSource bufferSource) {
            return renderDefault(font, charSequence, x, y, colour, secondaryColour, backgroundColour, packedLight, true, pose, scissor, bufferSource);
        }

        private static List<Pair<Font.PreparedText, @Nullable ScreenRectangle>> renderDefault(Font font, FormattedCharSequence charSequence, float x, float y,
                                                                                              int colour, int secondaryColour, int backgroundColour, int packedLight, boolean dropShadow,
                                                                                              Either<Matrix3x2f, Matrix4f> pose, @Nullable ScreenRectangle scissor, @Nullable MultiBufferSource.BufferSource bufferSource) {
            Font.PreparedTextBuilder preparedText = configurablePreparedText(font, x, y, colour, secondaryColour, backgroundColour, dropShadow);
            charSequence.accept(preparedText);

            return Either.unwrap(pose.mapBoth(pose2d -> {
                ScreenRectangle region = preparedText.bounds();

                if (region != null)
                    region = region.transformMaxBounds(pose2d);

                return List.of(Pair.of(preparedText, scissor != null && region != null ? scissor.intersection(region) : null));
            }, pose3d -> {
                preparedText.visit(getWorldspaceGlyphVisitor(bufferSource, pose3d, Font.DisplayMode.POLYGON_OFFSET, packedLight, dropShadow));

                return List.of();
            }));
        }

        private static List<Pair<Font.PreparedText, @Nullable ScreenRectangle>> renderOutlined(Font font, FormattedCharSequence charSequence, float x, float y,
                                                                                               int colour, int secondaryColour, int backgroundColour, int packedLight,
                                                                                               Either<Matrix3x2f, Matrix4f> pose, @Nullable ScreenRectangle scissor, @Nullable MultiBufferSource.BufferSource bufferSource) {
            final Font.PreparedTextBuilder outlineText = configurablePreparedText(font, 0, 0, secondaryColour, 0, backgroundColour, false);
            final float outlineWeight = 0.6f;

            for (int xO = -1; xO <= 1; xO++) {
                for (int yO = -1; yO <= 1; yO++) {
                    if (xO != 0 || yO != 0) {
                        float[] cumulativeXOffset = new float[] {x};
                        int xOffset = xO;
                        int yOffset = yO;

                        charSequence.accept((charIndex, style, character) -> {
                            BakedGlyph glyph = font.getGlyph(character, style);
                            float shadowOffset = glyph.info().getShadowOffset() * outlineWeight;
                            outlineText.x = cumulativeXOffset[0] + xOffset * shadowOffset;
                            outlineText.y = y + yOffset * shadowOffset;
                            cumulativeXOffset[0] += glyph.info().getAdvance(style.isBold());

                            return outlineText.accept(charIndex, style.withColor(backgroundColour), character);
                        });
                    }
                }
            }

            Font.PreparedTextBuilder text = configurablePreparedText(font, x, y, colour, 0, backgroundColour, false);

            return Either.unwrap(pose.mapBoth(pose2d -> {
                charSequence.accept(text);

                ScreenRectangle region = outlineText.bounds();
                ScreenRectangle bounds = null;
                ScreenRectangle bounds2 = null;

                if (region != null) {
                    region = region.transformMaxBounds(pose2d);
                    bounds = scissor != null ? scissor.intersection(region) : region;
                }

                region = text.bounds();

                if (region != null) {
                    region = region.transformMaxBounds(pose2d);
                    bounds2 = scissor != null ? scissor.intersection(region) : region;
                }

                return List.of(Pair.of(outlineText, bounds), Pair.of(text, bounds2));
            }, pose3d -> {
                Font.GlyphVisitor glyphVisitor = getWorldspaceGlyphVisitor(bufferSource, pose3d, Font.DisplayMode.NORMAL, packedLight, false);

                for (TextRenderable glyph : outlineText.glyphs) {
                    glyphVisitor.acceptGlyph(glyph);
                }

                charSequence.accept(text);
                text.visit(getWorldspaceGlyphVisitor(bufferSource, pose3d, Font.DisplayMode.POLYGON_OFFSET, packedLight, false));

                return List.of();
            }));
        }
    }

    static class RenderState extends GuiTextRenderState {
        public RenderState(Font.PreparedText preparedText, Font font, FormattedCharSequence charSequence, Matrix3x2f pose,
                           int x, int y, int colour, int secondaryColour, @Nullable ScreenRectangle scissor) {
            super(font, charSequence, pose, x, y, colour, secondaryColour, false, scissor);

            this.preparedText = preparedText;
        }

        @Override
        public Font.PreparedText ensurePrepared() {
            return this.preparedText;
        }
    }

    static Font.PreparedTextBuilder configurablePreparedText(Font font, float x, float y, int colour, int shadowColour, int backgroundColour, boolean dropShadow) {
        return font.new PreparedTextBuilder(x, y, colour, backgroundColour, dropShadow) {
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
                if (ARGB.alpha(this.backgroundColor) != 0) {
                    glyphVisitor.acceptEffect(font.provider.effect().createEffect(this.backgroundLeft, this.backgroundTop, this.backgroundRight, this.backgroundBottom, -0.01f, this.backgroundColor, 0, 0));
                }

                for (TextRenderable renderable : this.glyphs) {
                    glyphVisitor.acceptGlyph(renderable);
                }

                if (this.effects != null) {
                    for (TextRenderable effect : this.effects) {
                        glyphVisitor.acceptEffect(effect);
                    }
                }
            }
        };
    }

    static Font.GlyphVisitor getWorldspaceGlyphVisitor(MultiBufferSource bufferSource, Matrix4f pose, Font.DisplayMode displayMode, int packedLight, boolean dropShadow) {
        if (true)
        return Font.GlyphVisitor.forMultiBufferSource(bufferSource, pose, displayMode, packedLight);


        return new Font.GlyphVisitor() {
            @Override
            public void acceptGlyph(TextRenderable renderable) {
                /*VertexConsumer vertexConsumer = bufferSource.getBuffer(renderable.renderType(displayMode));

                renderDropShadowFriendlyGlyph(renderable, vertexConsumer, pose, packedLight, dropShadow);*/
            }

            @Override
            public void acceptEffect(TextRenderable renderable) {
                VertexConsumer vertexConsumer = bufferSource.getBuffer(renderable.renderType(displayMode));

                renderable.render(pose, vertexConsumer, packedLight, dropShadow);
            }

            // Because Mojang didn't build glyph shadows for 3d worldspace
            private static void renderDropShadowFriendlyGlyph(TextRenderable glyphInstance, VertexConsumer buffer, Matrix4f pose, int packedLight, boolean dropShadow) {/*
                net.minecraft.network.chat.Style style = glyphInstance.style();
                boolean italic = style.isItalic();
                boolean bold = style.isBold();
                float x = glyphInstance.x();
                float y = glyphInstance.y();
                int colour = glyphInstance.color();
                float dropShadowOffset = dropShadow ? 0.001f : 0;
                float zDepth;

                if (glyphInstance.hasShadow()) {
                    int shadowColour = glyphInstance.shadowColor();

                    glyph.render(italic, x + glyphInstance.shadowOffset(), y + glyphInstance.shadowOffset(), 0.0F, pose, buffer, shadowColour, bold, packedLight);

                    if (bold)
                        glyph.render(italic, x + glyphInstance.boldOffset() + glyphInstance.shadowOffset(), y + glyphInstance.shadowOffset(), dropShadowOffset, pose, buffer, shadowColour, true, packedLight);

                    zDepth = dropShadow ? 0.006f : 0;
                }
                else {
                    zDepth = 0;
                }

                glyph.render(italic, x, y, zDepth, pose, buffer, colour, bold, packedLight);

                if (bold)
                    glyph.render(italic, x + glyphInstance.boldOffset(), y, zDepth + dropShadowOffset, pose, buffer, colour, true, packedLight);*/
            }
        };
    }
}
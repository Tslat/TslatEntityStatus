package net.tslat.tslatentitystatus.api.client.object.renderhelper;

import net.minecraft.client.gui.Font;
import org.joml.Matrix4fc;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/// Duck-interface for extending the render submit system for custom submit types
public interface CustomRenderSubmitsExtension {
    default void tslatentitystatus$submitCustomWorldspaceText(Matrix4fc pose, int packedLight, Font.DisplayMode displayMode, boolean skipDepth,
                                                              Function<Font, Font.PreparedText> textBuilder,
                                                              BiConsumer<Font.PreparedText, WorldspaceTextFeatureRenderer.GlyphRenderContext> renderer,
                                                              BiFunction<WorldspaceTextFeatureRenderer, WorldspaceTextFeatureRenderer.GlyphRenderContext, WorldspaceTextFeatureRenderer.GlyphRenderContext> contextProvider) {}
}

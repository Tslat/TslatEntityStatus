package net.tslat.tslatentitystatus.api.client.object.renderhelper;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.TextRenderable;
import net.minecraft.client.renderer.feature.FeatureFrameContext;
import net.minecraft.client.renderer.feature.FeatureRendererType;
import net.minecraft.client.renderer.feature.RenderTypeFeatureRenderer;
import net.minecraft.client.renderer.feature.submit.TranslucentSubmit;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/// Fully customiseable text renderer for in-world text rendering
///
/// Vanilla only really supports their built-in options, and anything outside of that is near-impossible for worldspace rendering
/// Custom GUI text rendering is mostly possible through custom [Font.PreparedText] and [Font.GlyphVisitor] instances, but the need for a [VertexConsumer] makes it difficult with the batch system
public class WorldspaceTextFeatureRenderer extends RenderTypeFeatureRenderer<WorldspaceTextFeatureRenderer.Submit> {
    public static final FeatureRendererType<Submit> TYPE = FeatureRendererType.create("Worldspace Text");

    @Override
    protected void buildGroup(final FeatureFrameContext context, final List<Submit> submits) {
        final GlyphRenderContext glyphRenderer = new GlyphRenderContext();

        for (Submit submit : submits) {
            final Font.PreparedText text = submit.textBuilder.apply(context.font());
            final GlyphRenderContext submitRenderer = submit.contextProvider.apply(this, glyphRenderer);

            submit.renderer.accept(text, submitRenderer.prepare(submit));
        }
    }

    public class GlyphRenderContext implements Font.GlyphVisitor {
        public final Matrix4f pose = new Matrix4f();

        public @Nullable Submit submit = null;

        public GlyphRenderContext prepare(Submit submit) {
            this.pose.set(submit.pose);
            this.submit = submit;

            return this;
        }

        @Override
        public void acceptEffect(TextRenderable effect) {
            if (this.submit != null) {
                final VertexConsumer buffer = getVertexBuilder(effect.renderType(this.submit.displayMode));

                effect.render(this.pose, buffer, this.submit.packedLight, this.submit.hasDepth);
            }
        }

        @Override
        public void acceptRenderable(TextRenderable renderable) {
            if (this.submit != null) {
                final VertexConsumer builder = getVertexBuilder(renderable.renderType(this.submit.displayMode));

                renderable.render(this.pose, builder, this.submit.packedLight, this.submit.hasDepth);
            }
        }
    }

    public record Submit(Matrix4fc pose, int packedLight, Font.DisplayMode displayMode, boolean hasDepth,
                         Function<Font, Font.PreparedText> textBuilder,
                         BiConsumer<Font.PreparedText, GlyphRenderContext> renderer,
                         BiFunction<WorldspaceTextFeatureRenderer, GlyphRenderContext, GlyphRenderContext> contextProvider)
            implements TranslucentSubmit {
        @Override
        public float distanceToCameraSq() {
            return TranslucentSubmit.computeDistanceToCameraSq(this.pose);
        }

        @Override
        public FeatureRendererType<Submit> featureType() {
            return WorldspaceTextFeatureRenderer.TYPE;
        }
    }
}

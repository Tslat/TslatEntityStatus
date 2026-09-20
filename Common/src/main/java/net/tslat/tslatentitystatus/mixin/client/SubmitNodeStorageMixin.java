package net.tslat.tslatentitystatus.mixin.client;

import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.tslat.tslatentitystatus.api.client.object.renderhelper.CustomRenderSubmitsExtension;
import net.tslat.tslatentitystatus.api.client.object.renderhelper.WorldspaceTextFeatureRenderer;
import org.joml.Matrix4fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

@Mixin(SubmitNodeStorage.class)
public abstract class SubmitNodeStorageMixin implements CustomRenderSubmitsExtension {
    @Shadow
    public abstract OrderedSubmitNodeCollector order(int par1);

    @Override
    public void tslatentitystatus$submitCustomWorldspaceText(Matrix4fc pose, int packedLight, Font.DisplayMode displayMode, boolean skipDepth,
                                                             Function<Font, Font.PreparedText> textBuilder,
                                                             BiConsumer<Font.PreparedText, WorldspaceTextFeatureRenderer.GlyphRenderContext> renderer,
                                                             BiFunction<WorldspaceTextFeatureRenderer, WorldspaceTextFeatureRenderer.GlyphRenderContext, WorldspaceTextFeatureRenderer.GlyphRenderContext> contextProvider) {
        order(0).tslatentitystatus$submitCustomWorldspaceText(pose, packedLight, displayMode, skipDepth, textBuilder, renderer, contextProvider);
    }
}

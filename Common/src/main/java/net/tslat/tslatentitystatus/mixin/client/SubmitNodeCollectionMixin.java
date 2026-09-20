package net.tslat.tslatentitystatus.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.feature.phase.FeatureRenderPhase;
import net.minecraft.client.renderer.feature.phase.TranslucentFeatureRenderPhase;
import net.tslat.tslatentitystatus.api.client.object.renderhelper.CustomRenderSubmitsExtension;
import net.tslat.tslatentitystatus.api.client.object.renderhelper.SubmitNodeCollectionPhases;
import net.tslat.tslatentitystatus.api.client.object.renderhelper.WorldspaceTextFeatureRenderer;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

@Mixin(SubmitNodeCollection.class)
public abstract class SubmitNodeCollectionMixin implements CustomRenderSubmitsExtension, SubmitNodeCollectionPhases {
    @Unique
    public final TranslucentFeatureRenderPhase tslatentitystatus$customTexts = new TranslucentFeatureRenderPhase();

    @SuppressWarnings({"unchecked", "rawtypes"})
    @WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/util/List;of([Ljava/lang/Object;)Ljava/util/List;"))
    public <E extends FeatureRenderPhase<?>> List<E> tslatentitystatus$wrapPhaseList(Object[] elements, Operation<List<E>> original) {
        // calling with the original elements list is broken in mixinextras
        final List<E> baseList = (List)List.of(elements);
        final List<E> newList = new ObjectArrayList<>(baseList);

        newList.add((E)this.tslatentitystatus$customTexts);

        return (List)List.of(newList.toArray(new Object[0]));
    }

    @Unique
    @Override
    public TranslucentFeatureRenderPhase tslatentitystatus$getCustomTexts() {
        return this.tslatentitystatus$customTexts;
    }

    @Override
    public void tslatentitystatus$submitCustomWorldspaceText(Matrix4fc pose, int packedLight, Font.DisplayMode displayMode, boolean skipDepth,
                                                             Function<Font, Font.PreparedText> textBuilder,
                                                             BiConsumer<Font.PreparedText, WorldspaceTextFeatureRenderer.GlyphRenderContext> renderer,
                                                             BiFunction<WorldspaceTextFeatureRenderer, WorldspaceTextFeatureRenderer.GlyphRenderContext, WorldspaceTextFeatureRenderer.GlyphRenderContext> contextProvider) {
        this.tslatentitystatus$customTexts.submit(new WorldspaceTextFeatureRenderer.Submit(new Matrix4f(pose), packedLight, displayMode, skipDepth, textBuilder, renderer, contextProvider));
    }
}

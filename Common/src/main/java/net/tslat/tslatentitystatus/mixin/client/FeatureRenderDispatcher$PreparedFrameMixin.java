package net.tslat.tslatentitystatus.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.feature.FeatureFrameContext;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.feature.phase.FeatureRenderPhase;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Iterator;

@Mixin(FeatureRenderDispatcher.PreparedFrame.class)
public abstract class FeatureRenderDispatcher$PreparedFrameMixin {
    @Shadow
    protected abstract void executePhase(FeatureRenderPhase<?> phase, FeatureFrameContext context);

    @Shadow
    private @Nullable FeatureFrameContext context;

    @WrapOperation(
            method = "executeTranslucent",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Iterator;next()Ljava/lang/Object;")
    )
    private <E> E tslatentitystatus$captureCollection(Iterator<E> instance, Operation<E> original, @Share("tes$capturedCollection") LocalRef<SubmitNodeCollection> tes$capturedCollection) {
        final SubmitNodeCollection value = (SubmitNodeCollection)original.call(instance);

        tes$capturedCollection.set(value);

        //noinspection unchecked
        return (E)value;
    }

    @WrapOperation(
            method = "executeTranslucent",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/feature/FeatureRenderDispatcher$PreparedFrame;executePhase(Lnet/minecraft/client/renderer/feature/phase/FeatureRenderPhase;Lnet/minecraft/client/renderer/feature/FeatureFrameContext;)V",
                    ordinal = 4)
    )
    private void tslatentitystatus$injectCustomTextFeatures(FeatureRenderDispatcher.PreparedFrame instance, FeatureRenderPhase<?> phase, FeatureFrameContext context, Operation<Void> original,
                                                            @Share("tes$capturedCollection") LocalRef<SubmitNodeCollection> tes$capturedCollection) {
        original.call(instance, phase, context);
        executePhase(tes$capturedCollection.get().tslatentitystatus$getCustomTexts(), context);
    }
}

package net.tslat.tslatentitystatus.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.client.renderer.state.level.ParticlesRenderState;
import net.tslat.tslatentitystatus.api.client.object.TESEntityRenderState;
import net.tslat.tslatentitystatus.core.client.hud.TESHud;
import net.tslat.tslatentitystatus.core.particle.TESParticleManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Shadow
    @Final
    private SubmitNodeStorage submitNodeStorage;
    @Shadow
    @Final
    private LevelRenderState levelRenderState;

    @WrapOperation(
            method = "submitEntities",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;submit(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lnet/minecraft/client/renderer/state/level/CameraRenderState;DDDLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;)V"))
    private <S extends EntityRenderState> void tslatentitystatus$injectInWorldRenderSubmits(EntityRenderDispatcher instance, S renderState, CameraRenderState camera,
                                                                                            double x, double y, double z, PoseStack poseStack,
                                                                                            SubmitNodeCollector submitNodeCollector, Operation<Void> original) {
        original.call(instance, renderState, camera, x, y, z, poseStack, submitNodeCollector);

        if (renderState instanceof LivingEntityRenderState livingEntityRenderState) {
            final TESEntityRenderState tesRenderState = livingEntityRenderState.tslatentitystatus$getRenderState();

            if (tesRenderState != null) {
                TESHud.submitWorldRenderTasks(tesRenderState, livingEntityRenderState, poseStack,
                                              submitNodeCollector instanceof SubmitNodeStorage storage ? storage : this.submitNodeStorage,
                                              this.levelRenderState.cameraRenderState);
            }
        }
    }

    @WrapOperation(
            method = "submitFeatures",
            at = @At(
                    value = "NEW",
                    target = "()Lcom/mojang/blaze3d/vertex/PoseStack;"))
    private PoseStack tslatentitystatus$capturePoseStack(Operation<PoseStack> original, @Share("tes$poseStack") LocalRef<PoseStack> tes$poseStack) {
        PoseStack poseStack = original.call();

        tes$poseStack.set(poseStack);

        return poseStack;
    }

    @WrapOperation(
            method = "submitFeatures",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/state/level/ParticlesRenderState;submit(Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V"))
    private void tslatentitystatus$submitTESParticles(ParticlesRenderState instance, SubmitNodeCollector submitNodeCollector, CameraRenderState camera,
                                                      Operation<Void> original, @Share("tes$poseStack") LocalRef<PoseStack> tes$poseStack) {
        original.call(instance, submitNodeCollector, camera);
        TESParticleManager.submitRenderTasks(tes$poseStack.get(), this.submitNodeStorage, this.levelRenderState.cameraRenderState);
    }
}

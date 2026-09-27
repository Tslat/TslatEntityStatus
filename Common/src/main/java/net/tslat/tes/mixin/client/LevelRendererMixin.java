package net.tslat.tes.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.resource.ResourceHandle;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.state.LevelRenderState;
import net.minecraft.world.entity.LivingEntity;
import net.tslat.tes.api.TESAPI;
import net.tslat.tes.core.hud.TESHud;
import net.tslat.tes.core.particle.TESParticleManager;
import net.tslat.tes.core.state.TESEntityTracking;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Shadow
    @Final
    private SubmitNodeStorage submitNodeStorage;
    @Shadow
    @Final
    private LevelRenderState levelRenderState;

    @WrapOperation(
            method = {"lambda$addMainPass$1", "method_62214"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/feature/FeatureRenderDispatcher;renderAllFeatures()V"
            )
    )
    public void tslatentitystatus$wrapFeatureRendering(FeatureRenderDispatcher instance, Operation<Void> original) {
        original.call(instance);

        final PoseStack poseStack = new PoseStack();

        for (LivingEntity entity : TESEntityTracking.getEntitiesToRender()) {
            TESHud.submitWorldRenderTasks(poseStack, this.submitNodeStorage, levelRenderState.cameraRenderState, entity, Minecraft.getInstance().getDeltaTracker());
        }
    }

    @Inject(method =
            {
                "lambda$addParticlesPass$2(Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lcom/mojang/blaze3d/resource/ResourceHandle;Lcom/mojang/blaze3d/resource/ResourceHandle;)V", // Forge
                "method_62213(Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lcom/mojang/blaze3d/resource/ResourceHandle;Lcom/mojang/blaze3d/resource/ResourceHandle;)V" // Fabric
            },
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/feature/FeatureRenderDispatcher;renderAllFeatures()V"),
            require = 0)
    private void tes$renderParticles(GpuBufferSlice shaderFog, ResourceHandle particleResource, ResourceHandle mainResource, CallbackInfo callback) {
        if (TESAPI.getConfig().particlesEnabled())
            TESParticleManager.render(new PoseStack(), this.submitNodeStorage, this.levelRenderState.cameraRenderState);
    }

    @Inject(method =
            {
                "lambda$addParticlesPass$2(Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lcom/mojang/blaze3d/resource/ResourceHandle;Lcom/mojang/blaze3d/resource/ResourceHandle;Lorg/joml/Matrix4f;)V"
            },
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/feature/FeatureRenderDispatcher;renderAllFeatures()V"),
            require = 0)
    private void tes$renderParticlesNeoForge(GpuBufferSlice shaderFog, ResourceHandle particleResource, ResourceHandle mainResource, Matrix4f modelViewMatrix, CallbackInfo callback) {
        if (TESAPI.getConfig().particlesEnabled())
            TESParticleManager.render(new PoseStack(), this.submitNodeStorage, this.levelRenderState.cameraRenderState);
    }
}

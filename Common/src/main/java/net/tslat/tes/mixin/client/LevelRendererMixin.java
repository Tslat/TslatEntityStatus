package net.tslat.tes.mixin.client;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.resource.ResourceHandle;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.state.LevelRenderState;
import net.minecraft.util.profiling.ProfilerFiller;
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
	@Shadow @Final private RenderBuffers renderBuffers;
    @Shadow
    @Final
    private SubmitNodeStorage submitNodeStorage;
    @Shadow
    @Final
    private LevelRenderState levelRenderState;

    @Inject(method =
            {
                 "lambda$addMainPass$1(Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lnet/minecraft/client/renderer/state/LevelRenderState;Lnet/minecraft/util/profiling/ProfilerFiller;Lorg/joml/Matrix4f;Lcom/mojang/blaze3d/resource/ResourceHandle;Lcom/mojang/blaze3d/resource/ResourceHandle;ZLnet/minecraft/client/renderer/culling/Frustum;Lcom/mojang/blaze3d/resource/ResourceHandle;Lcom/mojang/blaze3d/resource/ResourceHandle;)V" // Neo/Forge
            },
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V",
                    ordinal = 2),
            require = 0)
    private void tes$renderInWorldHuds(GpuBufferSlice shaderFog, LevelRenderState levelRenderState, ProfilerFiller profiler, Matrix4f frustumMatrix,
                                       ResourceHandle itemEntityResource, ResourceHandle entityOutlineResource, boolean renderBlockOutline, Frustum frustum,
                                       ResourceHandle translucentResource, ResourceHandle mainResource, CallbackInfo callback) {
        profiler.popPush("tesSubmitInWorldEntities");

        final PoseStack poseStack = new PoseStack();

        // TODO potentially just collect entities to render from submits in levelRenderState?
        // Will need to, since the entities should be gone by this stage
        // Move to RenderState
        // SIGH, I'm never getting stuff done
        for (LivingEntity entity : TESEntityTracking.getEntitiesToRender()) {
            TESHud.submitWorldRenderTasks(poseStack, this.submitNodeStorage, levelRenderState.cameraRenderState, entity, Minecraft.getInstance().getDeltaTracker());
        }
    }

    @Inject(method =
            {
                 "method_62214(Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lnet/minecraft/client/renderer/state/LevelRenderState;Lnet/minecraft/util/profiling/ProfilerFiller;Lorg/joml/Matrix4f;Lcom/mojang/blaze3d/resource/ResourceHandle;Lcom/mojang/blaze3d/resource/ResourceHandle;ZLnet/minecraft/client/renderer/culling/Frustum;Lcom/mojang/blaze3d/resource/ResourceHandle;Lcom/mojang/blaze3d/resource/ResourceHandle;)V" // Fabric
            },
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V",
                    ordinal = 2),
            require = 0)
    private void tes$renderInWorldHudsFabric(GpuBufferSlice shaderFog, LevelRenderState levelRenderState, ProfilerFiller profiler, Matrix4f frustumMatrix,
                                             ResourceHandle itemEntityResource, ResourceHandle entityOutlineResource, boolean renderBlockOutline,
                                             ResourceHandle translucentResource, ResourceHandle mainResource, CallbackInfo callback) {
        profiler.popPush("tesSubmitInWorldEntities");

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

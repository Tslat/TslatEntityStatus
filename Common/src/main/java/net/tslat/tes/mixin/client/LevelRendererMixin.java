package net.tslat.tes.mixin.client;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.resource.ResourceHandle;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.culling.Frustum;
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

	@Inject(method = {"lambda$addMainPass$2", "method_62214"},
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;endLastBatch()V",
					ordinal = 0,
					shift = At.Shift.AFTER),
			require = 0)
	private static void tes$renderInWorldHuds(GpuBufferSlice bufferSlice, DeltaTracker deltaTracker, Camera camera, ProfilerFiller profiler, Matrix4f projection,
                                       ResourceHandle itemEntityTarget, ResourceHandle entityOutlineTarget, boolean renderBlockOutline, Frustum frustum, ResourceHandle translucentTarget,
                                       ResourceHandle mainTarget, CallbackInfo ci) {
		final PoseStack poseStack = new PoseStack();

		for (LivingEntity entity : TESEntityTracking.getEntitiesToRender()) {
			TESHud.renderInWorld(poseStack, entity, deltaTracker);
		}
	}

	@Inject(method = "lambda$addMainPass$3",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;endLastBatch()V",
					ordinal = 0,
					shift = At.Shift.AFTER),
			require = 0)
	private void tes$renderInWorldHudsNf(GpuBufferSlice bufferSlice, DeltaTracker deltaTracker, Camera camera, ProfilerFiller profiler, Matrix4f projection, Frustum frustum,
										 ResourceHandle itemEntityTarget, ResourceHandle entityOutlineTarget, boolean renderBlockOutline, ResourceHandle translucentTarget,
										 ResourceHandle mainTarget, CallbackInfo ci) {
		final PoseStack poseStack = new PoseStack();

		for (LivingEntity entity : TESEntityTracking.getEntitiesToRender()) {
			TESHud.renderInWorld(poseStack, entity, deltaTracker);
		}
	}

	@Inject(method = {"lambda$addParticlesPass$3", "method_62213", "m_371651_"},
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/particle/ParticleEngine;render(Lnet/minecraft/client/Camera;FLnet/minecraft/client/renderer/MultiBufferSource$BufferSource;)V",
					shift = At.Shift.AFTER),
			require = 0)
	private void tes$renderParticles(GpuBufferSlice bufferSlice, ResourceHandle particlesTarget, ResourceHandle mainTarget, Camera camera, float partialTick, CallbackInfo ci) {
		if (TESAPI.getConfig().particlesEnabled())
			TESParticleManager.render(new PoseStack(), this.renderBuffers.bufferSource(), partialTick);
	}

	@Inject(method = "lambda$addParticlesPass$6",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/particle/ParticleEngine;render(Lnet/minecraft/client/Camera;FLnet/minecraft/client/renderer/MultiBufferSource$BufferSource;Lnet/minecraft/client/renderer/culling/Frustum;Ljava/util/function/Predicate;)V",
					shift = At.Shift.AFTER),
			require = 0)
	private void tes$renderParticlesNf(GpuBufferSlice bufferSlice, ResourceHandle particlesTarget, ResourceHandle mainTarget, Camera camera, float partialTick, Frustum frustum, Matrix4f projection, CallbackInfo ci) {
		if (TESAPI.getConfig().particlesEnabled())
			TESParticleManager.render(new PoseStack(), this.renderBuffers.bufferSource(), partialTick);
	}
}

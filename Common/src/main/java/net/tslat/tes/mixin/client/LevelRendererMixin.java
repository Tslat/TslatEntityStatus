package net.tslat.tes.mixin.client;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.resource.ResourceHandle;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogParameters;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.LivingEntity;
import net.tslat.tes.api.TESAPI;
import net.tslat.tes.api.util.TESClientUtil;
import net.tslat.tes.core.hud.TESHud;
import net.tslat.tes.core.particle.TESParticleManager;
import net.tslat.tes.core.state.TESEntityTracking;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
	@Inject(method = {"lambda$addMainPass$1", "method_62214"},
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;endLastBatch()V",
					ordinal = 0,
					shift = At.Shift.AFTER),
			require = 0)
	private static void tes$renderInWorldHuds(FogParameters fogParameters, DeltaTracker deltaTracker, Camera camera, ProfilerFiller profiler, Matrix4f frustumMatrix,
											  Matrix4f projectionMatrix, ResourceHandle resourcehandle2, ResourceHandle resourcehandle3, boolean renderBlockOutline,
											  Frustum frustum, ResourceHandle resourcehandle1, ResourceHandle resourcehandle, CallbackInfo ci) {
		final PoseStack poseStack = new PoseStack();

		for (LivingEntity entity : TESEntityTracking.getEntitiesToRender()) {
			TESHud.renderInWorld(poseStack, entity, deltaTracker);
		}
	}

	@Inject(method = "lambda$addMainPass$2",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;endLastBatch()V",
					ordinal = 0,
					shift = At.Shift.AFTER),
			require = 0)
	private static void tes$renderInWorldHudsNf(FogParameters fogParameters, DeltaTracker deltaTracker, Camera camera, ProfilerFiller profilerFiller, Matrix4f frustumMatrix,
												Matrix4f projectionMatrix, ResourceHandle<RenderTarget> mainTarget, ResourceHandle<RenderTarget> translucentTarget, ResourceHandle<RenderTarget> itemEntityTarget,
												ResourceHandle<RenderTarget> weatherTarget, Frustum frustum, boolean renderBlockOutline, ResourceHandle<RenderTarget> entityOutlineTarget, CallbackInfo ci) {
		final PoseStack poseStack = new PoseStack();

		for (LivingEntity entity : TESEntityTracking.getEntitiesToRender()) {
			TESHud.renderInWorld(poseStack, entity, deltaTracker);
		}
	}

	@Inject(method = {"lambda$addParticlesPass$2", "method_62213", "m_371651_"},
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/particle/ParticleEngine;render(Lnet/minecraft/client/Camera;FLnet/minecraft/client/renderer/MultiBufferSource$BufferSource;)V",
					shift = At.Shift.AFTER),
			require = 0)
	private static void tes$renderParticles(FogParameters fogParameters, ResourceHandle<RenderTarget> mainTarget, ResourceHandle<RenderTarget> particleTarget,
											Camera camera, float partialTick, CallbackInfo ci) {
		if (TESAPI.getConfig().particlesEnabled())
			TESParticleManager.render(TESClientUtil.createInlineGuiGraphics(new PoseStack(), Minecraft.getInstance().renderBuffers().bufferSource()), partialTick);
	}

	@Inject(method = "lambda$addParticlesPass$5",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/particle/ParticleEngine;render(Lnet/minecraft/client/Camera;FLnet/minecraft/client/renderer/MultiBufferSource$BufferSource;Lnet/minecraft/client/renderer/culling/Frustum;Ljava/util/function/Predicate;)V",
					shift = At.Shift.AFTER),
			require = 0)
	private static void tes$renderParticlesNf(FogParameters fogParameters, ResourceHandle<RenderTarget> mainTarget, ResourceHandle<RenderTarget> particleTarget,
									   Camera camera, float partialTick, Frustum frustum, Matrix4f frustumMatrix, Matrix4f projectionMatrix, CallbackInfo ci) {
		if (TESAPI.getConfig().particlesEnabled())
			TESParticleManager.render(TESClientUtil.createInlineGuiGraphics(new PoseStack(), Minecraft.getInstance().renderBuffers().bufferSource()), partialTick);
	}
}

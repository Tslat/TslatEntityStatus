package net.tslat.tes.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
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
	@Inject(method = "renderLevel", require = 0, at = {
			@At(value = "INVOKE", target = "Lnet/minecraft/client/particle/ParticleEngine;render(Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/Camera;F)V", shift = At.Shift.AFTER),
			@At(value = "INVOKE", target = "Lnet/minecraft/client/particle/ParticleEngine;render(Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/Camera;FLnet/minecraft/client/renderer/culling/Frustum;)V", shift = At.Shift.AFTER)})
	private void renderLevel(float partialTick, long nanoTime, boolean renderBlockOutlines, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f pose, Matrix4f effectModifiedPose, CallbackInfo callback) {
		if (TESAPI.getConfig().particlesEnabled()) {
			TESParticleManager.render(TESClientUtil.createInlineGuiGraphics(new PoseStack(), Minecraft.getInstance().renderBuffers().bufferSource()), partialTick);
		}
	}

	@Inject(method = "renderLevel", at = @At("TAIL"))
	private void renderInWorldHud(float partialTick, long nanoTime, boolean renderBlockOutlines, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f pose, Matrix4f effectModifiedPose, CallbackInfo callback) {
		final PoseStack poseStack = new PoseStack();

		poseStack.mulPose(pose);

		for (LivingEntity entity : TESEntityTracking.getEntitiesToRender()) {
			TESHud.renderInWorld(poseStack, entity, partialTick);
		}
	}
}
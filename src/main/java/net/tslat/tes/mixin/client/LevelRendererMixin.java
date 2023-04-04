package net.tslat.tes.mixin.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.vector.Matrix4f;
import net.tslat.tes.api.TESAPI;
import net.tslat.tes.core.hud.TESHud;
import net.tslat.tes.core.particle.TESParticleManager;
import net.tslat.tes.core.state.TESEntityTracking;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class LevelRendererMixin {
	@Inject(method = "renderLevel", require = 0, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/ParticleManager;renderParticles(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer$Impl;Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/renderer/ActiveRenderInfo;FLnet/minecraft/client/renderer/culling/ClippingHelper;)V", shift = At.Shift.AFTER))
	private void renderParticles(MatrixStack poseStack, float partialTick, long finishTime, boolean drawBlockOutlines, ActiveRenderInfo renderInfo, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projection, CallbackInfo callback) {
		if (TESAPI.getConfig().particlesEnabled())
			TESParticleManager.render(poseStack, partialTick);
	}

	@Inject(method = "renderLevel", at = @At("TAIL"))
	private void renderInWorldHud(MatrixStack poseStack, float partialTick, long finishTime, boolean drawBlockOutlines, ActiveRenderInfo renderInfo, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projection, CallbackInfo callback) {
		for (LivingEntity entity : TESEntityTracking.getEntitiesToRender()) {
			TESHud.renderInWorld(poseStack, entity, partialTick);
		}
	}
}

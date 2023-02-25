package net.tslat.tes.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.tslat.tes.api.TESConstants;
import net.tslat.tes.core.state.TESEntityTracking;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin<T extends Entity> {
	@Inject(method = "renderNameTag", at = @At("HEAD"), cancellable = true)
	private void cancelNameTag(T entity, Component name, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, CallbackInfo callback) {
		if (TESConstants.CONFIG.inWorldBarsEnabled() && TESEntityTracking.wasNameRendered(entity.getId()))
			callback.cancel();
	}
}

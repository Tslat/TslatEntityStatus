package net.tslat.tes.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.tslat.tes.api.TESAPI;
import net.tslat.tes.api.TESConstants;
import net.tslat.tes.api.util.TESClientUtil;
import net.tslat.tes.api.util.TESUtil;
import net.tslat.tes.core.state.TESEntityTracking;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
	@Inject(method = "render(Lnet/minecraft/world/entity/Entity;DDDFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/EntityRenderer;)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;render(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;DDDLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/EntityRenderer;)V"))
	private <E extends Entity, S extends EntityRenderState> void tes$trackRenderedEntity(E entity, double x, double y, double z, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, EntityRenderer<? super E, S> renderer, CallbackInfo ci) {
		LivingEntity target = TESConstants.UTILS.getLivingEntityIfPossible(entity);

		if (target != null && TESClientUtil.getClientPlayer() != null && TESUtil.shouldTESHandleEntity(target, TESClientUtil.getClientPlayer())) {
			TESEntityTracking.accountForEntity(target);

			if (TESAPI.getConfig().inWorldBarsEnabled())
				TESEntityTracking.addEntityToRender(target);
		}
	}

	@WrapOperation(method = "render(Lnet/minecraft/world/entity/Entity;DDDFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/EntityRenderer;)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;createRenderState(Lnet/minecraft/world/entity/Entity;F)Lnet/minecraft/client/renderer/entity/state/EntityRenderState;"), require = 0)
	private <E extends Entity, S extends EntityRenderState> S tes$cancelNameplateRender(EntityRenderer<E, S> renderer, E entity, float partialTick, Operation<S> original) {
		S renderState = original.call(renderer, entity, partialTick);

		if (TESConstants.CONFIG.inWorldBarsEnabled() && TESConstants.CONFIG.inWorldHudNameOverride() && TESEntityTracking.wasNameRendered(entity.getId()))
			renderState.nameTagAttachment = null;

		return renderState;
	}
}
package net.tslat.tes.mixin.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.tslat.tes.api.TESAPI;
import net.tslat.tes.api.TESConstants;
import net.tslat.tes.api.util.TESClientUtil;
import net.tslat.tes.api.util.TESUtil;
import net.tslat.tes.core.state.TESEntityTracking;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRendererManager.class)
public class EntityRenderDispatcherMixin {
	@Inject(method = "render",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;render(Lnet/minecraft/entity/Entity;FFLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V"))
	private <E extends Entity> void onEntityRender(E entity, double posX, double posY, double posZ, float rotYaw, float partialTick, MatrixStack poseStack, IRenderTypeBuffer bufferSource, int packedLight, CallbackInfo callback) {
		LivingEntity target = TESConstants.UTILS.getLivingEntityIfPossible(entity);

		if (TESUtil.isVisibleToPlayer(target, TESClientUtil.getClientPlayer())) {
			TESEntityTracking.accountForEntity(target);

			if (TESAPI.getConfig().inWorldBarsEnabled())
				TESEntityTracking.addEntityToRender(target);
		}
	}
}
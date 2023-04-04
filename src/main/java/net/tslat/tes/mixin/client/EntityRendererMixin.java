package net.tslat.tes.mixin.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.text.ITextComponent;
import net.tslat.tes.api.TESConstants;
import net.tslat.tes.core.state.TESEntityTracking;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin<T extends Entity> {
	@Inject(method = "renderNameTag", at = @At("HEAD"), cancellable = true)
	private void cancelNameTag(T entity, ITextComponent displayName, MatrixStack poseStack, IRenderTypeBuffer buffer, int packedLight, CallbackInfo callback) {
		if (TESConstants.CONFIG.inWorldBarsEnabled() && TESConstants.CONFIG.inWorldHudNameOverride() && TESEntityTracking.wasNameRendered(entity.getId()))
			callback.cancel();
	}
}
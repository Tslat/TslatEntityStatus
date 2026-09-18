package net.tslat.tslatentitystatus.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.tslat.tslatentitystatus.api.TESConfig;
import net.tslat.tslatentitystatus.api.TESConstants;
import net.tslat.tslatentitystatus.api.client.object.TESEntityRenderState;
import net.tslat.tslatentitystatus.api.client.util.TESClientUtil;
import net.tslat.tslatentitystatus.api.common.util.TESUtil;
import net.tslat.tslatentitystatus.core.client.hud.BuiltinHudElements;
import net.tslat.tslatentitystatus.core.state.TESEntityState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
	/// Handle the [EntityRenderState] extraction for a render pass for the given entity
    @WrapOperation(
			method = "extractEntity",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;createRenderState(Lnet/minecraft/world/entity/Entity;F)Lnet/minecraft/client/renderer/entity/state/EntityRenderState;"),
			require = 0)
	private <E extends Entity, S extends EntityRenderState> S tslatentitystatus$handleRenderExtraction(EntityRenderer<E, S> renderer, E entity, float partialTick, Operation<S> original) {
		final S renderState = original.call(renderer, entity, partialTick);
		
		if (entity instanceof LivingEntity livingEntity && renderState instanceof LivingEntityRenderState livingRenderState) {
			final TESConfig config = TESConstants.getConfig();
			final Player player = TESClientUtil.getClientPlayer();
			
			if (player != null && TESUtil.shouldTESHandleEntity(livingEntity, player)) {
				final TESEntityState entityState = TESClientUtil.getTESEntityState(livingEntity);
				
				if (entityState != null) {
					final Vec3 entityRenderOffset = renderer.getRenderOffset(renderState);
					final TESEntityRenderState tesRenderState = TESEntityRenderState.create(livingEntity, entityState, entityRenderOffset, partialTick);
					
					livingRenderState.tslatentitystatus$setRenderState(tesRenderState);

					if (config.inWorldBarsEnabled() &&
						config.inWorldHudNameOverride() &&
						config.inWorldHUDActivation().shouldBeActiveFor(livingRenderState, tesRenderState) &&
						BuiltinHudElements.willRenderName(true, livingEntity.hasCustomName()) &&
						entityState.shouldRenderInWorldHud(livingEntity)) {
						renderState.nameTagAttachment = null;
					}
				}
			}
		}
		
		return renderState;
	}
}
package net.tslat.tes.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.tslat.tes.api.TESAPI;
import net.tslat.tes.api.TESConfig;
import net.tslat.tes.api.TESConstants;
import net.tslat.tes.api.util.TESClientUtil;
import net.tslat.tes.api.util.TESUtil;
import net.tslat.tes.core.state.EntityState;
import net.tslat.tes.core.state.TESEntityTracking;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
    @WrapOperation(method = "extractEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;createRenderState(Lnet/minecraft/world/entity/Entity;F)Lnet/minecraft/client/renderer/entity/state/EntityRenderState;"), require = 0)
	private <E extends Entity, S extends EntityRenderState> S tes$cancelNameplateRender(EntityRenderer<E, S> renderer, E entity, float partialTick, Operation<S> original) {
		S renderState = original.call(renderer, entity, partialTick);
		TESConfig config = TESAPI.getConfig();

        if (TESClientUtil.getClientPlayer() != null && TESUtil.shouldTESHandleEntity(entity, TESClientUtil.getClientPlayer())) {
            if (config.inWorldBarsEnabled() && config.inWorldHudNameOverride()) {
                if (config.inWorldHudEntityName() || (config.inWorldHudNameOverride() && entity.hasCustomName())) {
                    if (entity instanceof LivingEntity livingEntity) {
                        EntityState state = TESEntityTracking.getStateForEntity(livingEntity);

                        if (state != null && state.isValid() && config.inWorldHUDActivation().test(state))
                            renderState.nameTagAttachment = null;
                    }
                }
            }

            LivingEntity target = TESConstants.UTILS.getLivingEntityIfPossible(entity);

            if (target != null) {
                TESEntityTracking.accountForEntity(target);

                if (TESAPI.getConfig().inWorldBarsEnabled())
                    TESEntityTracking.addEntityToRender(target);
            }
        }

		return renderState;
	}
}
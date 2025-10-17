package net.tslat.tes.mixin.common;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.tslat.tes.api.TESConstants;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
	@Inject(method = "onEffectAdded", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffect;addAttributeModifiers(Lnet/minecraft/world/entity/ai/attributes/AttributeMap;I)V"))
	private void addEffect(MobEffectInstance effectInstance, @Nullable Entity entity, CallbackInfo callback) {
        final LivingEntity self = (LivingEntity)(Object)this;

        if (!self.level().isClientSide())
		    TESConstants.NETWORKING.sendEffectsSync((LivingEntity)(Object)this, Set.of(effectInstance.getEffect()), Set.of());
	}

	@Inject(method = "onEffectRemoved", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffect;removeAttributeModifiers(Lnet/minecraft/world/entity/ai/attributes/AttributeMap;)V"))
	private void removeEffect(MobEffectInstance effectInstance, CallbackInfo callback) {
        final LivingEntity self = (LivingEntity)(Object)this;

        if (!self.level().isClientSide())
		    TESConstants.NETWORKING.sendEffectsSync((LivingEntity)(Object)this, Set.of(), Set.of(effectInstance.getEffect()));
	}
}

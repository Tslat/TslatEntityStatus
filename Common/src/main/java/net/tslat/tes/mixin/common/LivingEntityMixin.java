package net.tslat.tes.mixin.common;

import net.minecraft.core.Registry;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.tslat.tes.api.TESConstants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.Set;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
	@Inject(method = "onEffectAdded", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffect;addAttributeModifiers(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/ai/attributes/AttributeMap;I)V"))
	private void addEffect(MobEffectInstance effectInstance, @Nullable Entity entity, CallbackInfo callback) {
		TESConstants.NETWORKING.sendEffectsSync((LivingEntity)(Object)this, Set.of(Registry.MOB_EFFECT.getKey(effectInstance.getEffect())), Set.of());
	}

	@Inject(method = "onEffectRemoved", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffect;removeAttributeModifiers(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/ai/attributes/AttributeMap;I)V"))
	private void removeEffect(MobEffectInstance effectInstance, CallbackInfo callback) {
		TESConstants.NETWORKING.sendEffectsSync((LivingEntity)(Object)this, Set.of(), Set.of(Registry.MOB_EFFECT.getKey(effectInstance.getEffect())));
	}
}

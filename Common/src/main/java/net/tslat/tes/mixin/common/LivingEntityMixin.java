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

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
	@Inject(method = "onEffectAdded", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffect;addAttributeModifiers(Lnet/minecraft/world/entity/ai/attributes/AttributeMap;I)V"))
	private void tes$syncAddedEffect(MobEffectInstance effectInstance, @Nullable Entity entity, CallbackInfo callback) {
		TESConstants.NETWORKING.sendEffectsSync((LivingEntity)(Object)this, Set.of(effectInstance.getEffect()), Set.of());
	}

	@Inject(method = "onEffectsRemoved", at = @At("HEAD"))
	private void tes$syncRemovedEffect(Collection<MobEffectInstance> effects, CallbackInfo ci) {
		TESConstants.NETWORKING.sendEffectsSync((LivingEntity)(Object)this, Set.of(), effects.stream().map(MobEffectInstance::getEffect).collect(Collectors.toSet()));
	}
}

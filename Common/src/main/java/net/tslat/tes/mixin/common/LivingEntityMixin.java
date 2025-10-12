package net.tslat.tes.mixin.common;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
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

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
	@Inject(method = "onEffectAdded", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffect;addAttributeModifiers(Lnet/minecraft/world/entity/ai/attributes/AttributeMap;I)V"))
	private void tes$syncAddedEffect(MobEffectInstance effectInstance, @Nullable Entity entity, CallbackInfo callback) {
		TESConstants.NETWORKING.sendEffectsSync((LivingEntity)(Object)this, Set.of(effectInstance.getEffect()), Set.of());
	}

	@Inject(method = "onEffectsRemoved", at = @At("HEAD"))
	private void tes$syncRemovedEffect(Collection<MobEffectInstance> effects, CallbackInfo ci) {
		if (!((LivingEntity)(Object)this).level().isClientSide()) {
			Set<Holder<MobEffect>> currentEffects = new ObjectOpenHashSet<>(effects.size());

			for (MobEffectInstance instance : effects) {
				currentEffects.add(instance.getEffect());
			}

			TESConstants.NETWORKING.sendEffectsSync((LivingEntity)(Object)this, Set.of(), currentEffects);
		}
	}
}

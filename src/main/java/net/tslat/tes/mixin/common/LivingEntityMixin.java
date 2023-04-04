package net.tslat.tes.mixin.common;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.tslat.tes.api.TESConstants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
	@Inject(method = "onEffectAdded", at = @At(value = "INVOKE", target = "Lnet/minecraft/potion/Effect;addAttributeModifiers(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/entity/ai/attributes/AttributeModifierManager;I)V"))
	private void addEffect(EffectInstance effectInstance, CallbackInfo callback) {
		TESConstants.NETWORKING.sendEffectsSync((LivingEntity)(Object)this, Util.make(new ObjectOpenHashSet<>(1), set -> set.add(Registry.MOB_EFFECT.getKey(effectInstance.getEffect()))), Collections.emptySet());
	}

	@Inject(method = "onEffectRemoved", at = @At(value = "INVOKE", target = "Lnet/minecraft/potion/Effect;removeAttributeModifiers(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/entity/ai/attributes/AttributeModifierManager;I)V"))
	private void removeEffect(EffectInstance effectInstance, CallbackInfo callback) {
		TESConstants.NETWORKING.sendEffectsSync((LivingEntity)(Object)this, Collections.emptySet(), Util.make(new ObjectOpenHashSet<>(1), set -> set.add(Registry.MOB_EFFECT.getKey(effectInstance.getEffect()))));
	}
}

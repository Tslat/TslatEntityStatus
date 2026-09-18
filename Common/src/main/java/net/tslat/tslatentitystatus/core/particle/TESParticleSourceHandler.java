package net.tslat.tslatentitystatus.core.particle;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.tslat.tslatentitystatus.api.client.object.TESParticle;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

/// Interface for custom handling of damage-based [TESParticle]s predicated by their [DamageSource]
///
/// This can be used to more reliably special-handle damage particles for specific DamageSources (such as freezing damage, fire damage, etc.)
public interface TESParticleSourceHandler {
	/// Override this method to handle custom DamageSource claims for TESParticles
	///
	/// @param entity	 		The entity whose health change is currently being handled
	/// @param damageAmount 	The amount of damage that was taken (by default, the value which will be displayed by the particle)
	/// @param damageSource 	The DamageSource for the damage dealt
	/// @param particleAdder 	Helper function for adding particles to TES to handle
	/// @return 				Whether you have handled the event or not. Returning true prevents other handlers and TES itself from generating more particles for this tick
	boolean checkIncomingDamage(LivingEntity entity, float damageAmount, @Nullable DamageSource damageSource, Consumer<TESParticle<?>> particleAdder);
}

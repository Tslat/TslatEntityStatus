package net.tslat.tes.core.particle;

import net.minecraft.world.damagesource.DamageSource;
import net.tslat.tes.api.TESParticle;
import net.tslat.tes.core.state.EntityState;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Interface for custom handling of damage-based {@link net.tslat.tes.api.TESParticle TESParticles} predicated by their {@link net.minecraft.world.damagesource.DamageSource}<br>
 * This can be used to more reliably special-handle damage particles for specific DamageSources (such as freezing damage, fire damage, etc)
 */
public interface TESParticleSourceHandler {
	/**
	 * Override this method to handle custom DamageSource claims for TESParticles.
	 * @param entityState The EntityState for the entity currently being handled
	 * @param damageAmount The amount of damage that was taken (by default, the value which will be displayed by the particle)
	 * @param damageSource The DamageSource for the damage dealt
	 * @param particleAdder Helper function for adding particles to TES to handle
	 * @return Whether you have handled the event or not. Returning true prevents other handlers and TES itself from generating more particles for this tick
	 */
	boolean checkIncomingDamage(EntityState entityState, float damageAmount, @Nullable DamageSource damageSource, Consumer<TESParticle<?>> particleAdder);
}

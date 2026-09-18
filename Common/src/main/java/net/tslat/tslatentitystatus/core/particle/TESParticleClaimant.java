package net.tslat.tslatentitystatus.core.particle;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.tslat.tslatentitystatus.api.TESParticleAPI;
import net.tslat.tslatentitystatus.api.TESRegistrationAPI;
import net.tslat.tslatentitystatus.api.client.object.TESParticle;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

/// Functional interface for a 'claimant' in TES' particle claimant system
///
/// How this works:
///
///   1. Register a claimant via [TESRegistrationAPI#registerParticleClaimant]
///   2. Submit a claim via [TESParticleAPI#submitParticleClaim]
///   3. When your claimant is called, check that the healthDelta is what you expected (and any other conditions), then submit your new particle to the particleAdder
///   4. Return the healthDelta, with your 'claimed' amount subtracted. I.E. if you are claiming 5 damage from a hit, you would return `healthDelta + 5`
///
/// NOTE: healthDelta is a <u>negative</u> value for damage taken, and a <u>positive</u> value for health healed
@FunctionalInterface
public interface TESParticleClaimant {
	/// Handle a health status change for an entity
	///
	/// @param entity	 		The entity that the claim is being checked for
	/// @param healthDelta 		The difference in health from the last status. Negative values indicate damage taken, positive values is health healed
	/// @param data 			Optional additional data for the claim. This is what you would have provided when you submitted the claim
	/// @param particleAdder 	Consumer for adding additional particles from your claim, for ease-of-use
	/// @return 				The remaining healthDelta after your claim. E.G. If you are claiming 5 damage from the attack, you would return `healthDelta + 5`
	float checkClaim(LivingEntity entity, float healthDelta, @Nullable CompoundTag data, Consumer<TESParticle<?>> particleAdder);
}

package net.tslat.tes.core.particle;

import net.minecraft.nbt.CompoundTag;
import net.tslat.tes.api.TESParticle;
import net.tslat.tes.core.state.EntityState;

import org.jetbrains.annotations.Nullable;
import java.util.function.Consumer;

/**
 * Functional interface for a 'claimant' in TES' particle claimant system.<br>
 * How this works:<br>
 * <ol>
 *     <li>Register a claimant via {@link net.tslat.tes.api.TESAPI#registerParticleClaimant TESAPI.registerParticleClaimant}</li>
 *     <li>Submit a claim via {@link net.tslat.tes.api.TESAPI#submitParticleClaim}</li>
 *     <li>When your claimant is called, check that the healthDelta is what you expected (and any other conditions), then submit your new particle to the particleAdder</li>
 *     <li>Return the healthDelta, with your 'claimed' amount subtracted. I.E. if you are claiming 5 damage from a hit, you would return healthDelta + 5</li>
 * </ol>
 * NOTE: healthDelta is a <u>negative</u> value for damage taken, and a <u>positive</u> value for health healed
 */
@FunctionalInterface
public interface TESParticleClaimant {
	/**
	 * Handle a health status change for an entity.
	 * @param entityState The EntityState for the given entity
	 * @param healthDelta The difference in health from the last status. Negative values indicate damage taken, positive values is health healed
	 * @param data Optional additional data for the claim. This is what you would have provided when you submitted the claim
	 * @param particleAdder Consumer for adding additional particles from your claim, for ease-of-use
	 * @return The remaining healthDelta after your claim. E.G. If you are claiming 5 damage from the attack, you would return healthDelta + 5
	 */
	float checkClaim(EntityState entityState, float healthDelta, @Nullable CompoundTag data, Consumer<TESParticle<?>> particleAdder);
}

package net.tslat.tes.core.particle;

import net.minecraft.nbt.CompoundTag;
import net.tslat.tes.api.TESParticle;
import net.tslat.tes.core.state.EntityState;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * Functional interface for a 'claimant' in TES' particle claimant system.<br>
 * How this works:<br>
 * <ol>
 *     <li>Register a claimant via {@link net.tslat.tes.api.TESAPI#registerParticleClaimant TESAPI.registerParticleClaimant}</li>
 *     <li>Submit a claim via {}</li>
 * </ol>
 */
@FunctionalInterface
public interface TESParticleClaimant {
	float checkClaim(EntityState entityState, float healthDelta, @Nullable CompoundTag data, Consumer<TESParticle<?>> particleAdder);
}

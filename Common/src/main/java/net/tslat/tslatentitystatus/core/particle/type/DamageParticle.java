package net.tslat.tslatentitystatus.core.particle.type;

import net.tslat.tslatentitystatus.api.TESConstants;
import net.tslat.tslatentitystatus.api.client.object.TESParticle;
import net.tslat.tslatentitystatus.core.state.TESEntityState;
import org.joml.Vector3f;

/// Built-in class for damage-type [`TES Particles`][TESParticle]
public class DamageParticle extends NumericParticle {
	public DamageParticle(TESEntityState entityState, Vector3f position, double amount) {
		this(entityState, position, Animation.POP_OFF, amount);
	}

	public DamageParticle(TESEntityState entityState, Vector3f position, Animation animation, double amount) {
		this(entityState, position, animation, amount, TESConstants.getConfig().defaultParticleLifespan());
	}

	public DamageParticle(TESEntityState entityState, Vector3f position, Animation animation, double amount, int lifespan) {
		super(entityState, position, animation, amount, lifespan);

		withColour(TESConstants.getConfig().getDamageParticleColour());
	}
}

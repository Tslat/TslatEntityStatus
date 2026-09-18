package net.tslat.tslatentitystatus.core.particle.type;

import net.tslat.tslatentitystatus.api.TESConstants;
import net.tslat.tslatentitystatus.api.client.object.TESParticle;
import net.tslat.tslatentitystatus.core.state.TESEntityState;
import org.joml.Vector3f;

/// Built-in class for healing-type [`TES Particles`][TESParticle]
public class HealParticle extends NumericParticle {
	public HealParticle(TESEntityState entityState, Vector3f position, double amount) {
		this(entityState, position, Animation.RISE, amount);
	}

	public HealParticle(TESEntityState entityState, Vector3f position, Animation animation, double amount) {
		this(entityState, position, animation, amount, TESConstants.getConfig().defaultParticleLifespan() / 2);
	}

	public HealParticle(TESEntityState entityState, Vector3f position, Animation animation, double amount, int lifespan) {
		super(entityState, position, animation, amount, lifespan);

		withColour(TESConstants.getConfig().getHealParticleColour());
	}
}

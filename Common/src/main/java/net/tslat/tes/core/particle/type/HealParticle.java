package net.tslat.tes.core.particle.type;

import net.tslat.tes.api.TESAPI;
import net.tslat.tes.api.TESParticle;
import org.joml.Vector3f;

/**
 * Built-in class for healing-type {@link TESParticle TES Particles}
 */
public class HealParticle extends NumericParticle {
	public HealParticle(Vector3f position, double amount) {
		this(position, Animation.RISE, amount);
	}

	public HealParticle(Vector3f position, Animation animation, double amount) {
		this(position, animation, amount, DEFAULT_LIFESPAN);
	}

	public HealParticle(Vector3f position, Animation animation, double amount, int lifespan) {
		super(position, animation, amount, lifespan);

		setColour(TESAPI.getConfig().getHealParticleColour());
	}
}

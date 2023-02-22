package net.tslat.tes.core.particle.type;

import net.tslat.tes.api.TESAPI;
import net.tslat.tes.api.TESParticle;
import org.joml.Vector3f;

/**
 * Built-in class for damage-type {@link TESParticle TES Particles}
 */
public class DamageParticle extends NumericParticle {
	public DamageParticle(Vector3f position, double amount) {
		this(position, Animation.POP_OFF, amount);
	}

	public DamageParticle(Vector3f position, Animation animation, double amount) {
		this(position, animation, amount, DEFAULT_LIFESPAN);
	}

	public DamageParticle(Vector3f position, Animation animation, double amount, int lifespan) {
		super(position, animation, amount, lifespan);

		setColour(TESAPI.getConfig().getDamageParticleColour());
	}
}

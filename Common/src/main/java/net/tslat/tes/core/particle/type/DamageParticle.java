package net.tslat.tes.core.particle.type;

import com.mojang.math.Vector3f;
import net.tslat.tes.api.TESAPI;
import net.tslat.tes.api.TESParticle;
import net.tslat.tes.core.state.EntityState;

/**
 * Built-in class for damage-type {@link TESParticle TES Particles}
 */
public class DamageParticle extends NumericParticle {
	public DamageParticle(EntityState entityState, Vector3f position, double amount) {
		this(entityState, position, Animation.POP_OFF, amount);
	}

	public DamageParticle(EntityState entityState, Vector3f position, Animation animation, double amount) {
		this(entityState, position, animation, amount, DEFAULT_LIFESPAN);
	}

	public DamageParticle(EntityState entityState, Vector3f position, Animation animation, double amount, int lifespan) {
		super(entityState, position, animation, amount, lifespan);

		setColour(TESAPI.getConfig().getDamageParticleColour());
	}
}

package net.tslat.tes.core.particle.type;

import com.mojang.math.Vector3f;
import net.tslat.tes.api.TESAPI;
import net.tslat.tes.api.util.TESUtil;
import net.tslat.tes.core.state.EntityState;

/**
 * Generic class for numeric-based TES Particles.
 */
public class NumericParticle extends TextParticle {
	public NumericParticle(EntityState entityState, Vector3f position, double value) {
		this(entityState, position, Animation.POP_OFF, value);
	}

	public NumericParticle(EntityState entityState, Vector3f position, Animation animation, double value) {
		this(entityState, position, animation, value, DEFAULT_LIFESPAN);
	}

	public NumericParticle(EntityState entityState, Vector3f position, Animation animation, double value, int lifespan) {
		super(entityState, position, animation, TESUtil.roundToDecimal(value, TESAPI.getConfig().particleDecimalPoints()), lifespan);
	}
}

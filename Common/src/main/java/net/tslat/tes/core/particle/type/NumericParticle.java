package net.tslat.tes.core.particle.type;

import net.tslat.tes.api.TESAPI;
import net.tslat.tes.api.util.TESUtil;
import org.joml.Vector3f;

/**
 * Generic class for numeric-based TES Particles.
 */
public class NumericParticle extends TextParticle {
	public NumericParticle(Vector3f position, double value) {
		this(position, Animation.POP_OFF, value);
	}

	public NumericParticle(Vector3f position, Animation animation, double value) {
		this(position, animation, value, DEFAULT_LIFESPAN);
	}

	public NumericParticle(Vector3f position, Animation animation, double value, int lifespan) {
		super(position, animation, TESUtil.roundToDecimal(value, TESAPI.getConfig().particleDecimalPoints()), lifespan);
	}
}

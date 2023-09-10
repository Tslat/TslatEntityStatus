package net.tslat.tes.core.particle.type;

import net.tslat.tes.api.TESAPI;
import net.tslat.tes.api.TESConstants;
import net.tslat.tes.api.util.TESUtil;
import net.tslat.tes.core.state.EntityState;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

/**
 * Generic class for numeric-based TES Particles.
 */
public class NumericParticle extends TextParticle {
	public NumericParticle(@Nullable EntityState entityState, Vector3f position, double value) {
		this(entityState, position, Animation.POP_OFF, value);
	}

	public NumericParticle(@Nullable EntityState entityState, Vector3f position, Animation animation, double value) {
		this(entityState, position, animation, value, TESConstants.CONFIG.defaultParticleLifespan());
	}

	public NumericParticle(@Nullable EntityState entityState, Vector3f position, Animation animation, double value, int lifespan) {
		super(entityState, position, animation, TESUtil.roundToDecimal(value, TESAPI.getConfig().particleDecimalPoints()), lifespan);
	}
}

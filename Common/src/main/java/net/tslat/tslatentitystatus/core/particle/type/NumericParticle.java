package net.tslat.tslatentitystatus.core.particle.type;

import net.tslat.tslatentitystatus.api.TESConstants;
import net.tslat.tslatentitystatus.api.common.util.TESUtil;
import net.tslat.tslatentitystatus.core.state.TESEntityState;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

/// Generic class for numeric-based TES Particles
public class NumericParticle extends TextParticle {
	public NumericParticle(@Nullable TESEntityState entityState, Vector3f position, double value) {
		this(entityState, position, Animation.POP_OFF, value);
	}

	public NumericParticle(@Nullable TESEntityState entityState, Vector3f position, Animation animation, double value) {
		this(entityState, position, animation, value, TESConstants.getConfig().defaultParticleLifespan());
	}

	public NumericParticle(@Nullable TESEntityState entityState, Vector3f position, Animation animation, double value, int lifespan) {
		super(entityState, position, animation, TESUtil.roundToDecimal(value, TESConstants.getConfig().particleDecimalPoints()), lifespan);
	}
}

package net.tslat.tslatentitystatus.core.particle.type;

import net.minecraft.client.Minecraft;
import net.tslat.tslatentitystatus.api.TESConstants;
import net.tslat.tslatentitystatus.api.client.object.TESParticle;
import net.tslat.tslatentitystatus.api.client.util.TESClientUtil;
import net.tslat.tslatentitystatus.core.state.TESEntityState;
import org.jspecify.annotations.Nullable;
import org.joml.Vector3f;

/// Generic class for TES Particles, with default base handling common to most particle types
public abstract class GenericTESParticle<D> implements TESParticle<D> {
	protected int lifetime;
	protected final Vector3f pos;
	protected final Vector3f prevPos;
	protected final Vector3f velocity;
	protected final Animation animation;

	protected final @Nullable TESEntityState entityState;

	protected GenericTESParticle(@Nullable TESEntityState entityState, Vector3f position) {
		this(entityState, position, Animation.POP_OFF);
	}

	protected GenericTESParticle(@Nullable TESEntityState entityState, Vector3f position, Animation animation) {
		this(entityState, position, animation, TESConstants.getConfig().defaultParticleLifespan());
	}

	protected GenericTESParticle(@Nullable TESEntityState entityState, Vector3f position, Animation animation, int lifespan) {
		this.entityState = entityState;
		this.lifetime = lifespan;
		this.pos = position;
		this.prevPos = new Vector3f(this.pos);
		this.animation = animation;
		this.velocity = animation.getInitialVelocity(this, position, TESClientUtil.getRandom());
	}

	@Override
	public void tick(Minecraft mc) {
		this.lifetime--;

		this.prevPos.set(this.pos);
		this.animation.perTickModifier(this, this.lifetime, this.pos, this.prevPos, this.velocity, TESClientUtil.getRandom());
	}

	@Override
	public boolean isValid() {
		return this.lifetime >= 0;
	}
}

package net.tslat.tes.core.particle.type;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.vector.Vector3f;
import net.tslat.tes.api.TESConstants;
import net.tslat.tes.api.TESParticle;
import net.tslat.tes.core.state.EntityState;

import javax.annotation.Nullable;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Generic class for TES Particles, with default base handling common to most particle types
 */
public abstract class GenericTESParticle<D> implements TESParticle<D> {
	@Deprecated
	protected static final int DEFAULT_LIFESPAN = 10;

	protected int lifetime;
	protected final Vector3f pos;
	protected final Vector3f prevPos;
	protected final Vector3f velocity;
	protected final Animation animation;
	@Nullable
	protected final EntityState entityState;

	protected GenericTESParticle(@Nullable EntityState entityState, Vector3f position) {
		this(entityState, position, Animation.POP_OFF);
	}

	protected GenericTESParticle(@Nullable EntityState entityState, Vector3f position, Animation animation) {
		this(entityState, position, Animation.POP_OFF, TESConstants.CONFIG.defaultParticleLifespan());
	}

	protected GenericTESParticle(@Nullable EntityState entityState, Vector3f position, Animation animation, int lifespan) {
		this.entityState = entityState;
		this.lifetime = lifespan;
		this.pos = position;
		this.prevPos = this.pos.copy();
		this.animation = animation;
		this.velocity = animation.getInitialVelocity(this, position, ThreadLocalRandom.current());
	}

	@Override
	public void tick(Minecraft mc) {
		this.lifetime--;

		this.prevPos.set(this.pos.x(), this.pos.y(), this.pos.z());
		this.animation.perTickModifier(this, this.lifetime, this.pos, this.prevPos, this.velocity, ThreadLocalRandom.current());
	}

	@Override
	public boolean isValid() {
		return this.lifetime >= 0;
	}
}

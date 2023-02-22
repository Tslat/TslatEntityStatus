package net.tslat.tes.core.particle.type;

import net.minecraft.client.Minecraft;
import net.tslat.tes.api.TESParticle;
import org.joml.Vector3f;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Generic class for TES Particles, with default base handling common to most particle types
 */
public abstract class GenericTESParticle<D> implements TESParticle<D> {
	protected static final int DEFAULT_LIFESPAN = 10;

	protected int lifetime;
	protected final Vector3f pos;
	protected final Vector3f prevPos;
	protected final Vector3f velocity;
	protected final Animation animation;

	protected GenericTESParticle(Vector3f position) {
		this(position, Animation.POP_OFF);
	}

	protected GenericTESParticle(Vector3f position, Animation animation) {
		this(position, Animation.POP_OFF, DEFAULT_LIFESPAN);
	}

	protected GenericTESParticle(Vector3f position, Animation animation, int lifespan) {
		this.lifetime = lifespan;
		this.pos = position;
		this.prevPos = new Vector3f(this.pos);
		this.animation = animation;
		this.velocity = animation.getInitialVelocity(this, position, ThreadLocalRandom.current());
	}

	@Override
	public void tick(Minecraft mc) {
		this.lifetime--;

		this.prevPos.set(this.pos);
		this.animation.perTickModifier(this, this.lifetime, this.pos, this.prevPos, this.velocity, ThreadLocalRandom.current());
	}

	@Override
	public boolean isValid() {
		return this.lifetime >= 0;
	}
}

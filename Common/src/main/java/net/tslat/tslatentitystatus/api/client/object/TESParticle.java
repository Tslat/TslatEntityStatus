package net.tslat.tslatentitystatus.api.client.object;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.util.RandomSource;
import net.tslat.tslatentitystatus.api.client.util.TESClientUtil;
import net.tslat.tslatentitystatus.api.client.util.TESRenderUtil;
import net.tslat.tslatentitystatus.core.particle.type.GenericTESParticle;
import org.joml.Vector3f;

/// Base interface for `TES` particles
///
/// @see GenericTESParticle
public interface TESParticle<D> {
	/// Receive and/or update the particle's data
	void updateData(D data);

	/// Submit the render operation for the particle
	///
	/// The implementing class is responsible for positioning and validating the particle prior to rendering
	///
	/// The [PoseStack] has already been transformed relative to the player's camera at this stage
	void submitRender(TESHudRenderContext.InWorldArgs renderArgs, Minecraft mc, Font fontRenderer);

	/// Tick the particle (if required)
	///
	/// Can be used for uniformly moving or otherwise modifying the particle
	void tick(Minecraft mc);

	/// Whether the particle is still valid or not
	///
	/// Returning false here will have the particle removed from rendering before the next render cycle
	boolean isValid();

	/// Create a defaulted render environment, then perform a render operation
	///
	/// This method handles the translations and rotations required to render text in-world
	default void defaultedTextRender(Minecraft mc, PoseStack poseStack, Vector3f prevPos, Vector3f pos, float partialTick, Runnable renderCallback) {
		final float scale = 0.035f * TESClientUtil.getConfig().particleScale();
		final Camera camera = mc.gameRenderer.getMainCamera();
		final Vector3f renderPos = prevPos
				.lerp(pos, partialTick, new Vector3f())
				.sub(camera.position().toVector3f());

		poseStack.pushPose();
		poseStack.translate(renderPos.x, renderPos.y, renderPos.z);
		TESRenderUtil.positionFacingCamera(poseStack);
		poseStack.scale(scale, scale, scale);
		renderCallback.run();
		poseStack.popPose();
	}

	/// Animation handler abstract class for [TESParticle]s
	///
	/// Provide your own implementation for custom animation
	abstract class Animation {
		/// Provide the initial velocity vector for the particle
		///
		/// @param particle The [TESParticle] being spawned
		/// @param position The position that the particle is being spawned at
		/// @param random 	The [RandomSource] instance to use for randomised handling
		/// @return			The velocity that the particle should spawn with
		public abstract Vector3f getInitialVelocity(TESParticle<?> particle, Vector3f position, RandomSource random);

		/// Per-tick access for the animator, allowing for dynamic control
		///
		/// @param particle The [TESParticle] being ticked
		/// @param lifetime The current lifetime (in ticks) of the particle
		/// @param pos		The current position of the particle
		/// @param prevPos	The position the particle was at last tick
		/// @param velocity	The current velocity of the particle. This should be modified directly
		/// @param random	The [RandomSource] instance to use for randomised handling
		public abstract void perTickModifier(TESParticle<?> particle, int lifetime, Vector3f pos, Vector3f prevPos, Vector3f velocity, RandomSource random);

		public static final Animation POP_OFF = new Animation() {
			@Override
			public Vector3f getInitialVelocity(TESParticle<?> particle, Vector3f position, RandomSource random) {
				return new Vector3f((float)random.nextGaussian() * 0.03f + 0.025f, random.nextFloat() * 0.035f + 0.37f, (float)random.nextGaussian() * 0.03f + 0.025f);
			}

			@Override
			public void perTickModifier(TESParticle<?> particle, int lifetime, Vector3f pos, Vector3f prevPos, Vector3f velocity, RandomSource random) {
				velocity.sub(0, 0.05f, 0);
				pos.add(velocity);
			}
		};
		public static final Animation RISE = new Animation() {
			@Override
			public Vector3f getInitialVelocity(TESParticle<?> particle, Vector3f position, RandomSource random) {
				position.add((float)random.nextGaussian() * 0.05f, 0.4f, (float)random.nextGaussian() * 0.05f);

				return new Vector3f(0, 0.2f, 0);
			}

			@Override
			public void perTickModifier(TESParticle<?> particle, int lifetime, Vector3f pos, Vector3f prevPos, Vector3f velocity, RandomSource random) {
				velocity.sub(0, 0.02f, 0);

				if (velocity.y() < 0)
					velocity.mul(1, 0.5f, 1);

				pos.add(velocity);
			}
		};
	}
}

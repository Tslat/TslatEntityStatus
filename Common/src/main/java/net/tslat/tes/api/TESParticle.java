package net.tslat.tes.api;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.tslat.tes.api.util.TESClientUtil;
import org.lwjgl.opengl.GL11;

import java.util.Random;

/**
 * Base interface for TES particles.<br>
 * @see net.tslat.tes.core.particle.type.GenericTESParticle GenericTESParticle
 */
public interface TESParticle<D> {
	/**
	 * Receive and/or update the particle's data
	 */
	void updateData(D data);

	/**
	 * Render the particle.<br>
	 * The implementing class is responsible for positioning and validating the particle prior to rendering.<br>
	 * The {@link PoseStack} has already been transformed relative to the player's camera at this stage.
	 */
	void render(PoseStack poseStack, Minecraft mc, Font fontRenderer, float partialTick);

	/**
	 * Tick the particle (if required).<br>
	 * Can be used for uniformly moving or otherwise modifying the particle.
	 */
	void tick(Minecraft mc);

	/**
	 * Whether the particle is still valid or not.<br>
	 * Returning false here will have the particle removed from rendering before the next render cycle.
	 */
	boolean isValid();

	/**
	 * Create a defaulted render environment then perform a render operation.<br>
	 * This method handles the translations and rotations required to render text in-world
	 */
	default void defaultedTextRender(Minecraft mc, PoseStack poseStack, Vector3f prevPos, Vector3f pos, float partialTick, Runnable renderCallback) {
		float scale = 0.035f * TESAPI.getConfig().getParticleScale();
		Camera camera = mc.gameRenderer.getMainCamera();
		Vector3f renderPos = prevPos.copy();

		renderPos.lerp(pos, partialTick);
		renderPos.sub(new Vector3f(camera.getPosition()));

		poseStack.pushPose();
		poseStack.translate(renderPos.x(), renderPos.y(), renderPos.z());
		TESClientUtil.positionFacingCamera(poseStack);
		poseStack.mulPose(Vector3f.ZP.rotationDegrees(180));
		poseStack.scale(scale, scale, scale);

		RenderSystem.enableBlend();
		RenderSystem.enableDepthTest();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

		renderCallback.run();

		RenderSystem.disableBlend();
		poseStack.popPose();
	}

	/**
	 * Animation handler abstract class for TES Particles.<br>
	 * Provide your own implementation for custom animation
	 */
	abstract class Animation {
		public static final Animation POP_OFF = new Animation() {
			@Override
			public Vector3f getInitialVelocity(TESParticle<?> particle, Vector3f position, Random random) {
				return new Vector3f((float)random.nextGaussian() * 0.03f + 0.025f, random.nextFloat() * 0.035f + 0.37f, (float)random.nextGaussian() * 0.03f + 0.025f);
			}

			@Override
			public void perTickModifier(TESParticle<?> particle, int lifetime, Vector3f pos, Vector3f prevPos, Vector3f velocity, Random random) {
				velocity.sub(new Vector3f(0, 0.05f, 0));
				pos.add(velocity);
			}
		};
		public static final Animation RISE = new Animation() {
			@Override
			public Vector3f getInitialVelocity(TESParticle<?> particle, Vector3f position, Random random) {
				position.add((float)random.nextGaussian() * 0.05f, 0.4f, (float)random.nextGaussian() * 0.05f);

				return new Vector3f(0, 0.2f, 0);
			}

			@Override
			public void perTickModifier(TESParticle<?> particle, int lifetime, Vector3f pos, Vector3f prevPos, Vector3f velocity, Random random) {
				velocity.sub(new Vector3f(0, 0.02f, 0));

				if (velocity.y() < 0)
					velocity.mul(1, 0.5f, 1);

				pos.add(velocity);
			}
		};

		/**
		 * Provide the initial velocity vector for the particle
		 */
		public abstract Vector3f getInitialVelocity(TESParticle<?> particle, Vector3f position, Random random);

		/**
		 * A per-tick access for the animator, allowing for dynamic control
		 */
		public abstract void perTickModifier(TESParticle<?> particle, int lifetime, Vector3f pos, Vector3f prevPos, Vector3f velocity, Random random);
	}
}

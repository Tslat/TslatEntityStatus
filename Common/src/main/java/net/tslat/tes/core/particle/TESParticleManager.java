package net.tslat.tes.core.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.tslat.tes.api.TESAPI;
import net.tslat.tes.api.TESParticle;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Lightweight 'particle' manager for TES particles
 */
public final class TESParticleManager {
	private static final ConcurrentLinkedQueue<TESParticle<?>> PARTICLES = new ConcurrentLinkedQueue<>();

	/**
	 * Add a {@link TESParticle} to the particle manager, for rendering and handling
	 */
	public static void addParticle(TESParticle<?> particle) {
		if (!TESAPI.getConfig().particlesEnabled())
			return;

		PARTICLES.add(particle);
	}

	public static void tick() {
		Minecraft mc = Minecraft.getInstance();

		if (!TESAPI.getConfig().particlesEnabled()) {
			PARTICLES.clear();

			return;
		}

		if (!mc.isPaused()) {
			PARTICLES.forEach(particle -> particle.tick(mc));
			PARTICLES.removeIf(particle -> !particle.isValid());
		}
	}

	public static void render(PoseStack poseStack, float partialTick) {
		Minecraft mc = Minecraft.getInstance();
		Font fontRenderer = mc.font;

		PARTICLES.forEach(particle -> particle.render(poseStack, mc, fontRenderer, partialTick));
	}
}

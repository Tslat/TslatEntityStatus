package net.tslat.tes.core.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.tslat.tes.api.TESAPI;
import net.tslat.tes.api.object.TESHudRenderContext;
import net.tslat.tes.api.object.TESParticle;
import net.tslat.tes.core.state.EntityState;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

/**
 * Lightweight 'particle' manager for TES particles
 */
public final class TESParticleManager {
	private static final ConcurrentLinkedQueue<TESParticle<?>> PARTICLES = new ConcurrentLinkedQueue<>();
	private static final Object2ObjectOpenHashMap<Identifier, TESParticleClaimant> CLAIMANTS = new Object2ObjectOpenHashMap<>();
	private static final ConcurrentMap<Integer, List<Pair<Identifier, Optional<CompoundTag>>>> CLAIMS = new ConcurrentHashMap<>();
	private static final ObjectArrayList<TESParticleSourceHandler> HANDLERS = new ObjectArrayList<>();
	private static final ObjectArrayList<Runnable> NEW_CLAIMS = new ObjectArrayList<>();

	/**
	 * Add a {@link TESParticle} to the particle manager, for rendering and handling
	 */
	public static void addParticle(TESParticle<?> particle) {
		if (!TESAPI.getConfig().particlesEnabled())
			return;

		PARTICLES.add(particle);
	}

	/**
	 * Register a {@link TESParticleClaimant} with TES for receiving custom particle claims
	 */
	public static void registerParticleClaimant(Identifier id, TESParticleClaimant claimant) {
		synchronized (CLAIMANTS) {
			CLAIMANTS.put(id, claimant);
		}
	}

	/**
	 * Register a {@link TESParticleSourceHandler} with TES for handling custom {@link net.minecraft.world.damagesource.DamageSource DamageSource}-based particles
	 */
	public static void registerParticleSourceHandler(TESParticleSourceHandler handler) {
		synchronized (HANDLERS) {
			HANDLERS.add(handler);
		}
	}

	/**
	 * Add a particle claim for the next tick for custom particle handling.<br>
	 * Must have a {@link TESParticleManager#registerParticleClaimant registered} {@link TESParticleClaimant} with the same ID to be able to receive the claim
	 * @param entityId The id of the entity to claim particles for
	 * @param claimantId The id of the claimant responsible for the claim
	 * @param data Optional additional data relevant to the claim
	 */
	public static void addParticleClaim(int entityId, Identifier claimantId, Optional<CompoundTag> data) {
		if (!TESAPI.getConfig().particlesEnabled())
			return;

		synchronized (NEW_CLAIMS) {
			NEW_CLAIMS.add(() -> CLAIMS.computeIfAbsent(entityId, key -> new ObjectArrayList<>()).add(Pair.of(claimantId, data)));
		}
	}

	public static float handleParticleClaims(EntityState entityState, float healthDelta, Consumer<TESParticle<?>> particleAdder, boolean checkSourceHandlers) {
		for (Pair<Identifier, Optional<CompoundTag>> pair : CLAIMS.getOrDefault(entityState.getEntity().map(Entity::getId).orElse(-1), List.of())) {
			healthDelta = CLAIMANTS.getOrDefault(pair.getFirst(), (state, delta, data, adder) -> delta).checkClaim(entityState, healthDelta, pair.getSecond(), particleAdder);

			if (healthDelta == 0)
				break;
		}

		if (checkSourceHandlers && healthDelta < 0) {
			for (TESParticleSourceHandler handler : HANDLERS) {
				if (handler.checkIncomingDamage(entityState, -healthDelta, entityState.getEntity().map(LivingEntity::getLastDamageSource).orElse(null), particleAdder)) {
					healthDelta = 0;

					break;
				}
			}
		}

		return healthDelta;
	}

	public static void tick() {
		Minecraft mc = Minecraft.getInstance();

		if (!mc.isPaused() && mc.level != null && !mc.level.tickRateManager().isFrozen()) {
			if (!TESAPI.getConfig().particlesEnabled()) {
				PARTICLES.clear();
				CLAIMS.clear();

				return;
			}

			PARTICLES.forEach(particle -> particle.tick(mc));
			PARTICLES.removeIf(particle -> !particle.isValid());
		}
	}

	public static void clearClaims() {
		CLAIMS.clear();
		NEW_CLAIMS.forEach(Runnable::run);
		NEW_CLAIMS.clear();
	}

	public static void render(PoseStack poseStack, SubmitNodeCollector renderTasks, CameraRenderState cameraRenderState) {
		final Minecraft mc = Minecraft.getInstance();
		final Font fontRenderer = mc.font;
        final float partialTick = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false);
        final TESHudRenderContext.InWorldArgs args = new TESHudRenderContext.InWorldArgs(poseStack, renderTasks, cameraRenderState, partialTick, LightTexture.FULL_BRIGHT);

		PARTICLES.forEach(particle -> particle.submitRender(args, mc, fontRenderer));
	}
}

package net.tslat.tslatentitystatus.core.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Brightness;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.tslat.tslatentitystatus.api.TESConstants;
import net.tslat.tslatentitystatus.api.client.object.TESHudRenderContext;
import net.tslat.tslatentitystatus.api.client.object.TESParticle;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

/// Lightweight 'particle' manager for TES particles
@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public final class TESParticleManager {
	private static final Queue<TESParticle<?>> PARTICLES = new ConcurrentLinkedQueue<>();
	private static final Map<Identifier, TESParticleClaimant> CLAIMANTS = new ConcurrentHashMap<>();
	private static final Int2ObjectMap<List<Pair<Identifier, @Nullable CompoundTag>>> CLAIMS = Int2ObjectMaps.synchronize(new Int2ObjectOpenHashMap<>());
	private static final List<TESParticleSourceHandler> HANDLERS = ObjectLists.synchronize(new ObjectArrayList<>());
	private static final Queue<Runnable> NEW_CLAIMS = new ConcurrentLinkedQueue<>();

	/// @return Whether TES particles are enabled as a whole
	public static boolean particlesEnabled() {
		return TESConstants.getConfig().particlesEnabled();
	}

	/// Add a [TESParticle] to the particle manager, for rendering and handling
	public static void addParticle(TESParticle<?> particle) {
		if (particlesEnabled())
			PARTICLES.add(particle);
	}

	/// Register a [TESParticleClaimant] with TES for receiving custom particle claims
	public static void registerParticleClaimant(Identifier id, TESParticleClaimant claimant) {
		CLAIMANTS.put(id, claimant);
	}

	/// Register a [TESParticleSourceHandler] with TES for handling custom [`DamageSource`][DamageSource]-based particles
	public static void registerParticleSourceHandler(TESParticleSourceHandler handler) {
		HANDLERS.add(handler);
	}

	/// Add a particle claim for the next tick for custom particle handling.
	///
	/// Must have a [`registered`][TESParticleManager#registerParticleClaimant] [TESParticleClaimant] with the same ID to be able to receive the claim
	///
	/// @param entityId 	The id of the entity to claim particles for
	/// @param claimantId 	The id of the claimant responsible for the claim
	/// @param data 		Optional additional data relevant to the claim
	public static void addParticleClaim(int entityId, Identifier claimantId, @Nullable CompoundTag data) {
		if (particlesEnabled())
			NEW_CLAIMS.add(() -> CLAIMS.computeIfAbsent(entityId, _ -> new ObjectArrayList<>()).add(Pair.of(claimantId, data)));
	}

	//<editor-fold defaultstate="collapsed" desc="<Internal Handling>">
	/// Account for custom-submitted [TESParticleClaimant]s and [TESParticleSourceHandler] claims for a health-delta for the given entity
	///
	/// This allows for third-party mods to claim part or all of an entity's health change with custom particles or handling
	///
	/// @return The remaining unclaimed health delta after accounting for potential third-party claims
	public static float handleParticleClaims(LivingEntity entity, float healthDelta, Consumer<TESParticle<?>> particleAdder, boolean checkSourceHandlers) {
		if (!NEW_CLAIMS.isEmpty()) {
			NEW_CLAIMS.forEach(Runnable::run);
			NEW_CLAIMS.clear();
		}

		for (Pair<Identifier, @Nullable CompoundTag> pair : CLAIMS.getOrDefault(entity.getId(), List.of())) {
			final TESParticleClaimant claimant = CLAIMANTS.get(pair.getFirst());

			if (claimant != null) {
				healthDelta = claimant.checkClaim(entity, healthDelta, pair.getSecond(), particleAdder);

				if (healthDelta == 0)
					break;
			}
		}

		if (checkSourceHandlers && healthDelta < 0) {
			for (TESParticleSourceHandler handler : HANDLERS) {
				if (handler.checkIncomingDamage(entity, -healthDelta, entity.getLastDamageSource(), particleAdder)) {
					healthDelta = 0;

					break;
				}
			}
		}

		return healthDelta;
	}

	public static void tick() {
		final Minecraft mc = Minecraft.getInstance();

		if (!mc.isPaused() && mc.level != null && !mc.level.tickRateManager().isFrozen()) {
			if (!particlesEnabled()) {
				PARTICLES.clear();
				CLAIMS.clear();
				NEW_CLAIMS.clear();

				return;
			}

			PARTICLES.removeIf(particle -> {
				particle.tick(mc);

				return !particle.isValid();
			});
		}
	}

	public static void submitRenderTasks(PoseStack poseStack, SubmitNodeCollector renderTasks, CameraRenderState cameraRenderState) {
		if (!particlesEnabled())
			return;
		
		final Minecraft mc = Minecraft.getInstance();
		final Font fontRenderer = mc.font;
        final float partialTick = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false);
        final TESHudRenderContext.InWorldArgs args = new TESHudRenderContext.InWorldArgs(poseStack, renderTasks, cameraRenderState, partialTick, Brightness.FULL_BRIGHT.pack());

		PARTICLES.forEach(particle -> particle.submitRender(args, mc, fontRenderer));
	}
	//</editor-fold>
}

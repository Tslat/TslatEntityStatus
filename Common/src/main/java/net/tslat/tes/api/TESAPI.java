package net.tslat.tes.api;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.tslat.tes.api.object.TESHudElement;
import net.tslat.tes.api.object.TESParticle;
import net.tslat.tes.core.hud.TESHud;
import net.tslat.tes.core.particle.TESParticleClaimant;
import net.tslat.tes.core.particle.TESParticleManager;
import net.tslat.tes.core.particle.TESParticleSourceHandler;
import net.tslat.tes.core.particle.type.ComponentParticle;
import net.tslat.tes.core.particle.type.NumericParticle;
import net.tslat.tes.core.state.EntityState;
import net.tslat.tes.core.state.TESEntityTracking;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Optional;

/**
 * Public-facing API class for TES.
 * <p>
 * Contains most of the commonly used access functions for ease-of-use and code-stability.
 */
public final class TESAPI {
	/**
	 * Add a {@link TESParticle} to the TES Particle manager.
	 * <p>
	 * This method should only be called <u><b>client-side</b></u>
	 * <p>
	 * The particle itself is responsible for rendering and its own validity/lifespan.
	 *
	 * @param particle The particle to add
	 */
	public static void addTESParticle(TESParticle<?> particle) {
		TESParticleManager.addParticle(particle);
	}

	/**
	 * Add a {@link TESHudElement HUD element} to the {@link TESHud TES HUD manager}.
	 * <p>
	 * The element will be called to render at each render frame, with its position pre-adjusted.
	 * <p>
	 * Generally, HUD elements should be added at mod construct
	 *
	 * @param id The id of the element to add
	 * @param element The element instance to add to the HUD
	 */
	public static void addTESHudElement(ResourceLocation id, TESHudElement element) {
		TESHud.addHudElement(id.toString(), element);
	}

	/**
	 * Remove an existing {@link TESHudElement HUD Element} from the {@link TESHud TES HUD manager}, if present
	 *
	 * @param id The id of the element to remove
	 * @return true if the element was present
	 */
	public static boolean removeTESHudElement(ResourceLocation id) {
		return TESHud.removeHudElement(id.toString());
	}

	/**
	 * Register a {@link TESParticleClaimant} with TES for handling custom claims.
	 * <p>
	 * This allows for overriding damage particles dynamically or doing other similar things
	 *
	 * @param id The id of the claimant to register
	 * @param claimant The claimant instance
	 */
	public static void registerParticleClaimant(ResourceLocation id, TESParticleClaimant claimant) {
		TESParticleManager.registerParticleClaimant(id, claimant);
	}

	/**
	 * Register a {@link TESParticleSourceHandler TESParticleSourceHandler} with TES for custom handling of damage-based {@link TESParticle TESParticles} predicated by their {@link net.minecraft.world.damagesource.DamageSource}
	 * <p>
	 * This can be used to more reliably special-handle damage particles for specific DamageSources (such as freezing damage, fire damage, etc)
	 */
	public static void registerParticleSourceHandler(TESParticleSourceHandler handler) {
		TESParticleManager.registerParticleSourceHandler(handler);
	}

	/**
	 * Get the current config for TES.
	 * <p>
	 * Config is only available on the client side
	 */
	@Nullable
	public static TESConfig getConfig() {
		return TESConstants.CONFIG;
	}

	/**
	 * Get TES' current HUD target entity if it has one
	 */
	@Nullable
	public static LivingEntity getCurrentHUDTarget() {
		return TESHud.getTargetEntity();
	}

	/**
	 * Get TES' current status data for a given entity if it has one
	 */
	@Nullable
	public static EntityState getTESDataForEntity(LivingEntity entity) {
		return getTESDataForEntity(entity.getId());
	}

	/**
	 * Get TES' current status data for a given entity if it has one
	 */
	@Nullable
	public static EntityState getTESDataForEntity(int entityId) {
		return TESEntityTracking.getStateForEntityId(entityId);
	}

	/**
	 * Submit a particle claim to the particle manager for the next/upcoming tick.
	 * <p>
	 * If the target entity has a health change next tick, your claimant will be called with the relevant info
	 *
	 * @param id The id of the claimant responsible for handling the claim
	 * @param targetEntity The entity the claim is for
	 * @param additionalData Optional additional data passed back to the claimant at the time of the claim
	 */
	public static void submitParticleClaim(ResourceLocation id, LivingEntity targetEntity, Optional<CompoundTag> additionalData) {
		if (TESConstants.IS_SERVER_SIDE) {
			TESConstants.NETWORKING.sendParticleClaim(id, targetEntity, additionalData);
		}
		else {
			TESParticleManager.addParticleClaim(targetEntity.getId(), id, additionalData);
		}
	}

	/**
	 * Add a {@link TESParticle TESParticle} for the given position
	 *
	 * @param level The level the particle is in
	 * @param position The position the particle should appear at
	 * @param contents The contents of the particle. If using a numeric value, use one of the double-based methods
	 */
	public static void addTESParticle(ServerLevel level, Vector3f position, Component contents) {
		if (TESConstants.IS_SERVER_SIDE) {
			TESConstants.NETWORKING.sendParticle(level, position, contents);
		}
		else {
			TESParticleManager.addParticle(new ComponentParticle(null, position, contents));
		}
	}

	/**
	 * Add a {@link TESParticle TESParticle} for the given entity
	 *
	 * @param targetedEntity The entity the particle should appear on
	 * @param contents The contents of the particle. If using a numeric value, use one of the double-based methods
	 */
	public static void addTESParticle(LivingEntity targetedEntity, Component contents) {
		if (TESConstants.IS_SERVER_SIDE) {
			TESConstants.NETWORKING.sendParticle(targetedEntity, contents);
		}
		else {
			EntityState entityState = getTESDataForEntity(targetedEntity);

			if (entityState != null)
				TESParticleManager.addParticle(new ComponentParticle(entityState, targetedEntity.getEyePosition().toVector3f(), contents));
		}
	}

	/**
	 * Add a {@link TESParticle TESParticle} for the given position
	 *
	 * @param level The level the particle is in
	 * @param position The position the particle should appear at
	 * @param value    The value of the particle
	 * @param colour   The text colour of the particle
	 */
	public static void sendParticle(ServerLevel level, Vector3f position, double value, int colour) {
		if (TESConstants.IS_SERVER_SIDE) {
			TESConstants.NETWORKING.sendParticle(level, position, value, colour);
		}
		else {
			TESParticleManager.addParticle(new NumericParticle(null, position, value).withColour(colour));
		}
	}

	/**
	 * Add a {@link TESParticle TESParticle} for the given entity
	 *
	 * @param targetedEntity The entity the particle should appear on
	 * @param value The value of the particle
	 * @param colour The text colour of the particle
	 */
	public static void sendParticle(LivingEntity targetedEntity, double value, int colour) {
		if (TESConstants.IS_SERVER_SIDE) {
			TESConstants.NETWORKING.sendParticle(targetedEntity, value, colour);
		}
		else {
			EntityState entityState = getTESDataForEntity(targetedEntity);

			if (entityState != null)
				TESParticleManager.addParticle(new NumericParticle(entityState, targetedEntity.getEyePosition().toVector3f(), value).withColour(colour));
		}
	}
}

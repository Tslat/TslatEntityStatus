package net.tslat.tes.api;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.tslat.tes.core.hud.TESHud;
import net.tslat.tes.core.particle.TESParticleClaimant;
import net.tslat.tes.core.particle.TESParticleManager;
import net.tslat.tes.core.state.EntityState;
import net.tslat.tes.core.state.TESEntityTracking;

import javax.annotation.Nullable;

/**
 * Public-facing API class for TES.<br>
 * Contains most of the commonly-used access functions for ease-of-use and code-stability.
 */
public final class TESAPI {
	/**
	 * Add a {@link TESParticle} to the TES Particle manager.<br>
	 * TES handles damage and healing natively, so this method should only really be necessary
	 * for custom-typed particles.<br>
	 * The particle itself is responsible for rendering and its own validity/lifespan.
	 * @param particle The particle to add
	 */
	public static void addTESParticle(TESParticle<?> particle) {
		TESParticleManager.addParticle(particle);
	}

	/**
	 * Add a {@link TESHudElement HUD element} to the {@link TESHud TES HUD manager}.<br>
	 * The element will be called to render at each render frame, with its position pre-adjusted.<br>
	 * <br>
	 * Generally, HUD elements should be added at mod construct
	 * @param id The id of the element to add
	 * @param element The element instance to add to the HUD
	 */
	public static void addTESHudElement(ResourceLocation id, TESHudElement element) {
		TESHud.addHudElement(id.toString(), element);
	}

	/**
	 * Remove an existing {@link TESHudElement HUD Element} from the {@link TESHud TES HUD manager}, if present
	 * @param id The id of the element to remove
	 * @return true if the element was present
	 */
	public static boolean removeTESHudElement(ResourceLocation id) {
		return TESHud.removeHudElement(id.toString());
	}

	/**
	 * Register a {@link TESParticleClaimant} with TES for handling custom claims.<br>
	 * This allows for overriding damage particles dynamically or doing other similar things
	 * @param id The id of the claimant to register
	 * @param claimant The claimant instance
	 */
	public static void registerParticleClaimant(ResourceLocation id, TESParticleClaimant claimant) {
		TESParticleManager.registerParticleClaimant(id, claimant);
	}

	/**
	 * Get the current config for TES
	 */
	public static TESConfig getConfig() {
		return TESConstants.CONFIG;
	}

	/**
	 * Get TES' current HUD target entity, if it has one
	 */
	@Nullable
	public static LivingEntity getCurrentHUDTarget() {
		return TESHud.getTargetEntity();
	}

	/**
	 * Get TES' current status data for a given entity, if it has one
	 */
	@Nullable
	public static EntityState getTESDataForEntity(LivingEntity entity) {
		return getTESDataForEntity(entity.getId());
	}

	/**
	 * Get TES' current status data for a given entity, if it has one
	 */
	@Nullable
	public static EntityState getTESDataForEntity(int entityId) {
		return TESEntityTracking.getStateForEntityId(entityId);
	}

	/**
	 * Submit a particle claim to the particle manager for the next/upcoming tick.<br>
	 * If the target entity has a health change next tick, your claimant will be called with the relevant info
	 * @param id The id of the claimant responsible for handling the claim
	 * @param targetEntity The entity the claim is for
	 * @param additionalData Optional additional data passed back to the claimant at the time of the claim
	 */
	public static void submitParticleClaim(ResourceLocation id, LivingEntity targetEntity, @Nullable CompoundTag additionalData) {
		if (TESConstants.IS_SERVER_SIDE) {
			TESConstants.NETWORKING.sendParticleClaim(id, targetEntity, additionalData);
		}
		else {
			TESParticleManager.addParticleClaim(targetEntity.getId(), id, additionalData);
		}
	}
}

package net.tslat.tes.core.networking;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Set;

/**
 * Base interface for TES' networking functionality.<br>
 * This is only functional if TES is installed on the server.<br>
 * Access this from {@link net.tslat.tes.api.TESConstants#NETWORKING TESConstants.NETWORKING}
 */
public interface TESNetworking {
	/**
	 * Request an update for {@link net.minecraft.world.effect.MobEffect MobEffects} for a given entity<br>
	 * Network direction: (CLIENT -> SERVER)
	 */
	void requestEffectsSync(int entityId);

	/**
	 * Send an update for {@link net.minecraft.world.effect.MobEffect MobEffects} for a given entity to a specific player (usually in response to a {@link TESNetworking#requestEffectsSync prior request}<br>
	 * Network direction: SERVER -> CLIENT
	 * @param player The player to send to
	 * @param entityId The id of the entity to update
	 * @param toAdd The effects to add to the entity's state on the client side (usually all of them)
	 * @param toRemove The effects to remove from the entity's state on the client side (usually empty)
	 */
	void sendEffectsSync(ServerPlayer player, int entityId, Set<ResourceLocation> toAdd, Set<ResourceLocation> toRemove);

	/**
	 * Send an update for {@link net.minecraft.world.effect.MobEffect MobEffects} to all players tracking the given entity. Usually as part of an effect being added/removed<br>
	 * Network direction: SERVER -> CLIENT
	 * @param targetedEntity The entity to update for
	 * @param toAdd The effects to add to the entity's state on the client side
	 * @param toRemove The effects to remove from the entity's state on the client side
	 */
	void sendEffectsSync(LivingEntity targetedEntity, Set<ResourceLocation> toAdd, Set<ResourceLocation> toRemove);

	/**
	 * Send a {@link net.tslat.tes.api.TESParticle TESParticle} for the given position<br>
	 * Network direction: SERVER -> CLIENT
	 * @param level The level the particle is in
	 * @param position The position the particle should appear at
	 * @param contents The contents of the particle. If sending a numeric value, use one of the double-based methods
	 */
	void sendParticle(Level level, Vector3f position, Component contents);

	/**
	 * Send a {@link net.tslat.tes.api.TESParticle TESParticle} for the given entity<br>
	 * Network direction: SERVER -> CLIENT
	 * @param targetedEntity The entity the particle should appear on
	 * @param contents The contents of the particle. If sending a numeric value, use one of the double-based methods
	 */
	void sendParticle(LivingEntity targetedEntity, Component contents);

	/**
	 * Send a {@link net.tslat.tes.api.TESParticle TESParticle} for the given position<br>
	 * Network direction: SERVER -> CLIENT
	 * @param level The level the particle is in
	 * @param position The position the particle should appear at
	 * @param value    The value of the particle
	 * @param colour   The text colour of the particle
	 */
	void sendParticle(Level level, Vector3f position, double value, int colour);

	/**
	 * Send a {@link net.tslat.tes.api.TESParticle TESParticle} for the given entity<br>
	 * Network direction: SERVER -> CLIENT
	 * @param targetedEntity The entity the particle should appear on
	 * @param value The value of the particle
	 * @param colour The text colour of the particle
	 */
	void sendParticle(LivingEntity targetedEntity, double value, int colour);

	/**
	 * Submit a particle claim for the next/upcoming tick<br>
	 * Network direction: SERVER -> CLIENT
	 * @param claimantId The id of the {@link net.tslat.tes.core.particle.TESParticleClaimant claimant} to handle the claim
	 * @param targetedEntity The entity for the claim
	 * @param additionalData Optional additional data for the claim
	 */
	void sendParticleClaim(ResourceLocation claimantId, LivingEntity targetedEntity, @Nullable CompoundTag additionalData);
}

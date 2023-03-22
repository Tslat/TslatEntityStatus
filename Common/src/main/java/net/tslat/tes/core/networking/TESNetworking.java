package net.tslat.tes.core.networking;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

import java.util.Set;

/**
 * Base interface for TES' networking functionality.<br>
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
}

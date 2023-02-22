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
	void requestEffectsSync(int entityId);
	void sendEffectsSync(ServerPlayer player, int entityId, Set<ResourceLocation> toAdd, Set<ResourceLocation> toRemove);
	void sendEffectsSync(LivingEntity targetedEntity, Set<ResourceLocation> toAdd, Set<ResourceLocation> toRemove);
}

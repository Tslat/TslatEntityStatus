package net.tslat.tes.networking;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

import java.util.Set;

public class TESNetworking implements net.tslat.tes.core.networking.TESNetworking {
	private static boolean EFFECTS_SYNCING_ENABLED = true;

	public static void init() {

	}

	public static boolean isSyncingEffects() {
		return EFFECTS_SYNCING_ENABLED;
	}

	@Override
	public void requestEffectsSync(int entityId) {
		if (!isSyncingEffects())
			return;
	}

	@Override
	public void sendEffectsSync(ServerPlayer player, int entityId, Set<ResourceLocation> toAdd, Set<ResourceLocation> toRemove) {

	}

	@Override
	public void sendEffectsSync(LivingEntity targetedEntity, Set<ResourceLocation> toAdd, Set<ResourceLocation> toRemove) {

	}
}

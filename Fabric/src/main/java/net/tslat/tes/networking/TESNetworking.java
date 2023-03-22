package net.tslat.tes.networking;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.tslat.tes.TESClient;
import net.tslat.tes.api.TESAPI;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class TESNetworking implements net.tslat.tes.core.networking.TESNetworking {
	public static boolean isSyncingEffects() {
		return TESAPI.getConfig().hudPotionIcons() || TESAPI.getConfig().inWorldHudPotionIcons();
	}

	@Override
	public void requestEffectsSync(int entityId) {
		if (!isSyncingEffects())
			return;

		FriendlyByteBuf buffer = PacketByteBufs.create();

		new RequestEffectsPacket(entityId).encode(buffer);

		TESClient.sendPacket(RequestEffectsPacket.ID, buffer);
	}

	@Override
	public void sendEffectsSync(ServerPlayer player, int entityId, Set<ResourceLocation> toAdd, Set<ResourceLocation> toRemove) {
		if (!isSyncingEffects())
			return;

		FriendlyByteBuf buffer = PacketByteBufs.create();

		new SyncEffectsPacket(entityId, toAdd, toRemove).encode(buffer);

		ServerPlayNetworking.send(player, SyncEffectsPacket.ID, buffer);
	}

	@Override
	public void sendEffectsSync(LivingEntity targetedEntity, Set<ResourceLocation> toAdd, Set<ResourceLocation> toRemove) {
		if (!isSyncingEffects())
			return;

		FriendlyByteBuf buffer = PacketByteBufs.create();

		new SyncEffectsPacket(targetedEntity.getId(), toAdd, toRemove).encode(buffer);

		for (ServerPlayer player : PlayerLookup.tracking(targetedEntity)) {
			ServerPlayNetworking.send(player, SyncEffectsPacket.ID, buffer);
		}
	}

	@Override
	public void sendParticleClaim(ResourceLocation claimantId, LivingEntity targetedEntity, @Nullable CompoundTag additionalData) {
		FriendlyByteBuf buffer = PacketByteBufs.create();

		new ParticleClaimPacket(targetedEntity.getId(), claimantId, additionalData).encode(buffer);

		for (ServerPlayer player : PlayerLookup.tracking(targetedEntity)) {
			ServerPlayNetworking.send(player, ParticleClaimPacket.ID, buffer);
		}
	}
}

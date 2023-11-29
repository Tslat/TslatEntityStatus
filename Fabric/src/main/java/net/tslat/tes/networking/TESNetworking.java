package net.tslat.tes.networking;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.tslat.tes.TESClient;
import net.tslat.tes.api.TESAPI;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Set;

public class TESNetworking implements net.tslat.tes.core.networking.TESNetworking {
	@Override
	public void requestEffectsSync(int entityId) {
		if (!TESAPI.getConfig().isSyncingEffects())
			return;

		FriendlyByteBuf buffer = PacketByteBufs.create();

		new RequestEffectsPacket(entityId).encode(buffer);

		TESClient.sendPacket(RequestEffectsPacket.ID, buffer);
	}

	@Override
	public void sendEffectsSync(ServerPlayer player, int entityId, Set<ResourceLocation> toAdd, Set<ResourceLocation> toRemove) {
		FriendlyByteBuf buffer = PacketByteBufs.create();

		new SyncEffectsPacket(entityId, toAdd, toRemove).encode(buffer);

		ServerPlayNetworking.send(player, SyncEffectsPacket.ID, buffer);
	}

	@Override
	public void sendEffectsSync(LivingEntity targetedEntity, Set<ResourceLocation> toAdd, Set<ResourceLocation> toRemove) {
		FriendlyByteBuf buffer = PacketByteBufs.create();

		new SyncEffectsPacket(targetedEntity.getId(), toAdd, toRemove).encode(buffer);

		for (ServerPlayer player : PlayerLookup.tracking(targetedEntity)) {
			ServerPlayNetworking.send(player, SyncEffectsPacket.ID, buffer);
		}
	}

	@Override
	public void sendParticle(Level level, Vector3f position, Component contents) {
		FriendlyByteBuf buffer = PacketByteBufs.create();

		new NewComponentParticlePacket(position, contents).encode(buffer);

		for (ServerPlayer player : PlayerLookup.tracking((ServerLevel)level, BlockPos.containing(position.x, position.y, position.z))) {
			ServerPlayNetworking.send(player, NewComponentParticlePacket.ID, buffer);
		}
	}

	@Override
	public void sendParticle(LivingEntity targetedEntity, Component contents) {
		FriendlyByteBuf buffer = PacketByteBufs.create();

		new NewComponentParticlePacket(targetedEntity, contents).encode(buffer);

		for (ServerPlayer player : PlayerLookup.tracking(targetedEntity)) {
			ServerPlayNetworking.send(player, NewComponentParticlePacket.ID, buffer);
		}
	}

	@Override
	public void sendParticle(Level level, Vector3f position, double value, int colour) {
		FriendlyByteBuf buffer = PacketByteBufs.create();

		new NewNumericParticlePacket(value, position, colour).encode(buffer);

		for (ServerPlayer player : PlayerLookup.tracking((ServerLevel)level, BlockPos.containing(position.x, position.y, position.z))) {
			ServerPlayNetworking.send(player, NewNumericParticlePacket.ID, buffer);
		}
	}

	@Override
	public void sendParticle(LivingEntity targetedEntity, double value, int colour) {
		FriendlyByteBuf buffer = PacketByteBufs.create();

		new NewNumericParticlePacket(value, new Vector3f((float)targetedEntity.getX(), (float)targetedEntity.getEyeY(), (float)targetedEntity.getZ()), colour).encode(buffer);

		for (ServerPlayer player : PlayerLookup.tracking(targetedEntity)) {
			ServerPlayNetworking.send(player, NewNumericParticlePacket.ID, buffer);
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

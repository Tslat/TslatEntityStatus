package net.tslat.tes.networking;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
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
import net.tslat.tes.core.networking.packet.*;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Set;

public class TESNetworking implements net.tslat.tes.core.networking.TESNetworking {
	@Override
	@ApiStatus.Internal
	public <P extends MultiloaderPacket> void registerPacketInternal(ResourceLocation id, boolean isClientBound, Class<P> packetClass, FriendlyByteBuf.Reader<P> decoder) {
		if (isClientBound) {
			if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
				TESClient.registerPacket(id, decoder);
		}
		else {
			ServerPlayNetworking.registerGlobalReceiver(id, (server, player, packetListener, buffer, sender) -> decoder.apply(buffer).receiveMessage(player, server::execute));
		}
	}

	@Override
	public void requestEffectsSync(int entityId) {
		if (!TESAPI.getConfig().isSyncingEffects())
			return;

		FriendlyByteBuf buffer = PacketByteBufs.create();

		new RequestEffectsPacket(entityId).write(buffer);

		TESClient.sendPacket(RequestEffectsPacket.ID, buffer);
	}

	@Override
	public void sendEffectsSync(ServerPlayer player, int entityId, Set<ResourceLocation> toAdd, Set<ResourceLocation> toRemove) {
		FriendlyByteBuf buffer = PacketByteBufs.create();

		new SyncEffectsPacket(entityId, toAdd, toRemove).write(buffer);

		ServerPlayNetworking.send(player, SyncEffectsPacket.ID, buffer);
	}

	@Override
	public void sendEffectsSync(LivingEntity targetedEntity, Set<ResourceLocation> toAdd, Set<ResourceLocation> toRemove) {
		FriendlyByteBuf buffer = PacketByteBufs.create();

		new SyncEffectsPacket(targetedEntity.getId(), toAdd, toRemove).write(buffer);

		for (ServerPlayer player : PlayerLookup.tracking(targetedEntity)) {
			ServerPlayNetworking.send(player, SyncEffectsPacket.ID, buffer);
		}
	}

	@Override
	public void sendParticle(Level level, Vector3f position, Component contents) {
		FriendlyByteBuf buffer = PacketByteBufs.create();

		new NewComponentParticlePacket(position, contents).write(buffer);

		for (ServerPlayer player : PlayerLookup.tracking((ServerLevel)level, BlockPos.containing(position.x, position.y, position.z))) {
			ServerPlayNetworking.send(player, NewComponentParticlePacket.ID, buffer);
		}
	}

	@Override
	public void sendParticle(LivingEntity targetedEntity, Component contents) {
		FriendlyByteBuf buffer = PacketByteBufs.create();

		new NewComponentParticlePacket(targetedEntity, contents).write(buffer);

		for (ServerPlayer player : PlayerLookup.tracking(targetedEntity)) {
			ServerPlayNetworking.send(player, NewComponentParticlePacket.ID, buffer);
		}
	}

	@Override
	public void sendParticle(Level level, Vector3f position, double value, int colour) {
		FriendlyByteBuf buffer = PacketByteBufs.create();

		new NewNumericParticlePacket(value, position, colour).write(buffer);

		for (ServerPlayer player : PlayerLookup.tracking((ServerLevel)level, BlockPos.containing(position.x, position.y, position.z))) {
			ServerPlayNetworking.send(player, NewNumericParticlePacket.ID, buffer);
		}
	}

	@Override
	public void sendParticle(LivingEntity targetedEntity, double value, int colour) {
		FriendlyByteBuf buffer = PacketByteBufs.create();

		new NewNumericParticlePacket(value, new Vector3f((float)targetedEntity.getX(), (float)targetedEntity.getEyeY(), (float)targetedEntity.getZ()), colour).write(buffer);

		for (ServerPlayer player : PlayerLookup.tracking(targetedEntity)) {
			ServerPlayNetworking.send(player, NewNumericParticlePacket.ID, buffer);
		}
	}

	@Override
	public void sendParticleClaim(ResourceLocation claimantId, LivingEntity targetedEntity, @Nullable CompoundTag additionalData) {
		FriendlyByteBuf buffer = PacketByteBufs.create();

		new ParticleClaimPacket(targetedEntity.getId(), claimantId, additionalData).write(buffer);

		for (ServerPlayer player : PlayerLookup.tracking(targetedEntity)) {
			ServerPlayNetworking.send(player, ParticleClaimPacket.ID, buffer);
		}
	}
}

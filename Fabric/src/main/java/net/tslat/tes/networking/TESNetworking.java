package net.tslat.tes.networking;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.tslat.tes.TESClient;
import net.tslat.tes.api.TESAPI;
import net.tslat.tes.core.networking.packet.*;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Vector3f;

import java.util.Optional;
import java.util.Set;

public class TESNetworking implements net.tslat.tes.core.networking.TESNetworking {
	@Override
	@ApiStatus.Internal
	public <B extends FriendlyByteBuf, P extends MultiloaderPacket> void registerPacketInternal(CustomPacketPayload.Type<P> packetType, StreamCodec<B, P> codec, boolean isClientBound) {
		if (isClientBound) {
			if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
				TESClient.registerPacket(packetType, codec);
		}
		else {
			PayloadTypeRegistry.playC2S().register(packetType, (StreamCodec<FriendlyByteBuf, P>)codec);
			ServerPlayNetworking.registerGlobalReceiver(packetType, (packet, context) -> packet.receiveMessage(context.player(), context.player().getServer()::execute));
		}
	}

	@Override
	public void requestEffectsSync(int entityId) {
		if (!TESAPI.getConfig().isSyncingEffects())
			return;

		TESClient.sendPacket(new RequestEffectsPacket(entityId));
	}

	@Override
	public void sendEffectsSync(ServerPlayer player, int entityId, Set<Holder<MobEffect>> toAdd, Set<Holder<MobEffect>> toRemove) {
		ServerPlayNetworking.send(player, new SyncEffectsPacket(entityId, toAdd, toRemove));
	}

	@Override
	public void sendEffectsSync(LivingEntity targetedEntity, Set<Holder<MobEffect>> toAdd, Set<Holder<MobEffect>> toRemove) {
		for (ServerPlayer player : PlayerLookup.tracking(targetedEntity)) {
			ServerPlayNetworking.send(player, new SyncEffectsPacket(targetedEntity.getId(), toAdd, toRemove));
		}
	}

	@Override
	public void sendParticle(ServerLevel level, Vector3f position, Component contents) {
		for (ServerPlayer player : PlayerLookup.tracking((ServerLevel)level, BlockPos.containing(position.x, position.y, position.z))) {
			ServerPlayNetworking.send(player, new NewComponentParticlePacket(position, contents));
		}
	}

	@Override
	public void sendParticle(LivingEntity targetedEntity, Component contents) {
		for (ServerPlayer player : PlayerLookup.tracking(targetedEntity)) {
			ServerPlayNetworking.send(player, new NewComponentParticlePacket(targetedEntity, contents));
		}
	}

	@Override
	public void sendParticle(ServerLevel level, Vector3f position, double value, int colour) {
		for (ServerPlayer player : PlayerLookup.tracking((ServerLevel)level, BlockPos.containing(position.x, position.y, position.z))) {
			ServerPlayNetworking.send(player, new NewNumericParticlePacket(value, position, colour));
		}
	}

	@Override
	public void sendParticle(LivingEntity targetedEntity, double value, int colour) {
		for (ServerPlayer player : PlayerLookup.tracking(targetedEntity)) {
			ServerPlayNetworking.send(player, new NewNumericParticlePacket(value, new Vector3f((float)targetedEntity.getX(), (float)targetedEntity.getEyeY(), (float)targetedEntity.getZ()), colour));
		}
	}

	@Override
	public void sendParticleClaim(ResourceLocation claimantId, LivingEntity targetedEntity, Optional<CompoundTag> additionalData) {
		for (ServerPlayer player : PlayerLookup.tracking(targetedEntity)) {
			ServerPlayNetworking.send(player, new ParticleClaimPacket(targetedEntity.getId(), claimantId, additionalData));
		}
	}
}

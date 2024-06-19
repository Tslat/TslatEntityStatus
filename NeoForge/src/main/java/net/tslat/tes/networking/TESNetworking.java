package net.tslat.tes.networking;

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
import net.neoforged.neoforge.network.PacketDistributor;
import net.tslat.tes.TES;
import net.tslat.tes.core.networking.packet.*;
import org.joml.Vector3f;

import java.util.Optional;
import java.util.Set;

public final class TESNetworking implements net.tslat.tes.core.networking.TESNetworking {
	public TESNetworking() {}

	@Override
	public <B extends FriendlyByteBuf, P extends MultiloaderPacket> void registerPacketInternal(CustomPacketPayload.Type<P> payloadType, StreamCodec<B, P> codec, boolean isClientBound) {
		if (isClientBound) {
			TES.packetRegistrar.playToClient(payloadType, (StreamCodec<FriendlyByteBuf, P>)codec, (packet, context) -> packet.receiveMessage(context.player(), context::enqueueWork));
		}
		else {
			TES.packetRegistrar.playToServer(payloadType, (StreamCodec<FriendlyByteBuf, P>)codec, (packet, context) -> packet.receiveMessage(context.player(), context::enqueueWork));
		}
	}

	@Override
	public void requestEffectsSync(int entityId) {
		PacketDistributor.sendToServer(new RequestEffectsPacket(entityId));
	}

	@Override
	public void sendEffectsSync(ServerPlayer player, int entityId, Set<Holder<MobEffect>> toAdd, Set<Holder<MobEffect>> toRemove) {
		PacketDistributor.sendToPlayer(player, new SyncEffectsPacket(entityId, toAdd, toRemove));
	}

	@Override
	public void sendEffectsSync(LivingEntity targetedEntity, Set<Holder<MobEffect>> toAdd, Set<Holder<MobEffect>> toRemove) {
		PacketDistributor.sendToPlayersTrackingEntity(targetedEntity, new SyncEffectsPacket(targetedEntity.getId(), toAdd, toRemove));
	}

	@Override
	public void sendParticle(ServerLevel level, Vector3f position, Component contents) {
		PacketDistributor.sendToPlayersNear(level, null, position.x, position.y, position.z, 200, new NewComponentParticlePacket(position, contents));
	}

	@Override
	public void sendParticle(LivingEntity targetedEntity, Component contents) {
		PacketDistributor.sendToPlayersTrackingEntity(targetedEntity, new NewComponentParticlePacket(targetedEntity, contents));
	}

	@Override
	public void sendParticle(ServerLevel level, Vector3f position, double value, int colour) {
		PacketDistributor.sendToPlayersNear(level, null, position.x, position.y, position.z, 200, new NewNumericParticlePacket(value, position, colour));
	}

	@Override
	public void sendParticle(LivingEntity targetedEntity, double value, int colour) {
		PacketDistributor.sendToPlayersTrackingEntity(targetedEntity, new NewNumericParticlePacket(value, new Vector3f((float)targetedEntity.getX(), (float)targetedEntity.getEyeY(), (float)targetedEntity.getZ()), colour));
	}

	@Override
	public void sendParticleClaim(ResourceLocation claimantId, LivingEntity targetedEntity, Optional<CompoundTag> additionalData) {
		PacketDistributor.sendToPlayersTrackingEntity(targetedEntity, new ParticleClaimPacket(targetedEntity.getId(), claimantId, additionalData));
	}
}

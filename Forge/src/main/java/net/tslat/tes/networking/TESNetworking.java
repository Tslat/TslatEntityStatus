package net.tslat.tes.networking;

import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.Channel;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.payload.PayloadProtocol;
import net.tslat.tes.api.TESConstants;
import net.tslat.tes.api.util.TESClientUtil;
import net.tslat.tes.core.networking.packet.*;
import org.joml.Vector3f;

import java.util.Optional;
import java.util.Set;

public final class TESNetworking implements net.tslat.tes.core.networking.TESNetworking {
	public static PayloadProtocol<RegistryFriendlyByteBuf, CustomPacketPayload> NETWORK_CHANNEL_BUILDER = ChannelBuilder.named(TESConstants.id("tes_packets")).networkProtocolVersion(0).optional().payloadChannel().play();
	public static Channel<CustomPacketPayload> CHANNEL;

	public TESNetworking() {}

	public static void init() {
		net.tslat.tes.core.networking.TESNetworking.init();

		CHANNEL = NETWORK_CHANNEL_BUILDER.bidirectional().build();
	}

	@Override
	public <B extends FriendlyByteBuf, P extends MultiloaderPacket> void registerPacketInternal(CustomPacketPayload.Type<P> packetType, StreamCodec<B, P> codec, boolean isClientBound, boolean configurationStage) {
		if (configurationStage) {
			(isClientBound ? NETWORK_CHANNEL_BUILDER.configuration().clientbound() : NETWORK_CHANNEL_BUILDER.configuration().serverbound()).add(packetType, (StreamCodec<FriendlyByteBuf, P>)codec, (packet, context) -> {
				packet.receiveMessage(context.getSender() != null ? context.getSender() : TESClientUtil.getClientPlayer(), context::enqueueWork);
				context.setPacketHandled(true);
			});
		}
		else {
			(isClientBound ? NETWORK_CHANNEL_BUILDER.clientbound() : NETWORK_CHANNEL_BUILDER.serverbound()).add(packetType, (StreamCodec<RegistryFriendlyByteBuf, P>)codec, (packet, context) -> {
				packet.receiveMessage(context.getSender() != null ? context.getSender() : TESClientUtil.getClientPlayer(), context::enqueueWork);
				context.setPacketHandled(true);
			});
		}
	}

	@Override
	public void requestEffectsSync(int entityId) {
		CHANNEL.send(new RequestEffectsPacket(entityId), PacketDistributor.SERVER.noArg());
	}

	@Override
	public void sendEffectsSync(ServerPlayer player, int entityId, Set<Holder<MobEffect>> toAdd, Set<Holder<MobEffect>> toRemove) {
		CHANNEL.send(new SyncEffectsPacket(entityId, toAdd, toRemove), PacketDistributor.PLAYER.with(player));
	}

	@Override
	public void sendEffectsSync(LivingEntity targetedEntity, Set<Holder<MobEffect>> toAdd, Set<Holder<MobEffect>> toRemove) {
		CHANNEL.send(new SyncEffectsPacket(targetedEntity.getId(), toAdd, toRemove), PacketDistributor.TRACKING_ENTITY.with(targetedEntity));
	}

	@Override
	public void sendParticle(ServerLevel level, Vector3f position, Component contents) {
		CHANNEL.send(new NewComponentParticlePacket(position, contents), PacketDistributor.NEAR.with(new PacketDistributor.TargetPoint(position.x, position.y, position.z, 200, level.dimension())));
	}

	@Override
	public void sendParticle(LivingEntity targetedEntity, Component contents) {
		CHANNEL.send(new NewComponentParticlePacket(targetedEntity, contents), PacketDistributor.TRACKING_ENTITY.with(targetedEntity));
	}

	@Override
	public void sendParticle(ServerLevel level, Vector3f position, double value, int colour) {
		CHANNEL.send(new NewNumericParticlePacket(value, position, colour), PacketDistributor.NEAR.with(new PacketDistributor.TargetPoint(position.x, position.y, position.z, 200, level.dimension())));
	}

	@Override
	public void sendParticle(LivingEntity targetedEntity, double value, int colour) {
		CHANNEL.send(new NewNumericParticlePacket(value, new Vector3f((float)targetedEntity.getX(), (float)targetedEntity.getEyeY(), (float)targetedEntity.getZ()), colour), PacketDistributor.TRACKING_ENTITY.with(targetedEntity));
	}

	@Override
	public void sendParticleClaim(ResourceLocation claimantId, LivingEntity targetedEntity, Optional<CompoundTag> additionalData) {
		CHANNEL.send(new ParticleClaimPacket(targetedEntity.getId(), claimantId, additionalData), PacketDistributor.TRACKING_ENTITY.with(targetedEntity));
	}
}

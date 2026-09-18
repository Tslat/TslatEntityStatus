package net.tslat.tslatentitystatus.networking;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.Channel;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.payload.PayloadFlow;
import net.minecraftforge.network.payload.PayloadProtocol;
import net.tslat.tslatentitystatus.api.TESConstants;
import net.tslat.tslatentitystatus.api.client.util.TESClientUtil;
import net.tslat.tslatentitystatus.core.networking.packet.*;
import net.tslat.tslatentitystatus.core.networking.packet.base.MultiloaderConfigurationPacket;
import net.tslat.tslatentitystatus.core.networking.packet.base.MultiloaderPacket;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.Set;

@NullMarked
public final class TESForgeNetworking implements net.tslat.tslatentitystatus.core.networking.TESNetworking {
	public static PayloadProtocol<RegistryFriendlyByteBuf, CustomPacketPayload> NETWORK_CHANNEL_BUILDER = ChannelBuilder.named(TESConstants.id("tes_packets")).networkProtocolVersion(0).optional().payloadChannel().play();
	public static Channel<CustomPacketPayload> CHANNEL;

	public TESForgeNetworking() {}

	public static void init() {
		net.tslat.tslatentitystatus.core.networking.TESNetworking.init();

		CHANNEL = NETWORK_CHANNEL_BUILDER.bidirectional().build();
	}

	@SuppressWarnings("unchecked")
    @Override
	@ApiStatus.Internal
	public <B extends FriendlyByteBuf, P extends MultiloaderConfigurationPacket> void registerConfigurationPacketInternal(CustomPacketPayload.Type<P> packetType, StreamCodec<B, P> codec, Direction direction) {
		final PayloadFlow<FriendlyByteBuf, CustomPacketPayload> payloadFlow = switch (direction) {
            case SERVERBOUND -> NETWORK_CHANNEL_BUILDER.configuration().serverbound();
            case CLIENTBOUND -> NETWORK_CHANNEL_BUILDER.configuration().clientbound();
            case BIDIRECTIONAL -> NETWORK_CHANNEL_BUILDER.configuration().bidirectional();
        };

		payloadFlow.add(packetType, (StreamCodec<FriendlyByteBuf, P>)codec, (packet, context) -> {
			packet.handleTask(new MultiloaderConfigurationPacket.TaskHandler(reply -> {
				if (context.isClientSide())
					CHANNEL.send(reply, context.getConnection());
			}, type -> {}));
			context.setPacketHandled(true);
		});
	}

	@SuppressWarnings("unchecked")
    @Override
	public <B extends FriendlyByteBuf, P extends MultiloaderPacket> void registerPacketInternal(CustomPacketPayload.Type<P> packetType, StreamCodec<B, P> codec, Direction direction) {
		final PayloadFlow<RegistryFriendlyByteBuf, CustomPacketPayload> payloadFlow = switch (direction) {
			case SERVERBOUND -> NETWORK_CHANNEL_BUILDER.serverbound();
			case CLIENTBOUND -> NETWORK_CHANNEL_BUILDER.clientbound();
			case BIDIRECTIONAL -> NETWORK_CHANNEL_BUILDER.bidirectional();
		};

		payloadFlow.add(packetType, (StreamCodec<RegistryFriendlyByteBuf, P>)codec, (packet, context) -> {
			packet.receiveMessage(context.getSender() != null ? context.getSender() : TESClientUtil.getClientPlayer(), context::enqueueWork);
			context.setPacketHandled(true);
		});
	}

	@Override
	public void requestEffectsSync(int entityId) {
		if (Minecraft.getInstance().getConnection() != null)
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
	public void sendParticle(ServerLevel level, Vec3 position, Component contents) {
		CHANNEL.send(new NewComponentParticlePacket(position, contents), PacketDistributor.NEAR.with(new PacketDistributor.TargetPoint(position.x, position.y, position.z, 200, level.dimension())));
	}

	@Override
	public void sendParticle(LivingEntity targetedEntity, Component contents) {
		CHANNEL.send(new NewComponentParticlePacket(targetedEntity, contents), PacketDistributor.TRACKING_ENTITY.with(targetedEntity));
	}

	@Override
	public void sendParticle(ServerLevel level, Vec3 position, double value, int colour) {
		CHANNEL.send(new NewNumericParticlePacket(value, position, colour), PacketDistributor.NEAR.with(new PacketDistributor.TargetPoint(position.x, position.y, position.z, 200, level.dimension())));
	}

	@Override
	public void sendParticle(LivingEntity targetedEntity, double value, int colour) {
		CHANNEL.send(new NewNumericParticlePacket(value, targetedEntity.getEyePosition(), colour), PacketDistributor.TRACKING_ENTITY.with(targetedEntity));
	}

	@Override
	public void sendParticleClaim(Identifier claimantId, LivingEntity targetedEntity, @Nullable CompoundTag additionalData) {
		CHANNEL.send(new ParticleClaimPacket(targetedEntity.getId(), claimantId, Optional.ofNullable(additionalData)), PacketDistributor.TRACKING_ENTITY.with(targetedEntity));
	}
}

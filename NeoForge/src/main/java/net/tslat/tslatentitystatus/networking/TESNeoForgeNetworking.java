package net.tslat.tslatentitystatus.networking;

import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import net.tslat.tslatentitystatus.TES;
import net.tslat.tslatentitystatus.TESClient;
import net.tslat.tslatentitystatus.core.networking.packet.*;
import net.tslat.tslatentitystatus.core.networking.packet.base.MultiloaderConfigurationPacket;
import net.tslat.tslatentitystatus.core.networking.packet.base.MultiloaderPacket;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.Set;

@NullMarked
public final class TESNeoForgeNetworking implements net.tslat.tslatentitystatus.core.networking.TESNetworking {
	public TESNeoForgeNetworking() {}

    @Override
	@ApiStatus.Internal
	public <B extends FriendlyByteBuf, P extends MultiloaderConfigurationPacket> void registerConfigurationPacketInternal(CustomPacketPayload.Type<P> payloadType, StreamCodec<B, P> codec, Direction direction) {
		@SuppressWarnings("unchecked")
        final StreamCodec<FriendlyByteBuf, P> implicitCodec = (StreamCodec<FriendlyByteBuf, P>)codec;

		switch (direction) {
			case CLIENTBOUND -> TES.packetRegistrar.configurationToClient(payloadType, implicitCodec, (packet, context) -> packet.handleTask(new MultiloaderConfigurationPacket.TaskHandler(context::reply, _ -> {})));
			case SERVERBOUND -> TES.packetRegistrar.configurationToServer(payloadType, implicitCodec, (packet, context) -> packet.handleTask(new MultiloaderConfigurationPacket.TaskHandler(_ -> {}, context::finishCurrentTask)));
			case BIDIRECTIONAL -> TES.packetRegistrar.configurationBidirectional(payloadType, implicitCodec, (packet, context) -> packet.handleTask(new MultiloaderConfigurationPacket.TaskHandler(reply -> {
				if (context.flow().isClientbound())
					context.reply(reply);
			}, task -> {
				if (context.flow().isServerbound())
					context.finishCurrentTask(task);
			})), (packet, context) -> packet.handleTask(new MultiloaderConfigurationPacket.TaskHandler(reply -> {
				if (context.flow().isClientbound())
					context.reply(reply);
			}, task -> {
				if (context.flow().isServerbound())
					context.finishCurrentTask(task);
			})));
		}
	}

	@SuppressWarnings("unchecked")
    @Override
	public <B extends FriendlyByteBuf, P extends MultiloaderPacket> void registerPacketInternal(CustomPacketPayload.Type<P> payloadType, StreamCodec<B, P> codec, Direction direction) {
		@SuppressWarnings("unchecked")
		final StreamCodec<FriendlyByteBuf, P> implicitCodec = (StreamCodec<FriendlyByteBuf, P>)codec;

		switch (direction) {
			case CLIENTBOUND -> TES.packetRegistrar.playToClient(payloadType, implicitCodec, (packet, context) -> packet.receiveMessage(context.player(), context::enqueueWork));
			case SERVERBOUND -> TES.packetRegistrar.playToServer(payloadType, implicitCodec, (packet, context) -> packet.receiveMessage(context.player(), context::enqueueWork));
            case BIDIRECTIONAL -> TES.packetRegistrar.playBidirectional(payloadType, implicitCodec,
																		(packet, context) -> packet.receiveMessage(context.player(), context::enqueueWork),
																		(packet, context) -> packet.receiveMessage(context.player(), context::enqueueWork));
        }
	}

	@Override
	public void requestEffectsSync(int entityId) {
		TESClient.sendServerboundPacket(new RequestEffectsPacket(entityId));
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
	public void sendParticle(ServerLevel level, Vec3 position, Component contents) {
		PacketDistributor.sendToPlayersNear(level, null, position.x, position.y, position.z, 200, new NewComponentParticlePacket(position, contents));
	}

	@Override
	public void sendParticle(LivingEntity targetedEntity, Component contents) {
		PacketDistributor.sendToPlayersTrackingEntity(targetedEntity, new NewComponentParticlePacket(targetedEntity, contents));
	}

	@Override
	public void sendParticle(ServerLevel level, Vec3 position, double value, int colour) {
		PacketDistributor.sendToPlayersNear(level, null, position.x, position.y, position.z, 200, new NewNumericParticlePacket(value, position, colour));
	}

	@Override
	public void sendParticle(LivingEntity targetedEntity, double value, int colour) {
		PacketDistributor.sendToPlayersTrackingEntity(targetedEntity, new NewNumericParticlePacket(value, targetedEntity.getEyePosition(), colour));
	}

	@Override
	public void sendParticleClaim(Identifier claimantId, LivingEntity targetedEntity, @Nullable CompoundTag additionalData) {
		PacketDistributor.sendToPlayersTrackingEntity(targetedEntity, new ParticleClaimPacket(targetedEntity.getId(), claimantId, Optional.ofNullable(additionalData)));
	}
}

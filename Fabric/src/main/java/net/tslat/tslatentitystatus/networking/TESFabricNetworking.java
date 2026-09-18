package net.tslat.tslatentitystatus.networking;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
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
import net.tslat.tslatentitystatus.TESClient;
import net.tslat.tslatentitystatus.api.TESConstants;
import net.tslat.tslatentitystatus.core.networking.TESNetworking;
import net.tslat.tslatentitystatus.core.networking.packet.*;
import net.tslat.tslatentitystatus.core.networking.packet.base.MultiloaderConfigurationPacket;
import net.tslat.tslatentitystatus.core.networking.packet.base.MultiloaderPacket;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.Set;

@NullMarked
public class TESFabricNetworking implements TESNetworking {
    @Override
	@ApiStatus.Internal
	public <B extends FriendlyByteBuf, P extends MultiloaderConfigurationPacket> void registerConfigurationPacketInternal(CustomPacketPayload.Type<P> packetType, StreamCodec<B, P> codec, Direction direction) {
		@SuppressWarnings("unchecked")
		final StreamCodec<FriendlyByteBuf, P> implicitCodec = (StreamCodec<FriendlyByteBuf, P>)codec;

		if (direction != Direction.CLIENTBOUND) {
			PayloadTypeRegistry.serverboundConfiguration().register(packetType, implicitCodec);
			ServerConfigurationNetworking.registerGlobalReceiver(packetType, (packet, context) ->
					packet.handleTask(new MultiloaderConfigurationPacket.TaskHandler(_ -> {}, context.packetListener()::completeTask)));
		}

		if (direction != Direction.SERVERBOUND) {
			PayloadTypeRegistry.clientboundConfiguration().register(packetType, implicitCodec);

			if (TESConstants.PLATFORM.isDevelopmentEnvironment())
				TESClient.registerConfigurationPacket(packetType);
		}
	}

    @Override
	@ApiStatus.Internal
	public <B extends FriendlyByteBuf, P extends MultiloaderPacket> void registerPacketInternal(CustomPacketPayload.Type<P> packetType, StreamCodec<B, P> codec, Direction direction) {
		@SuppressWarnings("unchecked")
		final StreamCodec<FriendlyByteBuf, P> implicitCodec = (StreamCodec<FriendlyByteBuf, P>)codec;

		if (direction != Direction.CLIENTBOUND) {
			PayloadTypeRegistry.serverboundPlay().register(packetType, implicitCodec);
			ServerPlayNetworking.registerGlobalReceiver(packetType, (packet, context) -> packet.receiveMessage(context.player(), context.player().level().getServer()::execute));
		}

		if (direction != Direction.SERVERBOUND) {
			PayloadTypeRegistry.clientboundPlay().register(packetType, implicitCodec);

			if (TESConstants.PLATFORM.isDevelopmentEnvironment())
				TESClient.registerPacket(packetType);
		}
	}

	@Override
	public void requestEffectsSync(int entityId) {
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
	public void sendParticle(ServerLevel level, Vec3 position, Component contents) {
		for (ServerPlayer player : PlayerLookup.tracking(level, BlockPos.containing(position.x, position.y, position.z))) {
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
	public void sendParticle(ServerLevel level, Vec3 position, double value, int colour) {
		for (ServerPlayer player : PlayerLookup.tracking(level, BlockPos.containing(position.x, position.y, position.z))) {
			ServerPlayNetworking.send(player, new NewNumericParticlePacket(value, position, colour));
		}
	}

	@Override
	public void sendParticle(LivingEntity targetedEntity, double value, int colour) {
		for (ServerPlayer player : PlayerLookup.tracking(targetedEntity)) {
			ServerPlayNetworking.send(player, new NewNumericParticlePacket(value, targetedEntity.getEyePosition(), colour));
		}
	}

	@Override
	public void sendParticleClaim(Identifier claimantId, LivingEntity targetedEntity, @Nullable CompoundTag additionalData) {
		for (ServerPlayer player : PlayerLookup.tracking(targetedEntity)) {
			ServerPlayNetworking.send(player, new ParticleClaimPacket(targetedEntity.getId(), claimantId, Optional.ofNullable(additionalData)));
		}
	}
}

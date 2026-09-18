package net.tslat.tslatentitystatus.core.networking;

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
import net.tslat.tslatentitystatus.api.TESConstants;
import net.tslat.tslatentitystatus.api.client.object.TESParticle;
import net.tslat.tslatentitystatus.core.networking.packet.*;
import net.tslat.tslatentitystatus.core.networking.packet.base.MultiloaderConfigurationPacket;
import net.tslat.tslatentitystatus.core.networking.packet.base.MultiloaderPacket;
import net.tslat.tslatentitystatus.core.particle.TESParticleClaimant;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.util.Set;

/// Duck-interface for `TES`' networking functionality.
///
/// This is only functional if `TES` is installed on the server.
///
/// Access this from [TESConstants#NETWORKING]
@SuppressWarnings("SameParameterValue")
public interface TESNetworking {
	/// Request an update for [MobEffect]s for a given entity
	///
	/// Network direction: `CLIENT -> SERVER`
	void requestEffectsSync(int entityId);

	/// Send an update for [MobEffect]s for a given entity to a specific player (usually in response to a [prior request][TESNetworking#requestEffectsSync]
	///
	/// Network direction: `SERVER -> CLIENT`
	///
	/// @param player 	The player to send to
	/// @param entityId The id of the entity to update
	/// @param toAdd 	The effects to add to the entity's state on the client side (usually all of them)
	/// @param toRemove The effects to remove from the entity's state on the client side (usually empty)
	void sendEffectsSync(ServerPlayer player, int entityId, Set<Holder<MobEffect>> toAdd, Set<Holder<MobEffect>> toRemove);

	/// Send an update for [MobEffect]s to all players tracking the given entity. Usually as part of an effect being added/removed
	///
	/// Network direction: `SERVER -> CLIENT`
	///
	/// @param targetedEntity 	The entity to update for
	/// @param toAdd 			The effects to add to the entity's state on the client side
	/// @param toRemove 		The effects to remove from the entity's state on the client side
	void sendEffectsSync(LivingEntity targetedEntity, Set<Holder<MobEffect>> toAdd, Set<Holder<MobEffect>> toRemove);

	/// Send a [TESParticle] for the given position
	///
	/// Network direction: `SERVER -> CLIENT`
	///
	/// @param level 	The level the particle is in
	/// @param position The position the particle should appear at
	/// @param contents The contents of the particle. If sending a numeric value, use one of the double-based methods
	void sendParticle(ServerLevel level, Vec3 position, Component contents);

	/// Send a [TESParticle] for the given entity
	///
	/// Network direction: `SERVER -> CLIENT`
	///
	/// @param targetedEntity 	The entity the particle should appear on
	/// @param contents 		The contents of the particle. If sending a numeric value, use one of the double-based methods
	void sendParticle(LivingEntity targetedEntity, Component contents);

	/// Send a [TESParticle] for the given position
	///
	/// Network direction: `SERVER -> CLIENT`
	///
	/// @param level 	The level the particle is in
	/// @param position The position the particle should appear at
	/// @param value    The value of the particle
	/// @param colour   The text colour of the particle
	void sendParticle(ServerLevel level, Vec3 position, double value, int colour);

	/// Send a [TESParticle] for the given entity
	///
	/// Network direction: `SERVER -> CLIENT`
	///
	/// @param targetedEntity 	The entity the particle should appear on
	/// @param value 			The value of the particle
	/// @param colour 			The text colour of the particle
	void sendParticle(LivingEntity targetedEntity, double value, int colour);

	/// Submit a particle claim for the next/upcoming tick
	///
	/// Network direction: `SERVER -> CLIENT`
	///
	/// @param claimantId 		The id of the [TESParticleClaimant] to handle the claim
	/// @param targetedEntity 	The entity for the claim
	/// @param additionalData 	Optional additional data for the claim
	void sendParticleClaim(Identifier claimantId, LivingEntity targetedEntity, @Nullable CompoundTag additionalData);

	@ApiStatus.Internal
	<B extends FriendlyByteBuf, P extends MultiloaderConfigurationPacket> void registerConfigurationPacketInternal(CustomPacketPayload.Type<P> payloadType, StreamCodec<B, P> codec, Direction direction);
	@ApiStatus.Internal
	<B extends FriendlyByteBuf, P extends MultiloaderPacket> void registerPacketInternal(CustomPacketPayload.Type<P> payloadType, StreamCodec<B, P> codec, Direction direction);

	@ApiStatus.Internal
	static void init() {
		registerPacket(NewComponentParticlePacket.TYPE, NewComponentParticlePacket.CODEC, Direction.CLIENTBOUND);
		registerPacket(NewNumericParticlePacket.TYPE, NewNumericParticlePacket.CODEC, Direction.CLIENTBOUND);
		registerPacket(ParticleClaimPacket.TYPE, ParticleClaimPacket.CODEC, Direction.CLIENTBOUND);
		registerPacket(RequestEffectsPacket.TYPE, RequestEffectsPacket.CODEC, Direction.SERVERBOUND);
		registerPacket(SyncEffectsPacket.TYPE, SyncEffectsPacket.CODEC, Direction.CLIENTBOUND);
		registerConfigurationPacket(ServerConnectionAckPacket.TYPE, ServerConnectionAckPacket.CODEC, Direction.BIDIRECTIONAL);
	}

	@ApiStatus.Internal
	private static <B extends FriendlyByteBuf, P extends MultiloaderPacket> void registerPacket(CustomPacketPayload.Type<P> payloadType, StreamCodec<B, P> codec, Direction direction) {
		TESConstants.NETWORKING.registerPacketInternal(payloadType, codec, direction);
	}

	@ApiStatus.Internal
	private static <B extends FriendlyByteBuf, P extends MultiloaderConfigurationPacket> void registerConfigurationPacket(CustomPacketPayload.Type<P> payloadType, StreamCodec<B, P> codec, Direction direction) {
		TESConstants.NETWORKING.registerConfigurationPacketInternal(payloadType, codec, direction);
	}

	/// Network direction enum for `TES` packets
	enum Direction {
		SERVERBOUND,
		CLIENTBOUND,
		BIDIRECTIONAL
	}
}

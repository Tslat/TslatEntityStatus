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
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;
import net.tslat.tes.api.TESAPI;
import net.tslat.tes.api.TESConstants;
import net.tslat.tes.api.util.TESClientUtil;
import net.tslat.tes.core.networking.packet.*;
import org.joml.Vector3f;

import java.util.Optional;
import java.util.Set;

public final class TESNetworking implements net.tslat.tes.core.networking.TESNetworking {
	public static final SimpleChannel INSTANCE = ChannelBuilder.named(new ResourceLocation(TESConstants.MOD_ID, "tes_packets")).clientAcceptedVersions((status, version) -> true).simpleChannel();

	public TESNetworking() {}

	@Override
	public <B extends FriendlyByteBuf, P extends MultiloaderPacket> void registerPacketInternal(CustomPacketPayload.Type<P> packetType, StreamCodec<B, P> codec, boolean isClientBound) {
		if (isClientBound) {
			INSTANCE.messageBuilder(packetType, NetworkDirection.PLAY_TO_CLIENT).codec((StreamCodec<RegistryFriendlyByteBuf, P>)codec).consumerMainThread((packet, context) -> {
				packet.receiveMessage(context.getSender() != null ? context.getSender() : TESClientUtil.getClientPlayer(), context::enqueueWork);
				context.setPacketHandled(true);
			}).add();
		}
		else {
			INSTANCE.messageBuilder(packetType, NetworkDirection.PLAY_TO_SERVER).codec((StreamCodec<RegistryFriendlyByteBuf, P>)codec).consumerMainThread((packet, context) -> {
				packet.receiveMessage(context.getSender() != null ? context.getSender() : TESClientUtil.getClientPlayer(), context::enqueueWork);
				context.setPacketHandled(true);
			}).add();
		}
	}

	@Override
	public void requestEffectsSync(int entityId) {
		if (!TESAPI.getConfig().isSyncingEffects())
			return;

		INSTANCE.send(new RequestEffectsPacket(entityId), PacketDistributor.SERVER.noArg());
	}

	@Override
	public void sendEffectsSync(ServerPlayer player, int entityId, Set<Holder<MobEffect>> toAdd, Set<Holder<MobEffect>> toRemove) {
		INSTANCE.send(new SyncEffectsPacket(entityId, toAdd, toRemove), PacketDistributor.PLAYER.with(player));
	}

	@Override
	public void sendEffectsSync(LivingEntity targetedEntity, Set<Holder<MobEffect>> toAdd, Set<Holder<MobEffect>> toRemove) {
		INSTANCE.send(new SyncEffectsPacket(targetedEntity.getId(), toAdd, toRemove), PacketDistributor.TRACKING_ENTITY.with(targetedEntity));
	}

	@Override
	public void sendParticle(ServerLevel level, Vector3f position, Component contents) {
		INSTANCE.send(new NewComponentParticlePacket(position, contents), PacketDistributor.NEAR.with(new PacketDistributor.TargetPoint(position.x, position.y, position.z, 200, level.dimension())));
	}

	@Override
	public void sendParticle(LivingEntity targetedEntity, Component contents) {
		INSTANCE.send(new NewComponentParticlePacket(targetedEntity, contents), PacketDistributor.TRACKING_ENTITY.with(targetedEntity));
	}

	@Override
	public void sendParticle(ServerLevel level, Vector3f position, double value, int colour) {
		INSTANCE.send(new NewNumericParticlePacket(value, position, colour), PacketDistributor.NEAR.with(new PacketDistributor.TargetPoint(position.x, position.y, position.z, 200, level.dimension())));
	}

	@Override
	public void sendParticle(LivingEntity targetedEntity, double value, int colour) {
		INSTANCE.send(new NewNumericParticlePacket(value, new Vector3f((float)targetedEntity.getX(), (float)targetedEntity.getEyeY(), (float)targetedEntity.getZ()), colour), PacketDistributor.TRACKING_ENTITY.with(targetedEntity));
	}

	@Override
	public void sendParticleClaim(ResourceLocation claimantId, LivingEntity targetedEntity, Optional<CompoundTag> additionalData) {
		INSTANCE.send(new ParticleClaimPacket(targetedEntity.getId(), claimantId, additionalData), PacketDistributor.TRACKING_ENTITY.with(targetedEntity));
	}
}

package net.tslat.tes.networking;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;
import net.tslat.tes.api.TESAPI;
import net.tslat.tes.api.TESConstants;
import net.tslat.tes.api.util.TESClientUtil;
import net.tslat.tes.core.networking.packet.*;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Set;

public final class TESNetworking implements net.tslat.tes.core.networking.TESNetworking {
	public static final SimpleChannel INSTANCE = ChannelBuilder.named(new ResourceLocation(TESConstants.MOD_ID, "tes_packets")).clientAcceptedVersions((status, version) -> true).simpleChannel();

	public TESNetworking() {}

	@Override
	public <P extends MultiloaderPacket> void registerPacketInternal(ResourceLocation id, Class<P> packetClass, FriendlyByteBuf.Reader<P> decoder) {
		INSTANCE.messageBuilder(packetClass).encoder(MultiloaderPacket::write).decoder(decoder).consumerMainThread((packet, context) -> {
			packet.receiveMessage(context.getSender() != null ? context.getSender() : TESClientUtil.getClientPlayer(), context::enqueueWork);
			context.setPacketHandled(true);
		}).add();
	}

	@Override
	public void requestEffectsSync(int entityId) {
		if (!TESAPI.getConfig().isSyncingEffects())
			return;

		INSTANCE.send(new RequestEffectsPacket(entityId), PacketDistributor.SERVER.noArg());
	}

	@Override
	public void sendEffectsSync(ServerPlayer player, int entityId, Set<ResourceLocation> toAdd, Set<ResourceLocation> toRemove) {
		INSTANCE.send(new SyncEffectsPacket(entityId, toAdd, toRemove), PacketDistributor.PLAYER.with(player));
	}

	@Override
	public void sendEffectsSync(LivingEntity targetedEntity, Set<ResourceLocation> toAdd, Set<ResourceLocation> toRemove) {
		INSTANCE.send(new SyncEffectsPacket(targetedEntity.getId(), toAdd, toRemove), PacketDistributor.TRACKING_ENTITY.with(targetedEntity));
	}

	@Override
	public void sendParticle(Level level, Vector3f position, Component contents) {
		INSTANCE.send(new NewComponentParticlePacket(position, contents), PacketDistributor.NEAR.with(new PacketDistributor.TargetPoint(position.x, position.y, position.z, 200, level.dimension())));
	}

	@Override
	public void sendParticle(LivingEntity targetedEntity, Component contents) {
		INSTANCE.send(new NewComponentParticlePacket(targetedEntity, contents), PacketDistributor.TRACKING_ENTITY.with(targetedEntity));
	}

	@Override
	public void sendParticle(Level level, Vector3f position, double value, int colour) {
		INSTANCE.send(new NewNumericParticlePacket(value, position, colour), PacketDistributor.NEAR.with(new PacketDistributor.TargetPoint(position.x, position.y, position.z, 200, level.dimension())));
	}

	@Override
	public void sendParticle(LivingEntity targetedEntity, double value, int colour) {
		INSTANCE.send(new NewNumericParticlePacket(value, new Vector3f((float)targetedEntity.getX(), (float)targetedEntity.getEyeY(), (float)targetedEntity.getZ()), colour), PacketDistributor.TRACKING_ENTITY.with(targetedEntity));
	}

	@Override
	public void sendParticleClaim(ResourceLocation claimantId, LivingEntity targetedEntity, @Nullable CompoundTag additionalData) {
		INSTANCE.send(new ParticleClaimPacket(targetedEntity.getId(), claimantId, additionalData), PacketDistributor.TRACKING_ENTITY.with(targetedEntity));
	}
}

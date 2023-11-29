package net.tslat.tes.networking;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.*;
import net.tslat.tes.api.TESAPI;
import net.tslat.tes.api.TESConstants;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Set;

public final class TESNetworking implements net.tslat.tes.core.networking.TESNetworking {
	public static final SimpleChannel INSTANCE = ChannelBuilder.named(new ResourceLocation(TESConstants.MOD_ID, "tes_packets")).clientAcceptedVersions((status, version) -> true).simpleChannel();

	public TESNetworking() {}

	public static void init() {
		INSTANCE.messageBuilder(RequestEffectsPacket.class, NetworkDirection.PLAY_TO_SERVER).encoder(RequestEffectsPacket::encode).decoder(RequestEffectsPacket::decode).consumerMainThread(RequestEffectsPacket::handleMessage).add();
		INSTANCE.messageBuilder(SyncEffectsPacket.class, NetworkDirection.PLAY_TO_CLIENT).encoder(SyncEffectsPacket::encode).decoder(SyncEffectsPacket::decode).consumerMainThread(SyncEffectsPacket::handleMessage).add();
		INSTANCE.messageBuilder(ParticleClaimPacket.class, NetworkDirection.PLAY_TO_CLIENT).encoder(ParticleClaimPacket::encode).decoder(ParticleClaimPacket::decode).consumerMainThread(ParticleClaimPacket::handleMessage).add();
		INSTANCE.messageBuilder(NewComponentParticlePacket.class, NetworkDirection.PLAY_TO_CLIENT).encoder(NewComponentParticlePacket::encode).decoder(NewComponentParticlePacket::decode).consumerMainThread(NewComponentParticlePacket::handleMessage).add();
		INSTANCE.messageBuilder(NewNumericParticlePacket.class, NetworkDirection.PLAY_TO_CLIENT).encoder(NewNumericParticlePacket::encode).decoder(NewNumericParticlePacket::decode).consumerMainThread(NewNumericParticlePacket::handleMessage).add();
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

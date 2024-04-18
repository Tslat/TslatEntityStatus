package net.tslat.tes.networking;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import net.tslat.tes.TES;
import net.tslat.tes.api.TESAPI;
import net.tslat.tes.api.util.TESClientUtil;
import net.tslat.tes.core.networking.packet.*;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Set;

public final class TESNetworking implements net.tslat.tes.core.networking.TESNetworking {
	public TESNetworking() {}

	@Override
	public <P extends MultiloaderPacket> void registerPacketInternal(ResourceLocation id, boolean isClientBound, Class<P> packetClass, FriendlyByteBuf.Reader<P> decoder) {
		TES.packetRegistrar.play(id, decoder, (packet, context) -> packet.receiveMessage(context.player().orElseGet(TESClientUtil::getClientPlayer), context.workHandler()::execute));
	}

	@Override
	public void requestEffectsSync(int entityId) {
		if (!TESAPI.getConfig().isSyncingEffects())
			return;

		PacketDistributor.SERVER.noArg().send(new RequestEffectsPacket(entityId));
	}

	@Override
	public void sendEffectsSync(ServerPlayer player, int entityId, Set<ResourceLocation> toAdd, Set<ResourceLocation> toRemove) {
		if (!TESAPI.getConfig().isSyncingEffects())
			return;

		PacketDistributor.PLAYER.with(player).send(new SyncEffectsPacket(entityId, toAdd, toRemove));
	}

	@Override
	public void sendEffectsSync(LivingEntity targetedEntity, Set<ResourceLocation> toAdd, Set<ResourceLocation> toRemove) {
		if (!TESAPI.getConfig().isSyncingEffects())
			return;

		PacketDistributor.TRACKING_ENTITY.with(targetedEntity).send(new SyncEffectsPacket(targetedEntity.getId(), toAdd, toRemove));
	}

	@Override
	public void sendParticle(Level level, Vector3f position, Component contents) {
		PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(position.x, position.y, position.z, 200, level.dimension()).get()).send(new NewComponentParticlePacket(position, contents));
	}

	@Override
	public void sendParticle(LivingEntity targetedEntity, Component contents) {
		PacketDistributor.TRACKING_ENTITY.with(targetedEntity).send(new NewComponentParticlePacket(targetedEntity, contents));
	}

	@Override
	public void sendParticle(Level level, Vector3f position, double value, int colour) {
		PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(position.x, position.y, position.z, 200, level.dimension()).get()).send(new NewNumericParticlePacket(value, position, colour));
	}

	@Override
	public void sendParticle(LivingEntity targetedEntity, double value, int colour) {
		PacketDistributor.TRACKING_ENTITY.with(targetedEntity).send(new NewNumericParticlePacket(value, new Vector3f((float)targetedEntity.getX(), (float)targetedEntity.getEyeY(), (float)targetedEntity.getZ()), colour));
	}

	@Override
	public void sendParticleClaim(ResourceLocation claimantId, LivingEntity targetedEntity, @Nullable CompoundTag additionalData) {
		PacketDistributor.TRACKING_ENTITY.with(targetedEntity).send(new ParticleClaimPacket(targetedEntity.getId(), claimantId, additionalData));
	}
}

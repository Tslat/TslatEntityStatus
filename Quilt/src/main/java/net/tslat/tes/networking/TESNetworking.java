package net.tslat.tes.networking;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.tslat.tes.TESClient;
import net.tslat.tes.api.TESAPI;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.PlayerLookup;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;

import java.util.Set;

public class TESNetworking implements net.tslat.tes.core.networking.TESNetworking {
	public static boolean isSyncingEffects() {
		return TESAPI.getConfig().hudPotionIcons() || TESAPI.getConfig().inWorldHudPotionIcons();
	}

	@Override
	public void requestEffectsSync(int entityId) {
		if (!isSyncingEffects())
			return;

		FriendlyByteBuf buffer = PacketByteBufs.create();

		new RequestEffectsPacket(entityId).encode(buffer);

		TESClient.sendPacket(RequestEffectsPacket.ID, buffer);
	}

	@Override
	public void sendEffectsSync(ServerPlayer player, int entityId, Set<ResourceLocation> toAdd, Set<ResourceLocation> toRemove) {
		if (!isSyncingEffects())
			return;

		FriendlyByteBuf buffer = PacketByteBufs.create();

		new SyncEffectsPacket(entityId, toAdd, toRemove).encode(buffer);

		ServerPlayNetworking.send(player, SyncEffectsPacket.ID, buffer);
	}

	@Override
	public void sendEffectsSync(LivingEntity targetedEntity, Set<ResourceLocation> toAdd, Set<ResourceLocation> toRemove) {
		if (!isSyncingEffects())
			return;

		FriendlyByteBuf buffer = PacketByteBufs.create();

		new SyncEffectsPacket(targetedEntity.getId(), toAdd, toRemove).encode(buffer);

		ServerPlayNetworking.send(PlayerLookup.tracking(targetedEntity), SyncEffectsPacket.ID, buffer);
	}

	@Override
	public void sendParticle(Level level, Vector3f position, Component contents) {
		FriendlyByteBuf buffer = PacketByteBufs.create();

		new NewComponentParticlePacket(position, contents).encode(buffer);

		ServerPlayNetworking.send(PlayerLookup.tracking((ServerLevel)level, BlockPos.containing(position.x, position.y, position.z)), NewComponentParticlePacket.ID, buffer);
	}

	@Override
	public void sendParticle(LivingEntity targetedEntity, Component contents) {
		FriendlyByteBuf buffer = PacketByteBufs.create();

		new NewComponentParticlePacket(targetedEntity, contents).encode(buffer);

		ServerPlayNetworking.send(PlayerLookup.tracking(targetedEntity), NewComponentParticlePacket.ID, buffer);
	}

	@Override
	public void sendParticle(Level level, Vector3f position, double value, int colour) {
		FriendlyByteBuf buffer = PacketByteBufs.create();

		new NewNumericParticlePacket(value, position, colour).encode(buffer);

		ServerPlayNetworking.send(PlayerLookup.tracking((ServerLevel)level, BlockPos.containing(position.x, position.y, position.z)), NewNumericParticlePacket.ID, buffer);
	}

	@Override
	public void sendParticle(LivingEntity targetedEntity, double value, int colour) {
		FriendlyByteBuf buffer = PacketByteBufs.create();

		new NewNumericParticlePacket(value, new Vector3f((float)targetedEntity.getX(), (float)targetedEntity.getEyeY(), (float)targetedEntity.getZ()), colour).encode(buffer);

		ServerPlayNetworking.send(PlayerLookup.tracking(targetedEntity), NewNumericParticlePacket.ID, buffer);
	}

	@Override
	public void sendParticleClaim(ResourceLocation claimantId, LivingEntity targetedEntity, @Nullable CompoundTag additionalData) {
		FriendlyByteBuf buffer = PacketByteBufs.create();

		new ParticleClaimPacket(targetedEntity.getId(), claimantId, additionalData).encode(buffer);

		ServerPlayNetworking.send(PlayerLookup.tracking(targetedEntity), ParticleClaimPacket.ID, buffer);
	}
}

package net.tslat.tes.networking;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.tslat.tes.api.TESAPI;
import net.tslat.tes.api.TESConstants;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Set;
import java.util.function.Predicate;

public final class TESNetworking implements net.tslat.tes.core.networking.TESNetworking {
	private static final String REV = "1";
	private static final Predicate<String> ABSENT_OR_EQUAL = NetworkRegistry.acceptMissingOr(REV::equals);
	public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(TESConstants.MOD_ID, "tes_packets"), () -> REV, ABSENT_OR_EQUAL, ABSENT_OR_EQUAL);

	public TESNetworking() {}

	public static void init() {
		int id = 0;

		INSTANCE.registerMessage(id++, RequestEffectsPacket.class, RequestEffectsPacket::encode, RequestEffectsPacket::decode, RequestEffectsPacket::handleMessage);
		INSTANCE.registerMessage(id++, SyncEffectsPacket.class, SyncEffectsPacket::encode, SyncEffectsPacket::decode, SyncEffectsPacket::handleMessage);
		INSTANCE.registerMessage(id++, ParticleClaimPacket.class, ParticleClaimPacket::encode, ParticleClaimPacket::decode, ParticleClaimPacket::handleMessage);
		INSTANCE.registerMessage(id++, NewComponentParticlePacket.class, NewComponentParticlePacket::encode, NewComponentParticlePacket::decode, NewComponentParticlePacket::handleMessage);
		INSTANCE.registerMessage(id++, NewNumericParticlePacket.class, NewNumericParticlePacket::encode, NewNumericParticlePacket::decode, NewNumericParticlePacket::handleMessage);
	}

	@Override
	public void requestEffectsSync(int entityId) {
		if (!TESAPI.getConfig().isSyncingEffects())
			return;

		INSTANCE.sendToServer(new RequestEffectsPacket(entityId));
	}

	@Override
	public void sendEffectsSync(ServerPlayer player, int entityId, Set<ResourceLocation> toAdd, Set<ResourceLocation> toRemove) {
		INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new SyncEffectsPacket(entityId, toAdd, toRemove));
	}

	@Override
	public void sendEffectsSync(LivingEntity targetedEntity, Set<ResourceLocation> toAdd, Set<ResourceLocation> toRemove) {
		INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> targetedEntity), new SyncEffectsPacket(targetedEntity.getId(), toAdd, toRemove));
	}

	@Override
	public void sendParticle(Level level, Vector3f position, Component contents) {
		INSTANCE.send(PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(position.x, position.y, position.z, 200, level.dimension())), new NewComponentParticlePacket(position, contents));
	}

	@Override
	public void sendParticle(LivingEntity targetedEntity, Component contents) {
		INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> targetedEntity), new NewComponentParticlePacket(targetedEntity, contents));
	}

	@Override
	public void sendParticle(Level level, Vector3f position, double value, int colour) {
		INSTANCE.send(PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(position.x, position.y, position.z, 200, level.dimension())), new NewNumericParticlePacket(value, position, colour));
	}

	@Override
	public void sendParticle(LivingEntity targetedEntity, double value, int colour) {
		INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> targetedEntity), new NewNumericParticlePacket(value, new Vector3f((float)targetedEntity.getX(), (float)targetedEntity.getEyeY(), (float)targetedEntity.getZ()), colour));
	}

	@Override
	public void sendParticleClaim(ResourceLocation claimantId, LivingEntity targetedEntity, @Nullable CompoundTag additionalData) {
		INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> targetedEntity), new ParticleClaimPacket(targetedEntity.getId(), claimantId, additionalData));
	}
}

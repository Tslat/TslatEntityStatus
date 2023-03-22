package net.tslat.tes.networking;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.tslat.tes.api.TESAPI;
import net.tslat.tes.api.TESConstants;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public final class TESNetworking implements net.tslat.tes.core.networking.TESNetworking {
	private static final String REV = "1";
	public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(TESConstants.MOD_ID, "tes_packets"), () -> REV, rev -> {
		if (rev.equals(NetworkRegistry.ABSENT)) {
			EFFECTS_SYNCING_ENABLED = false;

			return true;
		}

		if (rev.equals(REV)) {
			EFFECTS_SYNCING_ENABLED = TESAPI.getConfig().hudPotionIcons() || TESAPI.getConfig().inWorldHudPotionIcons();

			return true;
		}

		return false;
	}, rev -> {
		if (rev.equals(NetworkRegistry.ABSENT)) {
			EFFECTS_SYNCING_ENABLED = false;

			return true;
		}

		if (rev.equals(REV)) {
			EFFECTS_SYNCING_ENABLED = TESAPI.getConfig().hudPotionIcons() || TESAPI.getConfig().inWorldHudPotionIcons();

			return true;
		}

		return false;
	});

	private static boolean EFFECTS_SYNCING_ENABLED = true;

	public TESNetworking() {}

	public static void init() {
		int id = 0;

		INSTANCE.registerMessage(id++, RequestEffectsPacket.class, RequestEffectsPacket::encode, RequestEffectsPacket::decode, RequestEffectsPacket::handleMessage);
		INSTANCE.registerMessage(id++, SyncEffectsPacket.class, SyncEffectsPacket::encode, SyncEffectsPacket::decode, SyncEffectsPacket::handleMessage);
		INSTANCE.registerMessage(id++, ParticleClaimPacket.class, ParticleClaimPacket::encode, ParticleClaimPacket::decode, ParticleClaimPacket::handleMessage);
	}

	public static boolean isSyncingEffects() {
		return EFFECTS_SYNCING_ENABLED;
	}

	@Override
	public void requestEffectsSync(int entityId) {
		if (!isSyncingEffects())
			return;

		INSTANCE.sendToServer(new RequestEffectsPacket(entityId));
	}

	@Override
	public void sendEffectsSync(ServerPlayer player, int entityId, Set<ResourceLocation> toAdd, Set<ResourceLocation> toRemove) {
		if (!isSyncingEffects())
			return;

		INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new SyncEffectsPacket(entityId, toAdd, toRemove));
	}

	@Override
	public void sendEffectsSync(LivingEntity targetedEntity, Set<ResourceLocation> toAdd, Set<ResourceLocation> toRemove) {
		if (!isSyncingEffects())
			return;

		INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> targetedEntity), new SyncEffectsPacket(targetedEntity.getId(), toAdd, toRemove));
	}

	@Override
	public void sendParticleClaim(ResourceLocation claimantId, LivingEntity targetedEntity, @Nullable CompoundTag additionalData) {
		INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> targetedEntity), new ParticleClaimPacket(targetedEntity.getId(), claimantId, additionalData));
	}
}

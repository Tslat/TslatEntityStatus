package net.tslat.tes.networking;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.tslat.tes.api.TESAPI;
import net.tslat.tes.api.TESConstants;

import javax.annotation.Nullable;
import java.util.Set;

/**
 * Base class for TES' networking functionality.<br>
 * This is only functional if TES is installed on the server.<br>
 * Access this from {@link net.tslat.tes.api.TESConstants#NETWORKING TESConstants.NETWORKING}
 */
public final class TESNetworking {
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
		INSTANCE.registerMessage(id++, NewComponentParticlePacket.class, NewComponentParticlePacket::encode, NewComponentParticlePacket::decode, NewComponentParticlePacket::handleMessage);
		INSTANCE.registerMessage(id++, NewNumericParticlePacket.class, NewNumericParticlePacket::encode, NewNumericParticlePacket::decode, NewNumericParticlePacket::handleMessage);
	}

	public static boolean isSyncingEffects() {
		return EFFECTS_SYNCING_ENABLED;
	}

	/**
	 * Request an update for {@link net.minecraft.potion.Effect MobEffects} for a given entity<br>
	 * Network direction: (CLIENT -> SERVER)
	 */
	public static void requestEffectsSync(int entityId) {
		if (!isSyncingEffects())
			return;

		INSTANCE.sendToServer(new RequestEffectsPacket(entityId));
	}

	/**
	 * Send an update for {@link net.minecraft.potion.Effect MobEffects} for a given entity to a specific player (usually in response to a {@link TESNetworking#requestEffectsSync prior request}<br>
	 * Network direction: SERVER -> CLIENT
	 * @param player The player to send to
	 * @param entityId The id of the entity to update
	 * @param toAdd The effects to add to the entity's state on the client side (usually all of them)
	 * @param toRemove The effects to remove from the entity's state on the client side (usually empty)
	 */
	public static void sendEffectsSync(ServerPlayerEntity player, int entityId, Set<ResourceLocation> toAdd, Set<ResourceLocation> toRemove) {
		if (!isSyncingEffects())
			return;

		INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new SyncEffectsPacket(entityId, toAdd, toRemove));
	}

	/**
	 * Send an update for {@link net.minecraft.potion.Effect MobEffects} to all players tracking the given entity. Usually as part of an effect being added/removed<br>
	 * Network direction: SERVER -> CLIENT
	 * @param targetedEntity The entity to update for
	 * @param toAdd The effects to add to the entity's state on the client side
	 * @param toRemove The effects to remove from the entity's state on the client side
	 */
	public static void sendEffectsSync(LivingEntity targetedEntity, Set<ResourceLocation> toAdd, Set<ResourceLocation> toRemove) {
		if (!isSyncingEffects())
			return;

		INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> targetedEntity), new SyncEffectsPacket(targetedEntity.getId(), toAdd, toRemove));
	}

	/**
	 * Send a {@link net.tslat.tes.api.TESParticle TESParticle} for the given position<br>
	 * Network direction: SERVER -> CLIENT
	 * @param level The level the particle is in
	 * @param position The position the particle should appear at
	 * @param contents The contents of the particle. If sending a numeric value, use one of the double-based methods
	 */
	public static void sendParticle(World level, Vector3f position, ITextComponent contents) {
		INSTANCE.send(PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(position.x(), position.y(), position.z(), 200, level.dimension())), new NewComponentParticlePacket(position, contents));
	}

	/**
	 * Send a {@link net.tslat.tes.api.TESParticle TESParticle} for the given entity<br>
	 * Network direction: SERVER -> CLIENT
	 * @param targetedEntity The entity the particle should appear on
	 * @param contents The contents of the particle. If sending a numeric value, use one of the double-based methods
	 */
	public static void sendParticle(LivingEntity targetedEntity, ITextComponent contents) {
		INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> targetedEntity), new NewComponentParticlePacket(targetedEntity, contents));
	}

	/**
	 * Send a {@link net.tslat.tes.api.TESParticle TESParticle} for the given position<br>
	 * Network direction: SERVER -> CLIENT
	 * @param level The level the particle is in
	 * @param position The position the particle should appear at
	 * @param value    The value of the particle
	 * @param colour   The text colour of the particle
	 */
	public static void sendParticle(World level, Vector3f position, double value, int colour) {
		INSTANCE.send(PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(position.x(), position.y(), position.z(), 200, level.dimension())), new NewNumericParticlePacket(value, position, colour));
	}

	/**
	 * Send a {@link net.tslat.tes.api.TESParticle TESParticle} for the given entity<br>
	 * Network direction: SERVER -> CLIENT
	 * @param targetedEntity The entity the particle should appear on
	 * @param value The value of the particle
	 * @param colour The text colour of the particle
	 */
	public static void sendParticle(LivingEntity targetedEntity, double value, int colour) {
		INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> targetedEntity), new NewNumericParticlePacket(value, new Vector3f((float)targetedEntity.getX(), (float)targetedEntity.getEyeY(), (float)targetedEntity.getZ()), colour));
	}

	/**
	 * Submit a particle claim for the next/upcoming tick<br>
	 * Network direction: SERVER -> CLIENT
	 * @param claimantId The id of the {@link net.tslat.tes.core.particle.TESParticleClaimant claimant} to handle the claim
	 * @param targetedEntity The entity for the claim
	 * @param additionalData Optional additional data for the claim
	 */
	public static void sendParticleClaim(ResourceLocation claimantId, LivingEntity targetedEntity, @Nullable CompoundNBT additionalData) {
		INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> targetedEntity), new ParticleClaimPacket(targetedEntity.getId(), claimantId, additionalData));
	}
}

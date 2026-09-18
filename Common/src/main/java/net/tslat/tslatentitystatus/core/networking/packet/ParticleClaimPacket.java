package net.tslat.tslatentitystatus.core.networking.packet;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.tslat.tslatentitystatus.api.TESConstants;
import net.tslat.tslatentitystatus.api.TESParticleAPI;
import net.tslat.tslatentitystatus.core.networking.packet.base.MultiloaderPacket;

import java.util.Optional;
import java.util.function.Consumer;

/// `TES` particle for submitting a `TESParticleClaim` to special-handle a `TESParticle`
public record ParticleClaimPacket(int entityId, Identifier claimantId, Optional<CompoundTag> data) implements MultiloaderPacket {
	public static final Type<ParticleClaimPacket> TYPE = new Type<>(TESConstants.id("particle_claim"));
	public static final StreamCodec<FriendlyByteBuf, ParticleClaimPacket> CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, ParticleClaimPacket::entityId,
			Identifier.STREAM_CODEC, ParticleClaimPacket::claimantId,
			ByteBufCodecs.OPTIONAL_COMPOUND_TAG, ParticleClaimPacket::data,
			ParticleClaimPacket::new);

	@Override
	public Type<ParticleClaimPacket> type() {
		return TYPE;
	}

	@Override
	public void receiveMessage(Player sender, Consumer<Runnable> workQueue) {
		workQueue.accept(() -> {
			if (sender.level().getEntity(this.entityId) instanceof LivingEntity target)
				TESParticleAPI.submitParticleClaim(this.claimantId, target, this.data);
		});
	}
}

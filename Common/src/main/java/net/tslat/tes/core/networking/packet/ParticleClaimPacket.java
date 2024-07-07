package net.tslat.tes.core.networking.packet;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.tslat.tes.api.TESConstants;
import net.tslat.tes.core.particle.TESParticleManager;

import java.util.Optional;
import java.util.function.Consumer;

public record ParticleClaimPacket(int entityId, ResourceLocation claimantId, Optional<CompoundTag> data) implements MultiloaderPacket {
	public static final Type<ParticleClaimPacket> TYPE = new Type<>(TESConstants.id("particle_claim"));
	public static final StreamCodec<FriendlyByteBuf, ParticleClaimPacket> CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT,
			ParticleClaimPacket::entityId,
			ResourceLocation.STREAM_CODEC,
			ParticleClaimPacket::claimantId,
			ByteBufCodecs.OPTIONAL_COMPOUND_TAG,
			ParticleClaimPacket::data,
			ParticleClaimPacket::new);

	@Override
	public Type<ParticleClaimPacket> type() {
		return TYPE;
	}

	@Override
	public void receiveMessage(Player sender, Consumer<Runnable> workQueue) {
		workQueue.accept(() -> TESParticleManager.addParticleClaim(this.entityId, this.claimantId, this.data));
	}
}

package net.tslat.tes.core.networking.packet;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.tslat.tes.api.TESConstants;
import net.tslat.tes.core.particle.TESParticleManager;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public record ParticleClaimPacket(int entityId, ResourceLocation claimantId, @Nullable CompoundTag data) implements MultiloaderPacket {
	public static final ResourceLocation ID = new ResourceLocation(TESConstants.MOD_ID, "particle_claim");

	@Override
	public ResourceLocation id() {
		return ID;
	}

	@Override
	public void write(final FriendlyByteBuf buf) {
		buf.writeVarInt(this.entityId);
		buf.writeResourceLocation(this.claimantId);
		buf.writeNbt(this.data);
	}

	public static ParticleClaimPacket decode(final FriendlyByteBuf buf) {
		return new ParticleClaimPacket(buf.readVarInt(), buf.readResourceLocation(), buf.readNbt());
	}

	@Override
	public void receiveMessage(Player sender, Consumer<Runnable> workQueue) {
		workQueue.accept(() -> TESParticleManager.addParticleClaim(this.entityId, this.claimantId, this.data));
	}
}

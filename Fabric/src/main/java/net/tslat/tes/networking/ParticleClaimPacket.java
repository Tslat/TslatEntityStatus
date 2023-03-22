package net.tslat.tes.networking;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.tslat.tes.api.TESConstants;
import net.tslat.tes.core.particle.TESParticleManager;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class ParticleClaimPacket {
	public static final ResourceLocation ID = new ResourceLocation(TESConstants.MOD_ID, "particle_claim");

	private final int entityId;
	private final ResourceLocation claimantId;
	@Nullable
	private final CompoundTag data;

	public ParticleClaimPacket(final int entityId, final ResourceLocation claimantId, final @Nullable CompoundTag data) {
		this.entityId = entityId;
		this.claimantId = claimantId;
		this.data = data;
	}

	public void encode(final FriendlyByteBuf buf) {
		buf.writeVarInt(this.entityId);
		buf.writeResourceLocation(this.claimantId);
		buf.writeNbt(this.data);
	}

	public static ParticleClaimPacket decode(final FriendlyByteBuf buf) {
		return new ParticleClaimPacket(buf.readVarInt(), buf.readResourceLocation(), buf.readNbt());
	}

	public void handleMessage(Consumer<Runnable> queue) {
		queue.accept(() -> TESParticleManager.addParticleClaim(this.entityId, this.claimantId, this.data));
	}
}

package net.tslat.tes.networking;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import net.tslat.tes.core.particle.TESParticleManager;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class ParticleClaimPacket {
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

	public void handleMessage(Supplier<NetworkEvent.Context> context) {
		context.get().enqueueWork(() -> TESParticleManager.addParticleClaim(this.entityId, this.claimantId, this.data));
		context.get().setPacketHandled(true);
	}
}

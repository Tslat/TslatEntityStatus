package net.tslat.tes.networking;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import net.tslat.tes.core.particle.TESParticleManager;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class ParticleClaimPacket {
	private final int entityId;
	private final ResourceLocation claimantId;
	@Nullable
	private final CompoundNBT data;

	public ParticleClaimPacket(final int entityId, final ResourceLocation claimantId, final @Nullable CompoundNBT data) {
		this.entityId = entityId;
		this.claimantId = claimantId;
		this.data = data;
	}

	public void encode(final PacketBuffer buf) {
		buf.writeVarInt(this.entityId);
		buf.writeResourceLocation(this.claimantId);
		buf.writeNbt(this.data);
	}

	public static ParticleClaimPacket decode(final PacketBuffer buf) {
		return new ParticleClaimPacket(buf.readVarInt(), buf.readResourceLocation(), buf.readNbt());
	}

	public void handleMessage(Supplier<NetworkEvent.Context> context) {
		context.get().enqueueWork(() -> TESParticleManager.addParticleClaim(this.entityId, this.claimantId, this.data));
		context.get().setPacketHandled(true);
	}
}
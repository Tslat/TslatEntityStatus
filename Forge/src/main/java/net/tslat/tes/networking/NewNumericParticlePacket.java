package net.tslat.tes.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.tslat.tes.core.particle.TESParticleManager;
import net.tslat.tes.core.particle.type.NumericParticle;
import org.joml.Vector3f;

import java.util.function.Supplier;

public class NewNumericParticlePacket {
	private final double value;
	private final Vector3f position;
	private final int colour;

	public NewNumericParticlePacket(final double value, final Vector3f position, final int colour) {
		this.value = value;
		this.position = position;
		this.colour = colour;
	}

	public void encode(final FriendlyByteBuf buf) {
		buf.writeDouble(this.value);
		buf.writeVector3f(this.position);
		buf.writeVarInt(this.colour);
	}

	public static NewNumericParticlePacket decode(final FriendlyByteBuf buf) {
		return new NewNumericParticlePacket(buf.readDouble(), buf.readVector3f(), buf.readVarInt());
	}

	public void handleMessage(Supplier<NetworkEvent.Context> context) {
		context.get().enqueueWork(() -> {
			TESParticleManager.addParticle(new NumericParticle(null, this.position, this.value).withColour(this.colour));
		});
		context.get().setPacketHandled(true);
	}
}

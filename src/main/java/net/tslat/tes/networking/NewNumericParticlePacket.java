package net.tslat.tes.networking;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.fml.network.NetworkEvent;
import net.tslat.tes.core.particle.TESParticleManager;
import net.tslat.tes.core.particle.type.NumericParticle;

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

	public void encode(final PacketBuffer buf) {
		buf.writeDouble(this.value);
		buf.writeFloat(this.position.x());
		buf.writeFloat(this.position.y());
		buf.writeFloat(this.position.z());
		buf.writeVarInt(this.colour);
	}

	public static NewNumericParticlePacket decode(final PacketBuffer buf) {
		return new NewNumericParticlePacket(buf.readDouble(), new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat()), buf.readVarInt());
	}

	public void handleMessage(Supplier<NetworkEvent.Context> context) {
		context.get().enqueueWork(() -> TESParticleManager.addParticle(new NumericParticle(null, this.position, this.value).withColour(this.colour)));
		context.get().setPacketHandled(true);
	}
}
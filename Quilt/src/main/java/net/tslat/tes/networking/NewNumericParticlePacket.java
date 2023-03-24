package net.tslat.tes.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.tslat.tes.api.TESConstants;
import net.tslat.tes.core.particle.TESParticleManager;
import net.tslat.tes.core.particle.type.NumericParticle;
import org.joml.Vector3f;

import java.util.function.Consumer;

public class NewNumericParticlePacket {
	public static final ResourceLocation ID = new ResourceLocation(TESConstants.MOD_ID, "new_numeric_particle");

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
		buf.writeFloat(this.position.x());
		buf.writeFloat(this.position.y());
		buf.writeFloat(this.position.z());
		buf.writeVarInt(this.colour);
	}

	public static NewNumericParticlePacket decode(final FriendlyByteBuf buf) {
		return new NewNumericParticlePacket(buf.readDouble(), new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat()), buf.readVarInt());
	}

	public void handleMessage(Consumer<Runnable> queue) {
		queue.accept(() -> TESParticleManager.addParticle(new NumericParticle(null, this.position, this.value).withColour(this.colour)));
	}
}
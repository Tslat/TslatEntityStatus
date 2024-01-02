package net.tslat.tes.core.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.tslat.tes.api.TESConstants;
import net.tslat.tes.core.particle.TESParticleManager;
import net.tslat.tes.core.particle.type.NumericParticle;
import org.joml.Vector3f;

import java.util.function.Consumer;

public record NewNumericParticlePacket(double value, Vector3f position, int colour) implements MultiloaderPacket {
	public static final ResourceLocation ID = new ResourceLocation(TESConstants.MOD_ID, "new_numeric_particle");

	@Override
	public ResourceLocation id() {
		return ID;
	}

	@Override
	public void write(final FriendlyByteBuf buf) {
		buf.writeDouble(this.value);
		buf.writeVector3f(this.position);
		buf.writeVarInt(this.colour);
	}

	public static NewNumericParticlePacket decode(final FriendlyByteBuf buf) {
		return new NewNumericParticlePacket(buf.readDouble(), buf.readVector3f(), buf.readVarInt());
	}

	@Override
	public void receiveMessage(Player sender, Consumer<Runnable> workQueue) {
		workQueue.accept(() -> TESParticleManager.addParticle(new NumericParticle(null, this.position, this.value).withColour(this.colour)));
	}
}

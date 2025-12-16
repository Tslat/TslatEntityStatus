package net.tslat.tes.core.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.tslat.tes.api.TESConstants;
import net.tslat.tes.core.particle.TESParticleManager;
import net.tslat.tes.core.particle.type.NumericParticle;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.function.Consumer;

public record NewNumericParticlePacket(double value, Vector3fc position, int colour) implements MultiloaderPacket {
	public static final CustomPacketPayload.Type<NewNumericParticlePacket> TYPE = new Type<>(TESConstants.id("new_numeric_particle"));
	public static final StreamCodec<FriendlyByteBuf, NewNumericParticlePacket> CODEC = StreamCodec.composite(
			ByteBufCodecs.DOUBLE,
			NewNumericParticlePacket::value,
			ByteBufCodecs.VECTOR3F,
			NewNumericParticlePacket::position,
			ByteBufCodecs.VAR_INT,
			NewNumericParticlePacket::colour,
			NewNumericParticlePacket::new);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	@Override
	public void receiveMessage(Player sender, Consumer<Runnable> workQueue) {
		workQueue.accept(() -> TESParticleManager.addParticle(new NumericParticle(null, new Vector3f(this.position), this.value).withColour(this.colour)));
	}
}

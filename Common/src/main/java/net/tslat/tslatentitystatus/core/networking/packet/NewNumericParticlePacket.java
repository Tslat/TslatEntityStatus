package net.tslat.tslatentitystatus.core.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.tslat.tslatentitystatus.api.TESConstants;
import net.tslat.tslatentitystatus.api.client.util.TESClientParticleUtil;
import net.tslat.tslatentitystatus.core.networking.packet.base.MultiloaderPacket;
import net.tslat.tslatentitystatus.core.particle.type.NumericParticle;

import java.util.function.Consumer;

/// `TES` packet for adding a new numerical `TESParticle`
public record NewNumericParticlePacket(double value, Vec3 position, int colour) implements MultiloaderPacket {
	public static final CustomPacketPayload.Type<NewNumericParticlePacket> TYPE = new Type<>(TESConstants.id("new_numeric_particle"));
	public static final StreamCodec<FriendlyByteBuf, NewNumericParticlePacket> CODEC = StreamCodec.composite(
			ByteBufCodecs.DOUBLE, NewNumericParticlePacket::value,
			VEC3_CODEC, NewNumericParticlePacket::position,
			ByteBufCodecs.VAR_INT, NewNumericParticlePacket::colour,
			NewNumericParticlePacket::new);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	@Override
	public void receiveMessage(Player sender, Consumer<Runnable> workQueue) {
		workQueue.accept(() -> TESClientParticleUtil.addTESParticle(new NumericParticle(null, this.position.toVector3f(), this.value).withColour(this.colour)));
	}
}

package net.tslat.tes.core.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.tslat.tes.api.TESConstants;

import java.util.function.Consumer;

public record ServerHandshakePacket() implements MultiloaderPacket {
	public static final Type<ServerHandshakePacket> TYPE = new Type<>(TESConstants.id("server_handshake"));
	public static final StreamCodec<FriendlyByteBuf, ServerHandshakePacket> CODEC = StreamCodec.unit(new ServerHandshakePacket());

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	@Override
	public void receiveMessage(Player sender, Consumer<Runnable> workQueue) {
		TESConstants.HAS_SERVER_CONNECTION = true;
	}
}

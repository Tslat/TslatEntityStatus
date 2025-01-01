package net.tslat.tes.core.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.tslat.tes.api.TESConstants;
import net.tslat.tes.core.networking.ServerConnectionAckTask;

public record ServerConnectionAckPacket() implements MultiloaderConfigurationPacket {
	public static final Type<ServerConnectionAckPacket> TYPE = new Type<>(TESConstants.id("server_handshake"));
	public static final StreamCodec<FriendlyByteBuf, ServerConnectionAckPacket> CODEC = StreamCodec.unit(new ServerConnectionAckPacket());

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	@Override
	public void handleTask(TaskHandler handler) {
		handler.sendResponse(this);
		handler.markTaskComplete(ServerConnectionAckTask.TYPE);
	}
}

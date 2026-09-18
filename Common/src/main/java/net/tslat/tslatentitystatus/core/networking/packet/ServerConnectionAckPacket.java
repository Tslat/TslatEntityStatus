package net.tslat.tslatentitystatus.core.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.tslat.tslatentitystatus.api.TESConstants;
import net.tslat.tslatentitystatus.core.networking.ServerConnectionAckTask;
import net.tslat.tslatentitystatus.core.networking.packet.base.MultiloaderConfigurationPacket;

/// Configuration-stage `TES` packet to handle the server handshake for server-side `TES` functionality
public record ServerConnectionAckPacket() implements MultiloaderConfigurationPacket {
	public static final Type<ServerConnectionAckPacket> TYPE = new Type<>(TESConstants.id("server_handshake"));
	public static final StreamCodec<FriendlyByteBuf, ServerConnectionAckPacket> CODEC = StreamCodec.unit(new ServerConnectionAckPacket());

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	@Override
	public void handleTask(TaskHandler handler) {
		TESConstants.PLATFORM.setSyncingEffects(true);
		handler.sendResponse(this);
		handler.markTaskComplete(ServerConnectionAckTask.TYPE);
	}
}

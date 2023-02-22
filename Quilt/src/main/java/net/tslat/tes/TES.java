package net.tslat.tes;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.tslat.tes.networking.RequestEffectsPacket;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.networking.api.PacketSender;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;

public class TES implements ModInitializer {
	@Override
	public void onInitialize(ModContainer mod) {
		ServerPlayNetworking.registerGlobalReceiver(RequestEffectsPacket.ID, (server, player, handler, buf, responseSender) -> RequestEffectsPacket.decode(buf).handleMessage(server::submit));
	}
}

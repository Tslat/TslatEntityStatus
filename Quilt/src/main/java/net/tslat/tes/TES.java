package net.tslat.tes;

import net.tslat.tes.networking.RequestEffectsPacket;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;

public class TES implements ModInitializer {
	@Override
	public void onInitialize(ModContainer mod) {
		ServerPlayNetworking.registerGlobalReceiver(RequestEffectsPacket.ID, (server, player, handler, buf, responseSender) -> RequestEffectsPacket.decode(buf).handleMessage(player, server::submit));
	}
}

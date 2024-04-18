package net.tslat.tes;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.tslat.tes.api.TESConstants;
import net.tslat.tes.networking.RequestEffectsPacket;

public class TES implements ModInitializer {
	@Override
	public void onInitialize() {
		ServerLifecycleEvents.SERVER_STARTED.register(server -> TESConstants.UTILS.clearDynamicCaches());
		ServerPlayNetworking.registerGlobalReceiver(RequestEffectsPacket.ID, (server, player, handler, buf, responseSender) -> RequestEffectsPacket.decode(buf).handleMessage(player, server::submit));
	}
}

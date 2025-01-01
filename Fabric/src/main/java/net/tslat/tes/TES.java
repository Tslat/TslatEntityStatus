package net.tslat.tes;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.tslat.tes.api.TESConstants;
import net.tslat.tes.core.networking.ServerConnectionAckTask;
import net.tslat.tes.core.networking.TESNetworking;
import net.tslat.tes.core.networking.packet.ServerConnectionAckPacket;

public class TES implements ModInitializer {
	@Override
	public void onInitialize() {
		TESNetworking.init();
		ServerLifecycleEvents.SERVER_STARTED.register(server -> TESConstants.UTILS.clearDynamicCaches());
		ServerConfigurationConnectionEvents.CONFIGURE.register((handler, server) -> {
			if (ServerConfigurationNetworking.canSend(handler, ServerConnectionAckPacket.TYPE))
				handler.addTask(new ServerConnectionAckTask(() -> true));
		});
	}
}

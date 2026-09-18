package net.tslat.tslatentitystatus;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.tslat.tslatentitystatus.core.networking.ServerConnectionAckTask;
import net.tslat.tslatentitystatus.core.networking.TESNetworking;
import net.tslat.tslatentitystatus.core.networking.packet.ServerConnectionAckPacket;

public class TES implements ModInitializer {
	@Override
	public void onInitialize() {
		TESNetworking.init();
		ServerConfigurationConnectionEvents.CONFIGURE.register((handler, _) -> {
			if (ServerConfigurationNetworking.canSend(handler, ServerConnectionAckPacket.TYPE))
				handler.addTask(new ServerConnectionAckTask(() -> true));
		});
	}
}

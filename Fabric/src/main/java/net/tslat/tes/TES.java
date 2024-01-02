package net.tslat.tes;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.tslat.tes.api.TESConstants;
import net.tslat.tes.core.networking.TESNetworking;

public class TES implements ModInitializer {
	@Override
	public void onInitialize() {
		TESNetworking.init();
		ServerLifecycleEvents.SERVER_STARTED.register(server -> TESConstants.UTILS.clearDynamicCaches());
	}
}

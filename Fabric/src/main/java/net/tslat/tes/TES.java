package net.tslat.tes;

import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.tslat.tes.api.TESConstants;
import net.tslat.tes.config.TESConfig;

public class TES implements ModInitializer {
	@Override
	public void onInitialize() {
		MidnightConfig.init(TESConstants.MOD_ID, TESConfig.class);
		ServerLifecycleEvents.SERVER_STARTED.register(server -> TESConstants.UTILS.clearDynamicCaches());
	}
}

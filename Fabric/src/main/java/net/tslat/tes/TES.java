package net.tslat.tes;

import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.api.ModInitializer;
import net.tslat.tes.api.TESConstants;
import net.tslat.tes.config.TESConfig;

public class TES implements ModInitializer {
	@Override
	public void onInitialize() {
		MidnightConfig.init(TESConstants.MOD_ID, TESConfig.class);
		TESConstants.setConfig(new TESConfig());
	}
}

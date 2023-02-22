package net.tslat.tes;

import net.minecraftforge.fml.common.Mod;
import net.tslat.tes.api.TESConstants;
import net.tslat.tes.config.TESConfig;
import net.tslat.tes.networking.TESNetworking;

@Mod(TESConstants.MOD_ID)
public class TES {
	public TES() {
		TESConfig.init();
		TESNetworking.init();
	}
}

package net.tslat.tes;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.tslat.tes.api.TESConstants;
import net.tslat.tes.config.TESConfig;
import net.tslat.tes.networking.TESNetworking;

@Mod(TESConstants.MOD_ID)
public class TES {
	public TES() {
		TESConfig.init();
		TESNetworking.init();

		FMLJavaModLoadingContext.get().getModEventBus().addListener(TES::clientInit);
	}

	private static void clientInit(final FMLClientSetupEvent ev) {
		TESConstants.setIsClient();
	}
}

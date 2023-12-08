package net.tslat.tes;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.tslat.tes.api.TESConstants;
import net.tslat.tes.config.TESConfig;
import net.tslat.tes.networking.TESNetworking;

@Mod(TESConstants.MOD_ID)
public class TES {
	public TES() {
		TESNetworking.init();

		if (FMLEnvironment.dist == Dist.CLIENT)
			TESConfig.init();

		FMLJavaModLoadingContext.get().getModEventBus().addListener(TES::clientInit);
		NeoForge.EVENT_BUS.addListener(TES::serverStart);
	}

	private static void clientInit(final FMLClientSetupEvent ev) {
		TESConstants.setIsClient();
	}

	private static void serverStart(final ServerStartedEvent ev) {
		TESConstants.UTILS.clearDynamicCaches();
	}
}

package net.tslat.tes;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.fml.DistExecutor;
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
		DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> TESConfig::init);

		FMLJavaModLoadingContext.get().getModEventBus().addListener(TES::clientInit);
		MinecraftForge.EVENT_BUS.addListener(TES::serverStart);
	}

	private static void clientInit(final FMLClientSetupEvent ev) {
		TESConstants.setIsClient();
	}

	private static void serverStart(final ServerStartedEvent ev) {
		TESConstants.UTILS.clearDynamicCaches();
	}
}

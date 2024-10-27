package net.tslat.tes;

import fuzs.forgeconfigapiport.forge.api.neoforge.v4.NeoForgeConfigRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.tslat.tes.api.TESConfig;
import net.tslat.tes.api.TESConstants;
import net.tslat.tes.networking.TESNetworking;

@Mod(TESConstants.MOD_ID)
public class TES {
	public TES(FMLJavaModLoadingContext context) {
		if (FMLEnvironment.dist == Dist.CLIENT)
			NeoForgeConfigRegistry.INSTANCE.register(TESConstants.MOD_ID, ModConfig.Type.CLIENT, TESConfig.init());

		TESNetworking.init();
		context.getModEventBus().addListener(TES::clientInit);
		MinecraftForge.EVENT_BUS.addListener(TES::serverStart);
	}

	private static void clientInit(final FMLClientSetupEvent ev) {
		TESConstants.setIsClient();
	}

	private static void serverStart(final ServerStartedEvent ev) {
		TESConstants.UTILS.clearDynamicCaches();
	}
}

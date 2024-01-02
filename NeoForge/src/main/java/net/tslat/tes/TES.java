package net.tslat.tes;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;
import net.tslat.tes.api.TESConstants;
import net.tslat.tes.config.TESConfig;
import net.tslat.tes.core.networking.TESNetworking;

@Mod(TESConstants.MOD_ID)
public class TES {
	public static IPayloadRegistrar packetRegistrar = null;

	public TES(IEventBus modBus) {
		if (FMLEnvironment.dist == Dist.CLIENT)
			TESConfig.init();

		modBus.addListener(TES::clientInit);
		modBus.addListener(TES::networkingInit);
		NeoForge.EVENT_BUS.addListener(TES::serverStart);
	}

	private static void clientInit(final FMLClientSetupEvent ev) {
		TESConstants.setIsClient();
	}

	private static void networkingInit(final RegisterPayloadHandlerEvent ev) {
		packetRegistrar = ev.registrar(TESConstants.MOD_ID);
		TESNetworking.init();
		packetRegistrar = null;
	}

	private static void serverStart(final ServerStartedEvent ev) {
		TESConstants.UTILS.clearDynamicCaches();
	}
}

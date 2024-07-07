package net.tslat.tes;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.tslat.tes.api.TESConstants;
import net.tslat.tes.config.TESConfig;
import net.tslat.tes.core.networking.TESNetworking;

@Mod(TESConstants.MOD_ID)
public class TES {
	public static PayloadRegistrar packetRegistrar = null;

	public TES(ModContainer modContainer, IEventBus modBus) {
		if (FMLEnvironment.dist == Dist.CLIENT)
			TESConfig.init(modContainer::registerConfig);

		modBus.addListener(TES::clientInit);
		modBus.addListener(TES::networkingInit);
		NeoForge.EVENT_BUS.addListener(TES::serverStart);
	}

	private static void clientInit(final FMLClientSetupEvent ev) {
		TESConstants.setIsClient();
	}

	private static void networkingInit(final RegisterPayloadHandlersEvent ev) {
		packetRegistrar = ev.registrar(TESConstants.MOD_ID).optional();
		TESNetworking.init();
		packetRegistrar = null;
	}

	private static void serverStart(final ServerStartedEvent ev) {
		TESConstants.UTILS.clearDynamicCaches();
	}
}

package net.tslat.tslatentitystatus;

import net.minecraft.network.protocol.configuration.ServerConfigurationPacketListener;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.event.RegisterConfigurationTasksEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.tslat.tslatentitystatus.api.TESConstants;
import net.tslat.tslatentitystatus.core.networking.ServerConnectionAckTask;
import net.tslat.tslatentitystatus.core.networking.TESNetworking;
import net.tslat.tslatentitystatus.core.networking.packet.ServerConnectionAckPacket;

@Mod(TESConstants.MOD_ID)
public class TES {
	public static PayloadRegistrar packetRegistrar = null;

	public TES(ModContainer modContainer, IEventBus modBus) {
		modBus.addListener(TES::serverHandshake);
		modBus.addListener(TES::networkingInit);
	}

	private static void serverHandshake(final RegisterConfigurationTasksEvent ev) {
		final ServerConfigurationPacketListener listener = ev.getListener();

		ev.register(new ServerConnectionAckTask(() -> listener.hasChannel(ServerConnectionAckPacket.TYPE)));
	}

	private static void networkingInit(final RegisterPayloadHandlersEvent ev) {
		packetRegistrar = ev.registrar(TESConstants.MOD_ID).optional();
		TESNetworking.init();
		packetRegistrar = null;
	}
}

package net.tslat.tslatentitystatus;

import fuzs.forgeconfigapiport.forge.api.v5.NeoForgeConfigRegistry;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraftforge.client.event.AddGuiOverlayLayersEvent;
import net.minecraftforge.event.network.GatherLoginConfigurationTasksEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.config.ConfigurationTaskContext;
import net.tslat.tslatentitystatus.api.TESConfig;
import net.tslat.tslatentitystatus.api.TESConstants;
import net.tslat.tslatentitystatus.core.networking.ServerConnectionAckTask;
import net.tslat.tslatentitystatus.networking.TESForgeNetworking;

import java.util.function.Consumer;

@Mod(TESConstants.MOD_ID)
public class TES {
	public TES(FMLJavaModLoadingContext context) {
		TESForgeNetworking.init();
		AddGuiOverlayLayersEvent.BUS.addListener(TESClient::registerHudLayer);
		GatherLoginConfigurationTasksEvent.BUS.addListener(TES::serverHandshake);
		NeoForgeConfigRegistry.INSTANCE.register(TESConstants.MOD_ID, ModConfig.Type.CLIENT, TESConfig.init());
	}

	private static void serverHandshake(final GatherLoginConfigurationTasksEvent ev) {
		ev.addTask(new ConfigurationTask() {
			final ServerConnectionAckTask wrappedTask = new ServerConnectionAckTask(() -> true);

			@Override
			public void start(Consumer<Packet<?>> consumer) {
				this.wrappedTask.start(consumer);
			}

			@Override
			public void start(ConfigurationTaskContext context) {
				start(packet -> {
					if (packet instanceof ClientboundCustomPayloadPacket(CustomPacketPayload payload)) {
						TESForgeNetworking.CHANNEL.send(payload, context.getConnection());
					}
					else {
						context.send(packet);
					}
				});
				context.finish(type());
			}

			@Override
			public Type type() {
				return this.wrappedTask.type();
			}
		});
	}
}

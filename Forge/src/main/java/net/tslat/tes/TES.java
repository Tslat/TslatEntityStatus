package net.tslat.tes;

import fuzs.forgeconfigapiport.forge.api.v5.NeoForgeConfigRegistry;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.network.GatherLoginConfigurationTasksEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.config.ConfigurationTaskContext;
import net.tslat.tes.api.TESConfig;
import net.tslat.tes.api.TESConstants;
import net.tslat.tes.core.networking.ServerConnectionAckTask;
import net.tslat.tes.networking.TESNetworking;

import java.util.function.Consumer;

@Mod(TESConstants.MOD_ID)
public class TES {
	public TES(FMLJavaModLoadingContext context) {
		TESNetworking.init();
		context.getModEventBus().addListener(TES::clientInit);
		MinecraftForge.EVENT_BUS.addListener(TES::serverStart);
		MinecraftForge.EVENT_BUS.addListener(TES::serverHandshake);
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
				start(context::send);
				context.finish(type());
			}

			@Override
			public Type type() {
				return this.wrappedTask.type();
			}
		});
	}

	private static void clientInit(final FMLClientSetupEvent ev) {
		TESConstants.setIsClient();
		NeoForgeConfigRegistry.INSTANCE.register(TESConstants.MOD_ID, ModConfig.Type.CLIENT, TESConfig.init());
	}

	private static void serverStart(final ServerStartedEvent ev) {
		TESConstants.UTILS.clearDynamicCaches();
	}
}

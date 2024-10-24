package net.tslat.tes;

import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.fml.config.ModConfig;
import net.tslat.tes.api.TESConfig;
import net.tslat.tes.api.TESConstants;
import net.tslat.tes.core.networking.packet.MultiloaderPacket;
import org.jetbrains.annotations.ApiStatus;

public class TESClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		TESConstants.setIsClient();
		NeoForgeConfigRegistry.INSTANCE.register(TESConstants.MOD_ID, ModConfig.Type.CLIENT, TESConfig.init());
	}

	public static void sendPacket(CustomPacketPayload packet) {
		ClientPlayNetworking.send(packet);
	}

	@ApiStatus.Internal
	public static <P extends MultiloaderPacket> void registerPacket(CustomPacketPayload.Type<P> packetType) {
		ClientPlayNetworking.registerGlobalReceiver(packetType, (packet, context) -> packet.receiveMessage(context.player(), context.client()::execute));
	}
}

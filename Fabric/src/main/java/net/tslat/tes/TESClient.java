package net.tslat.tes;

import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.tslat.tes.api.TESConstants;
import net.tslat.tes.config.TESConfig;
import net.tslat.tes.core.networking.packet.*;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

public class TESClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		TESConstants.setIsClient();
		MidnightConfig.init(TESConstants.MOD_ID, TESConfig.class);
		TESConstants.setConfig(new TESConfig());
	}

	public static void sendPacket(ResourceLocation packetId, FriendlyByteBuf buffer) {
		ClientPlayNetworking.send(packetId, buffer);
	}

	@ApiStatus.Internal
	public static <P extends MultiloaderPacket> void registerPacket(ResourceLocation id, Function<FriendlyByteBuf, P> decoder) {
		ClientPlayNetworking.registerGlobalReceiver(id, (client, handler, buf, responseSender) -> decoder.apply(buf).receiveMessage(client.player, client::execute));
	}
}

package net.tslat.tes;

import eu.midnightdust.lib.config.MidnightConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.tslat.tes.api.TESConstants;
import net.tslat.tes.config.TESConfig;
import net.tslat.tes.networking.SyncEffectsPacket;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;

public class TESClient implements ClientModInitializer {
	@Override
	public void onInitializeClient(ModContainer mod) {
		MidnightConfig.init(TESConstants.MOD_ID, TESConfig.class);
		TESConstants.setConfig(new TESConfig());

		ClientPlayNetworking.registerGlobalReceiver(SyncEffectsPacket.ID, (client, handler, buf, responseSender) -> SyncEffectsPacket.decode(buf).handleMessage(client::submit));
	}

	public static void sendPacket(ResourceLocation packetId, FriendlyByteBuf buffer) {
		ClientPlayNetworking.send(packetId, buffer);
	}
}

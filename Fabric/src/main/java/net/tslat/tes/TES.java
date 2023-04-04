package net.tslat.tes;

import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.tslat.tes.api.TESConstants;
import net.tslat.tes.config.TESConfig;
import net.tslat.tes.networking.RequestEffectsPacket;

public class TES implements ModInitializer {
	@Override
	public void onInitialize() {
		MidnightConfig.init(TESConstants.MOD_ID, TESConfig.class);
		TESConstants.setConfig(new TESConfig());

		ServerPlayNetworking.registerGlobalReceiver(RequestEffectsPacket.ID, (server, player, handler, buf, responseSender) -> RequestEffectsPacket.decode(buf).handleMessage(player, server::submit));
	}
}

package net.tslat.tes;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.tslat.tes.api.TESConstants;
import net.tslat.tes.networking.NewComponentParticlePacket;
import net.tslat.tes.networking.NewNumericParticlePacket;
import net.tslat.tes.networking.ParticleClaimPacket;
import net.tslat.tes.networking.SyncEffectsPacket;

public class TESClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		TESConstants.setIsClient();

		ClientPlayNetworking.registerGlobalReceiver(SyncEffectsPacket.ID, (client, handler, buf, responseSender) -> SyncEffectsPacket.decode(buf).handleMessage(client::submit));
		ClientPlayNetworking.registerGlobalReceiver(ParticleClaimPacket.ID, (client, handler, buf, responseSender) -> ParticleClaimPacket.decode(buf).handleMessage(client::submit));
		ClientPlayNetworking.registerGlobalReceiver(NewComponentParticlePacket.ID, (client, handler, buf, responseSender) -> NewComponentParticlePacket.decode(buf).handleMessage(client::submit));
		ClientPlayNetworking.registerGlobalReceiver(NewNumericParticlePacket.ID, (client, handler, buf, responseSender) -> NewNumericParticlePacket.decode(buf).handleMessage(client::submit));
	}

	public static void sendPacket(ResourceLocation packetId, FriendlyByteBuf buffer) {
		ClientPlayNetworking.send(packetId, buffer);
	}
}

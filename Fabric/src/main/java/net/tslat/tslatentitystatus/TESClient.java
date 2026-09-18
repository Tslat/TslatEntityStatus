package net.tslat.tslatentitystatus;

import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.impl.client.rendering.hud.HudElementRegistryImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.fml.config.ModConfig;
import net.tslat.tslatentitystatus.api.TESConfig;
import net.tslat.tslatentitystatus.api.TESConstants;
import net.tslat.tslatentitystatus.core.client.hud.TESHud;
import net.tslat.tslatentitystatus.core.networking.packet.base.MultiloaderConfigurationPacket;
import net.tslat.tslatentitystatus.core.networking.packet.base.MultiloaderPacket;
import org.jetbrains.annotations.ApiStatus;

public class TESClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ConfigRegistry.INSTANCE.register(TESConstants.MOD_ID, ModConfig.Type.CLIENT, TESConfig.init());
        //noinspection UnstableApiUsage
        HudElementRegistryImpl.attachElementAfter(VanillaHudElements.MOB_EFFECTS, TESConstants.id("tes_hud"), TESHud::submitHudRenderTasks);
		ClientPlayConnectionEvents.DISCONNECT.register((_, _) -> TESConstants.PLATFORM.setSyncingEffects(false));
	}

	public static void sendPacket(CustomPacketPayload packet) {
		if (Minecraft.getInstance().getConnection() != null)
			ClientPlayNetworking.send(packet);
	}

	@ApiStatus.Internal
	public static <P extends MultiloaderConfigurationPacket> void registerConfigurationPacket(CustomPacketPayload.Type<P> packetType) {
		ClientConfigurationNetworking.registerGlobalReceiver(packetType, (packet, context) -> packet.handleTask(new MultiloaderConfigurationPacket.TaskHandler(context.responseSender()::sendPacket, type -> {})));
	}

	@ApiStatus.Internal
	public static <P extends MultiloaderPacket> void registerPacket(CustomPacketPayload.Type<P> packetType) {
		ClientPlayNetworking.registerGlobalReceiver(packetType, (packet, context) -> packet.receiveMessage(context.player(), context.client()::execute));
	}
}

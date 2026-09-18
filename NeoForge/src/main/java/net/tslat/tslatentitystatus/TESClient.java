package net.tslat.tslatentitystatus;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.tslat.tslatentitystatus.api.TESConfig;
import net.tslat.tslatentitystatus.api.TESConstants;
import net.tslat.tslatentitystatus.core.client.hud.TESHud;
import net.tslat.tslatentitystatus.core.networking.packet.base.MultiloaderPacket;

@Mod(value = TESConstants.MOD_ID, dist = Dist.CLIENT)
public class TESClient {
    public TESClient(ModContainer modContainer, IEventBus modBus) {
        modContainer.registerConfig(ModConfig.Type.CLIENT, TESConfig.init());
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        modBus.addListener(TESClient::registerHudLayer);
    }

    private static void registerHudLayer(final RegisterGuiLayersEvent ev) {
        ev.registerAbove(VanillaGuiLayers.EFFECTS, TESConstants.HUD_LAYER_ID, TESHud::submitHudRenderTasks);
    }

    public static void sendServerboundPacket(MultiloaderPacket packet) {
        if (Minecraft.getInstance().getConnection() != null)
            ClientPacketDistributor.sendToServer(packet);
    }
}

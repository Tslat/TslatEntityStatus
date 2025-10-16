package net.tslat.tes;

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
import net.tslat.tes.api.TESConfig;
import net.tslat.tes.api.TESConstants;
import net.tslat.tes.core.hud.TESHud;

@Mod(value = TESConstants.MOD_ID, dist = Dist.CLIENT)
public class TESClient {
    public TESClient(ModContainer modContainer, IEventBus modBus) {
        TESConstants.setIsClient();

        modContainer.registerConfig(ModConfig.Type.CLIENT, TESConfig.init());
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        modBus.addListener(TESClient::registerHudLayer);
    }

    private static void registerHudLayer(final RegisterGuiLayersEvent ev) {
        ev.registerAbove(VanillaGuiLayers.EFFECTS, TESConstants.HUD_LAYER_ID, (guiGraphics, deltaTracker) ->
                                    TESHud.submitHudRenderTasks(guiGraphics, Minecraft.getInstance(), deltaTracker));
    }
}

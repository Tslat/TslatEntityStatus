package net.tslat.tes;

import fuzs.forgeconfigapiport.forge.api.v5.NeoForgeConfigRegistry;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.AddGuiOverlayLayersEvent;
import net.minecraftforge.client.gui.overlay.ForgeLayeredDraw;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.tslat.tes.api.TESConfig;
import net.tslat.tes.api.TESConstants;
import net.tslat.tes.core.hud.TESHud;

public class TESClient {
    static void clientSetup(final FMLClientSetupEvent event) {
        TESConstants.setIsClient();
        NeoForgeConfigRegistry.INSTANCE.register(TESConstants.MOD_ID, ModConfig.Type.CLIENT, TESConfig.init());
        AddGuiOverlayLayersEvent.BUS.addListener(TESClient::registerHudLayer);
    }

    static void registerHudLayer(final AddGuiOverlayLayersEvent ev) {
        ev.getLayeredDraw().addAbove(TESConstants.HUD_LAYER_ID, ForgeLayeredDraw.POTION_EFFECTS, (guiGraphics, deltaTracker) ->
                                             TESHud.submitHudRenderTasks(guiGraphics, Minecraft.getInstance(), deltaTracker));
    }
}

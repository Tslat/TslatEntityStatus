package net.tslat.tes;

import net.minecraftforge.client.event.AddGuiOverlayLayersEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.tslat.tes.api.TESConstants;
import net.tslat.tes.core.hud.TESHud;

public class TESClient {
    static void clientSetup(final FMLClientSetupEvent event) {
        TESConstants.setIsClient();
    }

    static void registerHudLayer(final AddGuiOverlayLayersEvent ev) {
        ev.getLayeredDraw().add(TESConstants.HUD_LAYER_ID, TESHud::submitHudRenderTasks);
    }
}

package net.tslat.tslatentitystatus;

import net.minecraftforge.client.event.AddGuiOverlayLayersEvent;
import net.tslat.tslatentitystatus.api.TESConstants;
import net.tslat.tslatentitystatus.core.client.hud.TESHud;

public class TESClient {
    static void registerHudLayer(final AddGuiOverlayLayersEvent ev) {
        ev.getLayeredDraw().add(TESConstants.HUD_LAYER_ID, TESHud::submitHudRenderTasks);
    }
}

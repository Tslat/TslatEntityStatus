package net.tslat.tslatentitystatus;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.tslat.tslatentitystatus.api.TESConstants;

/// Runtime client-side events for NeoForge
@EventBusSubscriber(value = Dist.CLIENT)
public final class TESClientEvents {
    @SubscribeEvent
    public static void onPlayerDisconnect(ClientPlayerNetworkEvent.LoggingOut ev) {
        TESConstants.PLATFORM.setSyncingEffects(false);
    }
}

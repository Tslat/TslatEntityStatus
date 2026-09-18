package net.tslat.tslatentitystatus;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.tslat.tslatentitystatus.api.TESConstants;

/// Runtime client-side events for Forge
@Mod.EventBusSubscriber(value = Dist.CLIENT)
public final class TESClientEvents {
    @SubscribeEvent
    public static void onPlayerDisconnect(ClientPlayerNetworkEvent.LoggingOut ev) {
        TESConstants.PLATFORM.setSyncingEffects(false);
    }
}

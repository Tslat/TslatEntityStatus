package net.tslat.tes.core.networking.packet;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;

public interface MultiloaderPacket extends CustomPacketPayload {
    /**
     * Handle the message after being received and decoded.<br>
     * Your packet should have its instance-values populated at this stage.<br>
     * This method is side-agnostic, so make sure you call out to client proxies as needed
     */
    void receiveMessage(Player sender, Consumer<Runnable> workQueue);
}

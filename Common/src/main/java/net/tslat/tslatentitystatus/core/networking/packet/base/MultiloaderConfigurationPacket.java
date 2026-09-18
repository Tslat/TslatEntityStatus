package net.tslat.tslatentitystatus.core.networking.packet.base;

import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

/// Base interface for configuration-stage packets for `TES`
///
/// These packets will be sent during the configuration handshake stage of world-join
public interface MultiloaderConfigurationPacket extends MultiloaderPacket {
    /// @see #handleTask(TaskHandler)
    @Override
    @ApiStatus.Internal
    @Deprecated
    default void receiveMessage(@Nullable Player sender, @Nullable Consumer<Runnable> workQueue) {}

    /// Called when the packet is received on the receiving side
    ///
    /// Run the relevant task(s) for this configuration packet
    void handleTask(TaskHandler handler);

    /// Helper container of callback points for configuration packet handling
    ///
    /// @param replyHandler          A packet consumer to send a reply packet back to the opposing network side
    /// @param taskCompletionHandler
    record TaskHandler(Consumer<MultiloaderConfigurationPacket> replyHandler, Consumer<ConfigurationTask.Type> taskCompletionHandler) {
        /// Send a reply packet back to the opposing network side
        ///
        /// This should be used for packet transmission during the configuration stage as it is run without waiting for the configuration
        /// stage to complete
        public void sendResponse(MultiloaderConfigurationPacket packet) {
            this.replyHandler.accept(packet);
        }

        /// Mark the provided configuration stage task as complete, allowing the handshake to progress
        public void markTaskComplete(ConfigurationTask.Type taskType) {
            this.taskCompletionHandler.accept(taskType);
        }
    }
}

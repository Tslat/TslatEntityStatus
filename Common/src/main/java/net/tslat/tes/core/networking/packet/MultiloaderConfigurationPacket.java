package net.tslat.tes.core.networking.packet;

import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface MultiloaderConfigurationPacket extends MultiloaderPacket {
    @Override
    @ApiStatus.Internal
    default void receiveMessage(@Nullable Player sender, @Nullable Consumer<Runnable> workQueue) {}

    void handleTask(TaskHandler handler);

    record TaskHandler(Consumer<MultiloaderConfigurationPacket> replyHandler, Consumer<ConfigurationTask.Type> taskCompletionHandler) {
        void sendResponse(MultiloaderConfigurationPacket packet) {
            this.replyHandler.accept(packet);
        }

        void markTaskComplete(ConfigurationTask.Type taskType) {
            this.taskCompletionHandler.accept(taskType);
        }
    }
}

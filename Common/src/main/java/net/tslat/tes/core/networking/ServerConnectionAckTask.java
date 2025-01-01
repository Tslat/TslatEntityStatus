package net.tslat.tes.core.networking;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.server.network.ConfigurationTask;
import net.tslat.tes.core.networking.packet.ServerConnectionAckPacket;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public record ServerConnectionAckTask(BooleanSupplier shouldSend) implements ConfigurationTask {
    public static final Type TYPE = new Type("tes_server_connection_ack");

    @Override
    public void start(Consumer<Packet<?>> consumer) {
        if (this.shouldSend.getAsBoolean())
            consumer.accept(new ClientboundCustomPayloadPacket(new ServerConnectionAckPacket()));
    }

    @Override
    public Type type() {
        return TYPE;
    }
}

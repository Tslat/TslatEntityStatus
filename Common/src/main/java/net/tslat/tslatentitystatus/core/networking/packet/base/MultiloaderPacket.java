package net.tslat.tslatentitystatus.core.networking.packet.base;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.function.Consumer;

/// Base interface for runtime packets for `TES`
public interface MultiloaderPacket extends CustomPacketPayload {
    /// Basic [StreamCodec] for a [Vec3]
    StreamCodec<FriendlyByteBuf, Vec3> VEC3_CODEC = new StreamCodec<>() {
        @Override
        public Vec3 decode(FriendlyByteBuf buffer) {
            return new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
        }

        @Override
        public void encode(FriendlyByteBuf buffer, Vec3 vec3) {
            buffer.writeDouble(vec3.x);
            buffer.writeDouble(vec3.y);
            buffer.writeDouble(vec3.z);
        }
    };

    /// Handle the message after being received and decoded
    ///
    /// This method is side-agnostic, so make sure you call out to client proxies as needed
    void receiveMessage(Player sender, Consumer<Runnable> workQueue);
}

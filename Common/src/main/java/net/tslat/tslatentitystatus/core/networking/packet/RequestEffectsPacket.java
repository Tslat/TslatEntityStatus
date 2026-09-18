package net.tslat.tslatentitystatus.core.networking.packet;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.Holder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.tslat.tslatentitystatus.api.TESConstants;
import net.tslat.tslatentitystatus.core.networking.packet.base.MultiloaderPacket;

import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;

/// Serverbound `TES` packet to request the current [MobEffect] collection applied to this entity
///
/// This is used to sync the visible effects down to the client side for HUD rendering
public record RequestEffectsPacket(int entityId) implements MultiloaderPacket {
    public static final CustomPacketPayload.Type<RequestEffectsPacket> TYPE = new Type<>(TESConstants.id("request_effects"));
    public static final StreamCodec<FriendlyByteBuf, RequestEffectsPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, RequestEffectsPacket::entityId,
            RequestEffectsPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void receiveMessage(Player sender, Consumer<Runnable> workQueue) {
        workQueue.accept(() -> {
            if (sender instanceof ServerPlayer pl && pl.level().getEntity(this.entityId) instanceof LivingEntity livingEntity) {
                final Collection<MobEffectInstance> effects = livingEntity.getActiveEffects();
                final Set<Holder<MobEffect>> ids = new ObjectOpenHashSet<>(effects.size());

                for (MobEffectInstance instance : effects) {
                    ids.add(instance.getEffect());
                }

                TESConstants.NETWORKING.sendEffectsSync(pl, this.entityId, ids, Set.of());
            }
        });
    }
}

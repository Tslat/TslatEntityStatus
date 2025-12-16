package net.tslat.tes.core.networking.packet;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.Holder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.tslat.tes.api.TESConstants;

import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;

public record RequestEffectsPacket(int entityId) implements MultiloaderPacket {
    public static final CustomPacketPayload.Type<RequestEffectsPacket> TYPE = new Type<>(TESConstants.id("request_effects"));
    public static final StreamCodec<FriendlyByteBuf, RequestEffectsPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            RequestEffectsPacket::entityId,
            RequestEffectsPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void receiveMessage(Player sender, Consumer<Runnable> workQueue) {
        workQueue.accept(() -> {
            Entity entity = sender.level().getEntity(this.entityId);

            if (entity instanceof LivingEntity livingEntity) {
                Collection<MobEffectInstance> effects = livingEntity.getActiveEffects();
                Set<Holder<MobEffect>> ids = new ObjectOpenHashSet<>(effects.size());

                for (MobEffectInstance instance : effects) {
                    if (instance.isVisible() || instance.showIcon())
                        ids.add(instance.getEffect());
                }

                TESConstants.NETWORKING.sendEffectsSync((ServerPlayer)sender, this.entityId, ids, Set.of());
            }
        });
    }
}

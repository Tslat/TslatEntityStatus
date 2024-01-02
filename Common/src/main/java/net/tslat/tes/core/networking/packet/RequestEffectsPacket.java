package net.tslat.tes.core.networking.packet;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.tslat.tes.api.TESConstants;

import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;

public record RequestEffectsPacket(int entityId) implements MultiloaderPacket {
    public static final ResourceLocation ID = new ResourceLocation(TESConstants.MOD_ID, "request_effects");

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeVarInt(this.entityId);
    }

    public static RequestEffectsPacket decode(final FriendlyByteBuf buffer) {
        return new RequestEffectsPacket(buffer.readVarInt());
    }

    @Override
    public void receiveMessage(Player sender, Consumer<Runnable> workQueue) {
        workQueue.accept(() -> {
            Entity entity = sender.level().getEntity(this.entityId);

            if (entity instanceof LivingEntity livingEntity) {
                Collection<MobEffectInstance> effects = livingEntity.getActiveEffects();
                Set<ResourceLocation> ids = new ObjectOpenHashSet<>(effects.size());

                for (MobEffectInstance instance : effects) {
                    if (instance.isVisible() || instance.showIcon())
                        ids.add(BuiltInRegistries.MOB_EFFECT.getKey(instance.getEffect()));
                }

                TESConstants.NETWORKING.sendEffectsSync((ServerPlayer)sender, this.entityId, ids, Set.of());
            }
        });
    }
}

package net.tslat.tslatentitystatus.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.tslat.tslatentitystatus.core.state.TESEntityState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin {
    /// Append [TESEntityState#tick(net.minecraft.world.entity.LivingEntity)] to each passenger's [Entity#tick()] call
    @WrapOperation(method = "tickNonPassenger", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;tick()V"))
    public void tslatentitystatus$wrapEntityTick(Entity entity, Operation<Void> original) {
        original.call(entity);

        if (entity instanceof LivingEntity livingEntity) {
           final TESEntityState state = livingEntity.tslatentitystatus$getEntityState();

           if (state != null)
                state.tick(livingEntity);
        }
    }

    /// Append [TESEntityState#tick(net.minecraft.world.entity.LivingEntity)] to each passenger's [Entity#rideTick()] call
    @WrapOperation(method = "tickPassenger", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;rideTick()V"))
    public void tslatentitystatus$wrapPassengerTick(Entity entity, Operation<Void> original) {
        original.call(entity);

        if (entity instanceof LivingEntity livingEntity) {
            final TESEntityState state = livingEntity.tslatentitystatus$getEntityState();

            if (state != null)
                state.tick(livingEntity);
        }
    }
}

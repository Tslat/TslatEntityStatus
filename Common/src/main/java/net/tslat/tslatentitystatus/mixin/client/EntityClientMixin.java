package net.tslat.tslatentitystatus.mixin.client;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.tslat.tslatentitystatus.core.state.TESEntityState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityClientMixin {
    @Shadow
    private Level level;

    @Inject(method = "setId", at = @At("TAIL"))
    private void tslatentitystatus$captureLocalPlayer(int id, CallbackInfo ci) {
        //noinspection ConstantValue
        if ((Object)this instanceof LivingEntity livingEntity && level.isClientSide())
            livingEntity.tslatentitystatus$setEntityState(new TESEntityState(livingEntity));
    }
}

package net.tslat.tslatentitystatus.mixin.client;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.tslat.tslatentitystatus.core.state.TESEntityState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityClientMixin {
    @Inject(method = "setId", at = @At("TAIL"))
    private void tslatentitystatus$captureLocalPlayer(int id, CallbackInfo ci) {
        if ((Object)this instanceof LivingEntity livingEntity)
            livingEntity.tslatentitystatus$setEntityState(new TESEntityState(livingEntity));
    }
}

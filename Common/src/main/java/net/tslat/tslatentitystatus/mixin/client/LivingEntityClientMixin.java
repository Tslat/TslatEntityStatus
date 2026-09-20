package net.tslat.tslatentitystatus.mixin.client;

import net.minecraft.world.entity.LivingEntity;
import net.tslat.tslatentitystatus.core.common.TESEntityStateHolder;
import net.tslat.tslatentitystatus.core.state.TESEntityState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/// Mostly just a duck injector for [TESEntityStateHolder] for living entities
@Mixin(LivingEntity.class)
public class LivingEntityClientMixin implements TESEntityStateHolder {
    @SuppressWarnings("NotNullFieldNotInitialized")
    @Unique
    private TESEntityState tslatentitystatus$entityState;

    @Unique
    public void tslatentitystatus$setEntityState(TESEntityState state) {
        this.tslatentitystatus$entityState = state;
    }

    @Override
    public TESEntityState tslatentitystatus$getEntityState() {
        return this.tslatentitystatus$entityState;
    }
}

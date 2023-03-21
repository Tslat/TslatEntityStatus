package net.tslat.tes.mixin.common;

import net.minecraft.world.entity.WalkAnimationState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WalkAnimationState.class)
public interface WalkAnimationStateAccess {
	@Accessor
	void setSpeedOld(float value);

	@Accessor
	void setSpeed(float value);

	@Accessor
	void setPosition(float value);

	@Accessor
	float getSpeedOld();
}

package net.tslat.tslatentitystatus.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.tslat.tslatentitystatus.core.client.hud.TESHud;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
	@Shadow
	public @Nullable Entity crosshairPickEntity;

	/// Inject [TESHud]'s targeted entity selection for `HUD` rendering
	@Inject(method = "pick(F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;pop()V"))
	private void tslatentitystatus$pickHudEntity(float partialTick, CallbackInfo callback) {
		TESHud.pickNewEntity(partialTick, this.crosshairPickEntity);
	}
}

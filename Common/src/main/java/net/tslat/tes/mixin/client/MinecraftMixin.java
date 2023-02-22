package net.tslat.tes.mixin.client;

import net.minecraft.client.Minecraft;
import net.tslat.tes.core.particle.TESParticleManager;
import net.tslat.tes.core.state.TESEntityTracking;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
	@Inject(method = "tick", at = @At(value = "TAIL"))
	private void tick(CallbackInfo callback) {
		if (Minecraft.getInstance().level != null) {
			TESParticleManager.tick();
			TESEntityTracking.tick();
		}
	}
}

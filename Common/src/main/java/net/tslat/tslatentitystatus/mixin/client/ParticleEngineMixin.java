package net.tslat.tslatentitystatus.mixin.client;

import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.tslat.tslatentitystatus.core.particle.TESParticleManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParticleEngine.class)
public class ParticleEngineMixin {
    /// Inject TES' [TESParticleManager#tick] call
    @Inject(method = "tick", at = @At("HEAD"))
    public void tslatentitystatus$injectTesParticleTick(CallbackInfo ci) {
        final ProfilerFiller profiler = Profiler.get();

        profiler.push("TESParticles");
        TESParticleManager.tick();
        profiler.pop();
    }
}
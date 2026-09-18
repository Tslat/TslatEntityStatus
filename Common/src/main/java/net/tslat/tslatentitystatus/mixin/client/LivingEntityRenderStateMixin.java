package net.tslat.tslatentitystatus.mixin.client;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.tslat.tslatentitystatus.api.client.object.TESEntityRenderState;
import net.tslat.tslatentitystatus.core.client.TESRenderStateExtension;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/// Duck-interface injection for [TESRenderStateExtension]
@Mixin(LivingEntityRenderState.class)
public class LivingEntityRenderStateMixin implements TESRenderStateExtension {
	@Unique
	private @Nullable TESEntityRenderState tes$renderState;

	/// Set the [TESEntityRenderState] for this [LivingEntityRenderState] instance
	@Unique
	@Override
	public void tslatentitystatus$setRenderState(@Nullable TESEntityRenderState renderState) {
		this.tes$renderState = renderState;
	}

	/// @return The [TESEntityRenderState] stored on this [LivingEntityRenderState] instance, or null if none was stored
	@Override
	public @Nullable TESEntityRenderState tslatentitystatus$getRenderState() {
		return this.tes$renderState;
	}
}

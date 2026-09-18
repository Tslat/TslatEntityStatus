package net.tslat.tslatentitystatus.core.client;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.tslat.tslatentitystatus.api.client.object.TESEntityRenderState;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

/// Duck-interface for `TES`' [TESEntityRenderState] extension of the vanilla [LivingEntityRenderState]
@ApiStatus.Internal
public interface TESRenderStateExtension {
	/// Set the [TESEntityRenderState] for this [LivingEntityRenderState] instance
	default void tslatentitystatus$setRenderState(@Nullable TESEntityRenderState renderState) {
		throw new UnsupportedOperationException("Interface injection failed for " + getClass().getName());
	}
	/// @return The [TESEntityRenderState] stored on this [LivingEntityRenderState] instance, or null if none was stored
	default @Nullable TESEntityRenderState tslatentitystatus$getRenderState() {
		throw new UnsupportedOperationException("Interface injection failed for " + getClass().getName());
	}
}

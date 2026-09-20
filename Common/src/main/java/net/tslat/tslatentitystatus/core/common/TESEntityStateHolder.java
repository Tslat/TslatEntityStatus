package net.tslat.tslatentitystatus.core.common;

import net.minecraft.world.entity.LivingEntity;
import net.tslat.tslatentitystatus.api.TESConstants;
import net.tslat.tslatentitystatus.core.state.TESEntityState;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

/// Duck-interface for [LivingEntity] to store TslatEntityStatus' per-entity data
///
/// This is typically only stored and managed on the client side, but it is not explicitly client-side code
public interface TESEntityStateHolder {
	@ApiStatus.Internal
	default void tslatentitystatus$setEntityState(TESEntityState state) {};

	/// @return The stored TES [TESEntityState] for this entity. Will only return `null` for entities tagged with [TESConstants#NO_TES_HANDLING]
	default @Nullable TESEntityState tslatentitystatus$getEntityState() {
		return null;
	}
}

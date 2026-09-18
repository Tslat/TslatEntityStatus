package net.tslat.tslatentitystatus.core.platform;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.tslat.tslatentitystatus.api.common.constant.TESEntityRelation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

/// Loader-agnostic service interface for general loader-specific functions
public interface TESPlatform {
	/// @return `true` if the current runtime is an in-dev (non-production) environment for running debug-only tasks
	boolean isDevelopmentEnvironment();
	
	/// @return `true` if the current runtime is on the client side regardless of logical context
	boolean isPhysicalClient();

	/// Get the provided entity in its damageable form, if possible
	///
	/// This method exists due to the existence of part-entities and non-damageable [LivingEntities][LivingEntity] such as [ArmorStand]
	@Contract("null->null")
	default @Nullable LivingEntity getDamageableEntity(@Nullable Entity entity) {
		if (entity instanceof LivingEntity livingEntity) {
			if (livingEntity instanceof ArmorStand)
				return null;

			return livingEntity;
		}

		return null;
	}

	/// Get the relationship type of the provided entity to the player, from the perspective of `TES`
	///
	/// This allows for handling different entities in different ways, particularly for render formatting
	TESEntityRelation getTESEntityRelation(LivingEntity entity);

	/// @return `true` if either playing in singleplayer, or on a server with `TES` installed
	boolean hasServerMod();

	/// Set the syncing state for effects for the TES HUD
	@ApiStatus.Internal
	void setSyncingEffects(boolean syncingEffects);
}

package net.tslat.tes.api.object;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.tslat.tes.api.TESConstants;
import net.tslat.tes.api.util.TESClientUtil;
import net.tslat.tes.core.hud.TESHud;
import net.tslat.tes.core.state.EntityState;

import java.util.function.Predicate;

/**
 * Activation types for TES' in-world entity status bars
 */
public enum TESHUDActivation {
	ALWAYS(state -> true),
	NEARBY_ONLY(state -> state.getEntity().filter(entity -> entity.distanceToSqr(TESClientUtil.getCameraPosition()) < Mth.square(TESConstants.CONFIG.inWorldHUDActivationDistance())).isPresent()),
	DAMAGED_ONLY(state -> state.getHealth() < state.getEntity().map(LivingEntity::getMaxHealth).orElse(0f)),
	DAMAGED_AND_NEARBY(state -> NEARBY_ONLY.test(state) && DAMAGED_ONLY.test(state)),
	LOOKING_AT(state -> state.getEntity().filter(entity -> entity == TESHud.getTargetEntity()).isPresent()),
	LOOKING_AT_AND_DAMAGED(state -> LOOKING_AT.test(state) && DAMAGED_ONLY.test(state)),
	LOOKING_AT_NEARBY_AND_DAMAGED(state -> LOOKING_AT.test(state) && DAMAGED_AND_NEARBY.test(state)),
	NOT_LOOKING_AT(state -> state.getEntity().filter(entity -> entity != TESHud.getTargetEntity()).isPresent()),
	NOT_LOOKING_AT_AND_DAMAGED(state -> NOT_LOOKING_AT.test(state) && DAMAGED_ONLY.test(state)),
	NOT_LOOKING_AT_NEARBY_AND_DAMAGED(state -> NOT_LOOKING_AT.test(state) && DAMAGED_AND_NEARBY.test(state));

	private final Predicate<EntityState> predicate;

	TESHUDActivation(Predicate<EntityState> predicate) {
		this.predicate = predicate;
	}

	public boolean test(EntityState state) {
		return this.predicate.test(state);
	}
}

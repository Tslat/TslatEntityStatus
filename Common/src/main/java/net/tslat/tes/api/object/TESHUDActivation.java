package net.tslat.tes.api.object;

import net.tslat.tes.api.util.TESClientUtil;
import net.tslat.tes.core.hud.TESHud;
import net.tslat.tes.core.state.EntityState;

import java.util.function.Predicate;

/**
 * Activation types for TES' in-world entity status bars
 */
public enum TESHUDActivation {
	ALWAYS(state -> true),
	NEARBY_ONLY(state -> state.getEntity().distanceToSqr(TESClientUtil.getClientCamera()) < 256),
	DAMAGED_ONLY(state -> state.getHealth() < state.getEntity().getMaxHealth()),
	DAMAGED_AND_NEARBY(state -> NEARBY_ONLY.test(state) && DAMAGED_ONLY.test(state)),
	LOOKING_AT(state -> state.getEntity() == TESHud.getTargetEntity()),
	LOOKING_AT_AND_DAMAGED(state -> LOOKING_AT.test(state) && DAMAGED_ONLY.test(state)),
	LOOKING_AT_NEARBY_AND_DAMAGED(state -> LOOKING_AT.test(state) && DAMAGED_AND_NEARBY.test(state)),
	NOT_LOOKING_AT(state -> state.getEntity() != TESHud.getTargetEntity()),
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

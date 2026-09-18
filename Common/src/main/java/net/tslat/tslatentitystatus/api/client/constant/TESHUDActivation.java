package net.tslat.tslatentitystatus.api.client.constant;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;
import net.tslat.tslatentitystatus.api.client.object.TESEntityRenderState;
import net.tslat.tslatentitystatus.api.TESConstants;
import net.tslat.tslatentitystatus.core.client.hud.TESHud;

import java.util.function.BiPredicate;

/// Activation types for TES' in-world entity status bars
public enum TESHUDActivation {
	ALWAYS((_, _) -> true),
	NEARBY_ONLY((state, _) -> state.distanceToCameraSq < Mth.square(TESConstants.getConfig().inWorldHUDActivationDistance())),
	DAMAGED_ONLY((_, tesState) -> tesState.health < tesState.maxHealth),
	DAMAGED_AND_NEARBY(NEARBY_ONLY.predicate.and(DAMAGED_ONLY.predicate)),
	LOOKING_AT((_, tesState) -> TESHud.getTargetEntity() != null && TESHud.getTargetEntity().getId() == tesState.entityId),
	LOOKING_AT_AND_DAMAGED((state, tesState) -> LOOKING_AT.shouldBeActiveFor(state, tesState) && DAMAGED_ONLY.shouldBeActiveFor(state, tesState)),
	LOOKING_AT_NEARBY_AND_DAMAGED((state, tesState) -> LOOKING_AT.shouldBeActiveFor(state, tesState) && DAMAGED_AND_NEARBY.shouldBeActiveFor(state, tesState)),
	NOT_LOOKING_AT((_, tesState) -> TESHud.getTargetEntity() == null || TESHud.getTargetEntity().getId() != tesState.entityId),
	NOT_LOOKING_AT_AND_DAMAGED((state, tesState) -> NOT_LOOKING_AT.shouldBeActiveFor(state, tesState) && DAMAGED_ONLY.shouldBeActiveFor(state, tesState)),
	NOT_LOOKING_AT_NEARBY_AND_DAMAGED((state, tesState) -> NOT_LOOKING_AT.shouldBeActiveFor(state, tesState) && DAMAGED_AND_NEARBY.shouldBeActiveFor(state, tesState));

	private final BiPredicate<LivingEntityRenderState, TESEntityRenderState> predicate;

	TESHUDActivation(BiPredicate<LivingEntityRenderState, TESEntityRenderState> predicate) {
		this.predicate = predicate;
	}

	/// Test if this `TESHUDActivation` should be activated based on the provided render state
	public boolean shouldBeActiveFor(LivingEntityRenderState renderState, TESEntityRenderState tesState) {
		return this.predicate.test(renderState, tesState);
	}
}

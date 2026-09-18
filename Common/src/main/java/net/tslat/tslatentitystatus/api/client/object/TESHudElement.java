package net.tslat.tslatentitystatus.api.client.object;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.LivingEntity;
import net.tslat.tslatentitystatus.api.TESConfig;
import net.tslat.tslatentitystatus.core.state.TESEntityState;

/// Base interface for `TES` HUD render elements
///
/// Elements are responsible for maintaining their own validity and keeping within the [recommended render width][TESConfig#hudHealthBarLength()]
@FunctionalInterface
public interface TESHudElement {
	/// Submit the HUD render element to the [TESHudRenderContext] for rendering
	///
	/// The [PoseStack] has already been transformed to the correct position
	///
	/// @param renderContext 	Either the gui rendering context, or in-world render arguments, depending on whether the current render context is HUD or in-world
	/// @param tesRenderState	TES' renderstate object for TES-related data for this entity for this render pass
	/// @param opacity     		The global base opacity for all elements in the TES HUD, as configured by the user in the TES Config
	/// 						Any elements with built-in transparency should multiply their alpha value by this
	/// @return 				How tall (in pixels) your element rendered, so that the next element can be given space for rendering
	int submit(TESHudRenderContext renderContext, TESEntityRenderState tesRenderState, float opacity);

	/// Add any required additional data to [TESEntityRenderState#extraData] for the given entity for the current render pass
	default void addRenderData(TESEntityRenderState tesRenderState, LivingEntity entity, TESEntityState tesState, float partialTick) {}
}

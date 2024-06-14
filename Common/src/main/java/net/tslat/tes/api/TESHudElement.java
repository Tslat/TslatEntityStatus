package net.tslat.tes.api;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.LivingEntity;

/**
 * Base interface for TES HUD render elements.<br>
 * Elements are responsible for maintaining their own validity and keeping within the {@link TESConfig#hudHealthBarLength() recommended render width}.
 */
@FunctionalInterface
public interface TESHudElement {
	/**
	 * Render the HUD element.<br>
	 * The PoseStack has already been transformed to the correct position.
	 *
	 * @param guiGraphics Minecraft's batched gui-rendering object. The PoseStack is contained within this
	 * @param mc The Minecraft instance, provided for convenience
	 * @param deltaTracker The Timer instance for tracking partialTick time with relevance to paused game states
	 * @param entity      The target entity to render the info for
	 * @param opacity     The global base opacity for all elements in the TES HUD, as configured by the user in the TES Config. Any elements with built-in transparency should multiply their alpha value by this
	 * @param inWorldHud  Whether the element is currently rendering in the in-world TES HUD
	 * @return How tall (in pixels) your element rendered, so that the next element can be given space for rendering
	 */
	int render(GuiGraphics guiGraphics, Minecraft mc, DeltaTracker deltaTracker, LivingEntity entity, float opacity, boolean inWorldHud);
}

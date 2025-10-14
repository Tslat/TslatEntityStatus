package net.tslat.tes.api.object;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.tslat.tes.api.TESConfig;

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
	 * @param renderContext Either the gui rendering context, or in-world render arguments, depending on whether the current render context is HUD or in-world
	 * @param mc The Minecraft instance, provided for convenience
	 * @param entity      The target entity to render the info for
	 * @param opacity     The global base opacity for all elements in the TES HUD, as configured by the user in the TES Config. Any elements with built-in transparency should multiply their alpha value by this
	 * @return How tall (in pixels) your element rendered, so that the next element can be given space for rendering
	 */
	int render(TESHudRenderContext renderContext, Minecraft mc, LivingEntity entity, float opacity);
}

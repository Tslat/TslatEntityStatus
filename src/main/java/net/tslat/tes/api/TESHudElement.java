package net.tslat.tes.api;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.tslat.tes.config.TESConfig;

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
	 * @param entity     The target entity to render the info for
	 * @param opacity    The global base opacity for all elements in the TES HUD, as configured by the user in the TES Config. Any elements with built-in transparency should multiply their alpha value by this
	 * @param inWorldHud Whether the element is currently rendering in the in-world TES HUD
	 * @return How tall (in pixels) your element rendered, so that the next element can be given space for rendering
	 */
	int render(MatrixStack poseStack, Minecraft mc, float partialTick, LivingEntity entity, float opacity, boolean inWorldHud);
}

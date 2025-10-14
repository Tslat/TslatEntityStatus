package net.tslat.tes.core.particle.type;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.tslat.tes.api.TESAPI;
import net.tslat.tes.api.TESConstants;
import net.tslat.tes.api.object.TESHudRenderContext;
import net.tslat.tes.api.object.TESParticle;
import net.tslat.tes.api.util.render.TextRenderHelper;
import net.tslat.tes.core.state.EntityState;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

/**
 * Built-in class for text-based {@link TESParticle TES Particles}
 */
public class TextParticle extends GenericTESParticle<String> {
	protected String text;
	protected int colour = 0xFFFFFFFF;

	public TextParticle(@Nullable EntityState entityState, Vector3f position, String text) {
		this(entityState, position, Animation.POP_OFF, text);
	}

	public TextParticle(@Nullable EntityState entityState, Vector3f position, Animation animation, String text) {
		this(entityState, position, animation, text, TESConstants.CONFIG.defaultParticleLifespan());
	}

	public TextParticle(@Nullable EntityState entityState, Vector3f position, Animation animation, String text, int lifespan) {
		super(entityState, position, animation, lifespan);

		updateData(text);
	}

	/**
	 * Set the rendering colour for this particle.<br>
	 * Format is ARGB
	 */
	public TextParticle withColour(int colour) {
		this.colour = colour;

		return this;
	}

	/**
	 * Get the rendering colour for this particle.<br>
	 * Format is ARGB
	 */
	public int getColour() {
		return this.colour;
	}

	@Override
	public void updateData(String data) {
		this.text = data;
	}

	@Override
	public void submitRender(TESHudRenderContext.InWorldArgs renderArgs, Minecraft mc, Font fontRenderer) {
		defaultedTextRender(mc, renderArgs.poseStack(), this.prevPos, this.pos, renderArgs.partialTick(), () ->
				TextRenderHelper.of(this.text).centered().colour(getColour()).style(TESAPI.getConfig().particleFontStyle()).renderInWorld(renderArgs, 0, -4));
	}
}

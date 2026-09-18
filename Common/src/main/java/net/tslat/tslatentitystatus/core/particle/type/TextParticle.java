package net.tslat.tslatentitystatus.core.particle.type;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.tslat.tslatentitystatus.api.TESConstants;
import net.tslat.tslatentitystatus.api.client.object.TESHudRenderContext;
import net.tslat.tslatentitystatus.api.client.object.TESParticle;
import net.tslat.tslatentitystatus.api.client.object.renderhelper.TextRenderHelper;
import net.tslat.tslatentitystatus.core.state.TESEntityState;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

/// Built-in class for text-based [`TES Particles`][TESParticle]
public class TextParticle extends GenericTESParticle<String> {
	protected String text;
	protected int colour = 0xFFFFFFFF;

	public TextParticle(@Nullable TESEntityState entityState, Vector3f position, String text) {
		this(entityState, position, Animation.POP_OFF, text);
	}

	public TextParticle(@Nullable TESEntityState entityState, Vector3f position, Animation animation, String text) {
		this(entityState, position, animation, text, TESConstants.getConfig().defaultParticleLifespan());
	}

	public TextParticle(@Nullable TESEntityState entityState, Vector3f position, Animation animation, String text, int lifespan) {
		super(entityState, position, animation, lifespan);

		updateData(text);
	}

	/// Set the rendering colour for this particle.
	///
	/// Format is ARGB
	public TextParticle withColour(int colour) {
		this.colour = colour;

		return this;
	}

	/// Get the rendering colour for this particle.
	///
	/// Format is ARGB
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
				TextRenderHelper.of(this.text).centered().colour(getColour()).style(TESConstants.getConfig().particleFontStyle()).inWorldSubmit(renderArgs, 0, -4));
	}
}

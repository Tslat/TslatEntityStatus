package net.tslat.tes.core.particle.type;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.tslat.tes.api.TESParticle;
import net.tslat.tes.api.util.TESClientUtil;
import net.tslat.tes.core.state.EntityState;

import javax.annotation.Nullable;

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
		this(entityState, position, animation, text, DEFAULT_LIFESPAN);
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
	public void render(PoseStack poseStack, Minecraft mc, Font fontRenderer, float partialTick) {
		defaultedTextRender(mc, poseStack, this.prevPos, this.pos, partialTick, () -> TESClientUtil.renderCenteredText(this.text, poseStack, fontRenderer, 0, 0, getColour()));
	}
}

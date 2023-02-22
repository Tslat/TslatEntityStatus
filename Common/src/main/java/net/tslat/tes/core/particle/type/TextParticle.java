package net.tslat.tes.core.particle.type;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.tslat.tes.api.TESParticle;
import net.tslat.tes.api.util.TESClientUtil;
import org.joml.Vector3f;

/**
 * Built-in class for text-based {@link TESParticle TES Particles}
 */
public class TextParticle extends GenericTESParticle<String> {
	protected String text;
	protected int colour = 0xFFFFFFFF;

	public TextParticle(Vector3f position, String text) {
		this(position, Animation.POP_OFF, text);
	}

	public TextParticle(Vector3f position, Animation animation, String text) {
		this(position, animation, text, DEFAULT_LIFESPAN);
	}

	public TextParticle(Vector3f position, Animation animation, String text, int lifespan) {
		super(position, animation, lifespan);

		updateData(text);
	}

	/**
	 * Set the rendering colour for this particle.<br>
	 * Format is ARGB
	 */
	public void setColour(int colour) {
		this.colour = colour;
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

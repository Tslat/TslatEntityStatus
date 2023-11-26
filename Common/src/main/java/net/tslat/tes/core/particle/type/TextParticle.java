package net.tslat.tes.core.particle.type;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.tslat.tes.api.TESAPI;
import net.tslat.tes.api.TESConstants;
import net.tslat.tes.api.TESParticle;
import net.tslat.tes.core.state.EntityState;
import net.tslat.tes.mixin.client.GuiGraphicsAccessor;
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
	public void render(GuiGraphics guiGraphics, Minecraft mc, Font fontRenderer, float partialTick) {
		defaultedTextRender(mc, guiGraphics.pose(), this.prevPos, this.pos, partialTick, () -> TESAPI.getConfig().particleFontStyle().render(fontRenderer, guiGraphics.pose(), Component.literal(this.text), -fontRenderer.width(Component.literal(this.text)) / 2f, 4, getColour(), guiGraphics.bufferSource()));
		((GuiGraphicsAccessor)guiGraphics).callFlushIfUnmanaged();
	}
}

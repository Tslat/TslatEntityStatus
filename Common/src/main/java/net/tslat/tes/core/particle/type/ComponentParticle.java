package net.tslat.tes.core.particle.type;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.tslat.tes.api.util.TESClientUtil;
import net.tslat.tes.core.state.EntityState;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

/**
 * Generic class for {@link Component}-based TES Particles
 */
public class ComponentParticle extends GenericTESParticle<Component> {
	protected Component contents;

	public ComponentParticle(@Nullable EntityState entityState, Vector3f position, Component contents) {
		this(entityState, position, Animation.POP_OFF, contents);
	}

	public ComponentParticle(@Nullable EntityState entityState, Vector3f position, Animation animation, Component contents) {
		this(entityState, position, animation, contents, DEFAULT_LIFESPAN);
	}

	public ComponentParticle(@Nullable EntityState entityState, Vector3f position, Animation animation, Component contents, int lifespan) {
		super(entityState, position, animation, lifespan);

		updateData(contents);
	}

	@Override
	public void updateData(Component data) {
		this.contents = data;
	}

	@Override
	public void render(GuiGraphics guiGraphics, Minecraft mc, Font fontRenderer, float partialTick) {
		defaultedTextRender(mc, guiGraphics.pose(), this.prevPos, this.pos, partialTick, () -> TESClientUtil.renderCenteredText(guiGraphics, this.contents, 0, 0, 0xFFFFFF));
	}
}

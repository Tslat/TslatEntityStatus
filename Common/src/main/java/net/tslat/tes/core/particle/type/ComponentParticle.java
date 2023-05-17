package net.tslat.tes.core.particle.type;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.tslat.tes.api.util.TESClientUtil;
import net.tslat.tes.core.state.EntityState;
import org.joml.Vector3f;

import org.jetbrains.annotations.Nullable;

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
	public void render(PoseStack poseStack, Minecraft mc, Font fontRenderer, float partialTick) {
		defaultedTextRender(mc, poseStack, this.prevPos, this.pos, partialTick, () -> TESClientUtil.renderCenteredText(this.contents, poseStack, fontRenderer, 0, 0, 0xFFFFFF));
	}
}

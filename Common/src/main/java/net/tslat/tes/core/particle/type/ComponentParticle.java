package net.tslat.tes.core.particle.type;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.tslat.tes.api.TESAPI;
import net.tslat.tes.api.TESConstants;
import net.tslat.tes.api.object.TESHudRenderContext;
import net.tslat.tes.api.util.render.TextRenderHelper;
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
		this(entityState, position, animation, contents, TESConstants.CONFIG.defaultParticleLifespan());
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
	public void submitRender(TESHudRenderContext.InWorldArgs renderArgs, Minecraft mc, Font fontRenderer) {
		defaultedTextRender(mc, renderArgs.poseStack(), this.prevPos, this.pos, renderArgs.partialTick(), () ->
				TextRenderHelper.of(this.contents).centered().style(TESAPI.getConfig().particleFontStyle()).renderInWorld(renderArgs, 0, -4));
	}
}

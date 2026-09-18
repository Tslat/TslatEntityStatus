package net.tslat.tslatentitystatus.core.particle.type;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.tslat.tslatentitystatus.api.TESConstants;
import net.tslat.tslatentitystatus.api.client.object.TESHudRenderContext;
import net.tslat.tslatentitystatus.api.client.object.renderhelper.TextRenderHelper;
import net.tslat.tslatentitystatus.core.state.TESEntityState;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

/// Generic class for [Component]-based TES Particles
public class ComponentParticle extends GenericTESParticle<Component> {
	protected Component contents;

	public ComponentParticle(@Nullable TESEntityState entityState, Vector3f position, Component contents) {
		this(entityState, position, Animation.POP_OFF, contents);
	}

	public ComponentParticle(@Nullable TESEntityState entityState, Vector3f position, Animation animation, Component contents) {
		this(entityState, position, animation, contents, TESConstants.getConfig().defaultParticleLifespan());
	}

	public ComponentParticle(@Nullable TESEntityState entityState, Vector3f position, Animation animation, Component contents, int lifespan) {
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
				TextRenderHelper.of(this.contents).centered().style(TESConstants.getConfig().particleFontStyle()).inWorldSubmit(renderArgs, 0, -4));
	}
}

package net.tslat.tes.core.particle.type;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.tslat.tes.api.util.TESClientUtil;
import net.tslat.tes.core.state.EntityState;

import javax.annotation.Nullable;

public class ComponentParticle extends GenericTESParticle<ITextComponent> {
	protected ITextComponent contents;

	public ComponentParticle(@Nullable EntityState entityState, Vector3f position, ITextComponent contents) {
		this(entityState, position, Animation.POP_OFF, contents);
	}

	public ComponentParticle(@Nullable EntityState entityState, Vector3f position, Animation animation, ITextComponent contents) {
		this(entityState, position, animation, contents, DEFAULT_LIFESPAN);
	}

	public ComponentParticle(@Nullable EntityState entityState, Vector3f position, Animation animation, ITextComponent contents, int lifespan) {
		super(entityState, position, animation, lifespan);

		updateData(contents);
	}

	@Override
	public void updateData(ITextComponent data) {
		this.contents = data;
	}

	@Override
	public void render(MatrixStack poseStack, Minecraft mc, FontRenderer fontRenderer, float partialTick) {
		defaultedTextRender(mc, poseStack, this.prevPos, this.pos, partialTick, () -> TESClientUtil.renderCenteredText(this.contents, poseStack, fontRenderer, 0, 0, 0xFFFFFF));
	}
}
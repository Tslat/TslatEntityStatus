package net.tslat.tes.api.util;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.tslat.tes.api.TESAPI;
import net.tslat.tes.api.TESConstants;
import net.tslat.tes.mixin.client.GuiGraphicsAccessor;
import org.joml.Matrix4f;

/**
 * Various helper methods for client-side functions
 */
public final class TESClientUtil {
	public static final ResourceLocation CREATIVE_INVENTORY_TEXTURE = new ResourceLocation("textures/gui/container/creative_inventory/tab_inventory.png");
	public static final ResourceLocation ICONS_ATLAS_LOCATION = new ResourceLocation("textures/atlas/gui.png");
	public static final ResourceLocation NOTCH_OVERLAY_SPRITE = new ResourceLocation("boss_bar/notched_6_progress");
	public static final ResourceLocation ARMOUR_ICON_SPRITE = new ResourceLocation("hud/armor_full");
	public static final ResourceLocation TOUGHNESS_ICON_SPRITE = new ResourceLocation(TESConstants.MOD_ID, "hud/toughness_full");
	public static final ResourceLocation ENTITY_FIRE_IMMUNE_SPRITE = new ResourceLocation(TESConstants.MOD_ID, "hud/entity_fire_immune");
	public static final ResourceLocation ENTITY_MELEE_SPRITE = new ResourceLocation(TESConstants.MOD_ID, "hud/entity_melee");
	public static final ResourceLocation ENTITY_RANGED_SPRITE = new ResourceLocation(TESConstants.MOD_ID, "hud/entity_ranged");
	public static final ResourceLocation ENTITY_ARTHROPOD_SPRITE = new ResourceLocation(TESConstants.MOD_ID, "hud/entity_type_arthropod");
	public static final ResourceLocation ENTITY_ILLAGER_SPRITE = new ResourceLocation(TESConstants.MOD_ID, "hud/entity_type_illager");
	public static final ResourceLocation ENTITY_UNDEAD_SPRITE = new ResourceLocation(TESConstants.MOD_ID, "hud/entity_type_undead");
	public static final ResourceLocation ENTITY_WATER_SPRITE = new ResourceLocation(TESConstants.MOD_ID, "hud/entity_type_water");

	/**
	 * Draw some text on screen at a given position, offset for the text's height and width
	 */
	public static void renderCenteredText(GuiGraphics guiGraphics, String text, float x, float y, int colour) {
		renderCenteredText(guiGraphics, Component.literal(text), x, y, colour);
	}

	/**
	 * Draw some text on screen at a given position, offset for the text's height and width
	 */
	public static void renderCenteredText(GuiGraphics guiGraphics, Component text, float x, float y, int colour) {
		drawText(guiGraphics, Minecraft.getInstance().font, text, x - Minecraft.getInstance().font.width(text) / 2f, y + 4f, colour);
	}

	/**
	 * Translate the given {@link PoseStack} to face the game camera
	 */
	public static void positionFacingCamera(PoseStack poseStack) {
		poseStack.mulPose(Minecraft.getInstance().gameRenderer.getMainCamera().rotation());
	}

	/**
	 * Prep the shader and bind a texture for the given ResourceLocation
	 */
	public static void prepRenderForTexture(ResourceLocation texture) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, texture);
	}

	/**
	 * Render a TES-style health bar at a given position for a given width
	 * @param guiGraphics The GuiGraphics instance for the current render state
	 * @param x The x-position of the bar (use 0 and translate the {@link PoseStack} for in-world rendering
	 * @param y The y-position of the bar (use 0 and translate the {@link PoseStack} for in-world rendering
	 * @param width The width of the bar. This method reconstructs the bar accurately using the two ends and a stretched center to make an indefinitely-applicable width
	 * @param sprite The atlas sprite for the bar texture to render
	 * @param percentComplete Percentage progress of the bar (use 1 for background or overlay pieces)
	 * @param withBarOverlay Render the bar segments overlay
	 * @param opacity The overall opacity of the bar
	 */
	public static void constructBarRender(GuiGraphics guiGraphics, int x, int y, int width, TextureAtlasSprite sprite, float percentComplete, boolean withBarOverlay, float opacity) {
		int percentPixels = Math.round(percentComplete * width);
		int midBarWidth = width - 10;

		RenderSystem.setShaderTexture(0, sprite.atlasLocation());

		drawSprite(guiGraphics, sprite, x, y, Math.min(5, percentPixels), 5, 0, 0, Math.min(5, percentPixels), 5, 182, 5);

		if (percentPixels > 5) {
			if (midBarWidth > 0)
				drawSprite(guiGraphics, sprite, x + 5, y, Math.min(midBarWidth, percentPixels - 5), 5, 5, 0, Math.min(midBarWidth, percentPixels - 5), 5, 182, 5);

			if (percentPixels > width - 5)
				drawSprite(guiGraphics, sprite, x + 5 + midBarWidth, y, Math.min(5, percentPixels - 5), 5, 177, 0, Math.min(5, percentPixels - 5), 5, 182, 5);
		}

		if (withBarOverlay && width > 10) {
			RenderSystem.setShaderColor(1, 1, 1, 0.75f * opacity);

			TextureAtlasSprite overlaySprite = getAtlasSprite(NOTCH_OVERLAY_SPRITE);

			RenderSystem.setShaderTexture(0, overlaySprite.atlasLocation());
			drawSprite(guiGraphics, overlaySprite, x, y, width, 5, 0, 0, 182, 5, 182, 5);
		}
	}

	/**
	 * Render a statically-positioned view of an {@link LivingEntity entity} instance, optionally including the frame TES usually renders with
	 */
	public static void renderEntityIcon(GuiGraphics guiGraphics, Minecraft mc, float partialTick, LivingEntity entity, float opacity, boolean includeFrame) {
		float scale = 0.04f * (float)Math.pow(Math.min(30 / entity.getBbWidth(), 40 / entity.getBbHeight()), 0.95f);
		MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
		PoseStack poseStack = guiGraphics.pose();

		poseStack.pushPose();

		if (includeFrame) {
			TESClientUtil.prepRenderForTexture(TESClientUtil.CREATIVE_INVENTORY_TEXTURE);
			RenderSystem.enableBlend();
			RenderSystem.setShaderColor(1, 1, 1, 0.5f * opacity);
			TESClientUtil.drawSimpleTexture(guiGraphics, 2, 2, 34, 45, 72, 5, 256);

			poseStack.translate(20, 25, 0);
			poseStack.scale(-20, -20, 20);
		}

		poseStack.scale(scale, scale, scale);
		poseStack.translate(0, entity.getBbHeight() * -0.5f, entity.getBbWidth());

		float yBodyRotPrev = entity.yBodyRot;
		float yRotPrev = entity.getYRot();
		float xRotPrev = entity.getXRot();
		float yHeadRotOldPrev = entity.yHeadRotO;
		float yHeadRotPrev = entity.yHeadRot;
		int hurtTicks = entity.hurtTime;
		float attackTimePrev = entity.attackAnim;
		float attackTimeOldPrev = entity.oAttackAnim;

		entity.setYRot(22.5f);
		entity.setXRot(0);

		entity.yBodyRot = 22.5f;
		entity.yHeadRot = entity.getYRot();
		entity.yHeadRotO = entity.getYRot();

		entity.hurtTime = TESAPI.getConfig().hudEntityDamageOverlay() ? entity.hurtTime : 0;
		entity.attackAnim = 0;
		entity.oAttackAnim = 0;

		Lighting.setupForEntityInInventory();

		RenderSystem.runAsFancy(() -> renderEntityRaw(poseStack, entity, partialTick, 0, LightTexture.FULL_BRIGHT, bufferSource));

		bufferSource.endBatch();
		Lighting.setupFor3DItems();

		entity.setYRot(yRotPrev);
		entity.setXRot(xRotPrev);

		entity.yBodyRot = yBodyRotPrev;
		entity.yHeadRot = yHeadRotPrev;
		entity.yHeadRotO = yHeadRotOldPrev;

		entity.hurtTime = hurtTicks;
		entity.attackAnim = attackTimePrev;
		entity.oAttackAnim = attackTimeOldPrev;

		poseStack.popPose();
	}

	/**
	 * Render a static instance of an entity, skipping its shadows and hitbox
	 */
	public static <T extends Entity> void renderEntityRaw(PoseStack poseStack, T entity, float partialTick, float rotYaw, int packedLight, MultiBufferSource bufferSource) {
		EntityRenderDispatcher entityRenderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
		EntityRenderer<T> entityRenderer = (EntityRenderer<T>)entityRenderDispatcher.getRenderer(entity);

		try {
			Vec3 renderOffset = entityRenderer.getRenderOffset(entity, partialTick);

			poseStack.pushPose();
			poseStack.translate(renderOffset.x, renderOffset.y, renderOffset.z);
			entityRenderer.render(entity, rotYaw, 1, poseStack, bufferSource, packedLight);
			poseStack.popPose();
		}
		catch (Exception ex) {
			CrashReport wrappedReport = CrashReport.forThrowable(ex, "Rendering entity in world");
			CrashReportCategory entityReportCategory = wrappedReport.addCategory("Entity being rendered");

			entity.fillCrashReportCategory(entityReportCategory);

			CrashReportCategory rendererReportCategory = wrappedReport.addCategory("Renderer details");

			rendererReportCategory.setDetail("Assigned renderer", entityRenderer);

			throw new ReportedException(wrappedReport);
		}
	}

	/**
	 * Wrapper for {@link GuiGraphics#blit} to make it easier to use
	 * @param guiGraphics The GuiGraphics instance for the current render state
	 * @param posX The x position on the screen to render at
	 * @param posY The y position on the screen to render at
	 * @param width The width of the image
	 * @param height The height of the image
	 * @param u The x position on the texture image to render from
	 * @param v The y position on the texture image to render from
	 * @param pngSize The pixel-size of the png file (only for square png files)
	 */
	public static void drawSimpleTexture(GuiGraphics guiGraphics, int posX, int posY, int width, int height, float u, float v, int pngSize) {
		drawSimpleTexture(guiGraphics, posX, posY, width, height, u, v, pngSize, pngSize);
	}

	/**
	 * Wrapper for {@link GuiGraphics#blit} to make it easier to use
	 * @param guiGraphics The GuiGraphics instance for the current render state
	 * @param posX The x position on the screen to render at
	 * @param posY The y position on the screen to render at
	 * @param width The width of the image
	 * @param height The height of the image
	 * @param u The x position on the texture image to render from
	 * @param v The y position on the texture image to render from
	 * @param pngWidth The pixel-width of the png file
	 * @param pngHeight The pixel-height of the png file
	 */
	public static void drawSimpleTexture(GuiGraphics guiGraphics, int posX, int posY, int width, int height, float u, float v, int pngWidth, int pngHeight) {
		drawSimpleTexture(guiGraphics, posX, posY, width, height, u, v, width, height, pngWidth, pngHeight);
	}

	/**
	 * Wrapper for {@link GuiGraphics#blit} to make it easier to use
	 * @param guiGraphics The GuiGraphics instance for the current render state
	 * @param posX The x position on the screen to render at
	 * @param posY The y position on the screen to render at
	 * @param width The width of the in-game render
	 * @param height The height of the in-game render
	 * @param u The x position on the texture image to render from
	 * @param v The y position on the texture image to render from
	 * @param uWidth The width of the texture on the image to render
	 * @param vHeight The height of the texture on the image to render
	 * @param pngWidth The width of the entire png file
	 * @param pngHeight The height of the entire png file
	 */
	public static void drawSimpleTexture(GuiGraphics guiGraphics, int posX, int posY, int width, int height, float u, float v, int uWidth, int vHeight, int pngWidth, int pngHeight) {
		final Matrix4f pose = guiGraphics.pose().last().pose();
		final BufferBuilder buffer = Tesselator.getInstance().getBuilder();
		final float widthRatio = 1.0F / pngWidth;
		final float heightRatio = 1.0F / pngHeight;

		RenderSystem.enableBlend();
		buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		buffer.vertex(pose, (float)posX, (float)posY, 0).uv(u * widthRatio, v * heightRatio).endVertex();
		buffer.vertex(pose, (float)posX, (float)posY + height, 0).uv(u * widthRatio, (v + vHeight) * heightRatio).endVertex();
		buffer.vertex(pose, (float)posX + width, (float)posY + height, 0).uv((u + uWidth) * widthRatio, (v + vHeight) * heightRatio).endVertex();
		buffer.vertex(pose, (float)posX + width, (float)posY, 0).uv((u + uWidth) * widthRatio, v * heightRatio).endVertex();
		BufferUploader.drawWithShader(buffer.end());
	}

	public static void drawSprite(GuiGraphics guiGraphics, TextureAtlasSprite sprite, int posX, int posY, int width, int height, int u, int v, int uWidth, int vHeight, int pngWidth, int pngHeight) {
		final Matrix4f pose = guiGraphics.pose().last().pose();
		final BufferBuilder buffer = Tesselator.getInstance().getBuilder();
		final float uMin = sprite.getU(u / (float)pngWidth);
		final float uMax = sprite.getU((u + uWidth) / (float)pngWidth);
		final float vMin = sprite.getV(v / (float)pngHeight);
		final float vMax = sprite.getV((v + vHeight) / (float)pngHeight);

		RenderSystem.enableBlend();
		buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		buffer.vertex(pose, (float)posX, (float)posY, 0).uv(uMin, vMin).endVertex();
		buffer.vertex(pose, (float)posX, (float)posY + height, 0).uv(uMin, vMax).endVertex();
		buffer.vertex(pose, (float)posX + width, (float)posY + height, 0).uv(uMax, vMax).endVertex();
		buffer.vertex(pose, (float)posX + width, (float)posY, 0).uv(uMax, vMin).endVertex();
		BufferUploader.drawWithShader(buffer.end());
	}

	/**
	 * Wrapper for {@link GuiGraphics#drawString} to make it easier/more consistent to use
	 * @param guiGraphics The GuiGraphics instance for the current render state
	 * @param font The font instance to use for rendering the text
	 * @param text The text to draw
	 * @param x The x position on the screen to render at
	 * @param y The y position on the screen to render at
	 * @param colour The {@link net.minecraft.util.FastColor packed int} colour for the text
	 */
	public static void drawText(GuiGraphics guiGraphics, Font font, String text, float x, float y, int colour) {
		drawText(guiGraphics, font, Component.literal(text), x, y, colour);
	}

	/**
	 * Wrapper for {@link GuiGraphics#drawString} to make it easier/more consistent to use
	 * @param guiGraphics The GuiGraphics instance for the current render state
	 * @param font The font instance to use for rendering the text
	 * @param text The text to draw
	 * @param x The x position on the screen to render at
	 * @param y The y position on the screen to render at
	 * @param colour The {@link net.minecraft.util.FastColor packed int} colour for the text
	 */
	public static void drawText(GuiGraphics guiGraphics, Font font, Component text, float x, float y, int colour) {
		font.drawInBatch(text, x, y, colour, false, guiGraphics.pose().last().pose(), guiGraphics.bufferSource(), Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);
		((GuiGraphicsAccessor)guiGraphics).callFlushIfUnmanaged();
	}

	/**
	 * Wrapper for {@link GuiGraphics#drawString} to make it easier/more consistent to use
	 * @param guiGraphics The GuiGraphics instance for the current render state
	 * @param font The font instance to use for rendering the text
	 * @param text The text to draw
	 * @param x The x position on the screen to render at
	 * @param y The y position on the screen to render at
	 * @param colour The {@link net.minecraft.util.FastColor packed int} colour for the text
	 */
	public static void drawTextWithShadow(GuiGraphics guiGraphics, Font font, String text, float x, float y, int colour) {
		drawText(guiGraphics, font, Component.literal(text), x, y, colour);
	}

	/**
	 * Wrapper for {@link GuiGraphics#drawString} to make it easier/more consistent to use, with a drop shadow
	 * @param guiGraphics The GuiGraphics instance for the current render state
	 * @param text The text to draw
	 * @param x The x position on the screen to render at
	 * @param y The y position on the screen to render at
	 * @param colour The {@link net.minecraft.util.FastColor packed int} colour for the text
	 */
	public static void drawTextWithShadow(GuiGraphics guiGraphics, Font font, Component text, float x, float y, int colour) {
		font.drawInBatch(text, x, y, colour, true, guiGraphics.pose().last().pose(), guiGraphics.bufferSource(), Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);
	}

	/**
	 * Wrapper for {@link GuiGraphics#fill} to make it easier/more consistent to use
	 * @param guiGraphics The GuiGraphics instance for the current render state
	 * @param posX The x position on the screen to render at
	 * @param posY The y position on the screen to render at
	 * @param width The width of the square
	 * @param height The height of the square
	 * @param colour The {@link net.minecraft.util.FastColor packed int} colour for the square
	 */
	public static void drawColouredSquare(GuiGraphics guiGraphics, int posX, int posY, int width, int height, int colour) {
		guiGraphics.fill(posX, posY, posX + width, posY + height, colour);
	}

	/**
	 * Translate a given locale key to its locally-translated string equivalent
	 */
	public static String translateKey(String key, Object... args) {
		return I18n.get(key, args);
	}

	/**
	 * Proxy-style getter for the client player
	 */
	public static Player getClientPlayer() {
		return Minecraft.getInstance().player;
	}

	/**
	 * Create a new {@link GuiGraphics} instance from the provided {@link net.minecraft.client.renderer.MultiBufferSource.BufferSource BufferSource}, pre-multiplying the {@link PoseStack} in line with the current pose
	 */
	public static GuiGraphics createInlineGuiGraphics(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource) {
		GuiGraphics guiGraphics = new GuiGraphics(Minecraft.getInstance(), bufferSource);

		guiGraphics.pose().mulPoseMatrix(poseStack.last().pose());

		return guiGraphics;
	}

	/**
	 * Get the TextureAtlasSprite instance for the given texture location
	 */
	public static TextureAtlasSprite getAtlasSprite(ResourceLocation texture) {
		return Minecraft.getInstance().getGuiSprites().getSprite(texture);
	}
}

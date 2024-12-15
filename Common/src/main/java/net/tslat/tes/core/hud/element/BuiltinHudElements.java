package net.tslat.tes.core.hud.element;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.CoreShaders;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.MobEffectTextureManager;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.tslat.tes.api.TESAPI;
import net.tslat.tes.api.TESConfig;
import net.tslat.tes.api.TESConstants;
import net.tslat.tes.api.TESEntityType;
import net.tslat.tes.api.util.TESClientUtil;
import net.tslat.tes.api.util.TESUtil;
import net.tslat.tes.core.hud.TESHud;
import net.tslat.tes.core.state.EntityState;
import net.tslat.tes.core.state.TESEntityTracking;

/**
 * Built-in HUD handles for the default rendering capabilities for the mod
 */
public final class BuiltinHudElements {
	public static int renderEntityName(GuiGraphics guiGraphics, Minecraft mc, DeltaTracker deltaTracker, LivingEntity entity, float opacity, boolean inWorldHud) {
		int lineHeight = mc.font.lineHeight;

		if (inWorldHud) {
			if (!TESAPI.getConfig().inWorldHudEntityName() && (!TESAPI.getConfig().inWorldHudNameOverride() || !entity.hasCustomName()))
				return 0;

			TESClientUtil.centerTextForRender(entity.getDisplayName(), 0, 0, (x, y) -> TESAPI.getConfig().inWorldHudEntityNameFontStyle().render(mc.font, guiGraphics.pose(), entity.getDisplayName(), x, y, ARGB.color((int)(opacity * 255f), 255, 255, 255), guiGraphics.bufferSource));

			if (TESAPI.getConfig().inWorldHudEntityNamespace()) {
				TESClientUtil.centerTextForRender(Component.literal("(" + BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).getNamespace() + ")"), 0, lineHeight, (x, y) -> TESAPI.getConfig().inWorldHudEntityNameFontStyle().render(mc.font, guiGraphics.pose(), entity.getDisplayName(), x, y, ARGB.color((int) (opacity * 255f), 200, 200, 200), guiGraphics.bufferSource));
				lineHeight += mc.font.lineHeight;
			}
		}
		else {
			if (!TESAPI.getConfig().hudEntityName())
				return 0;

			TESAPI.getConfig().hudEntityNameFontStyle().render(mc.font, guiGraphics.pose(), entity.getDisplayName(), 0, 0, ARGB.color(255, 255, 255, 255), guiGraphics.bufferSource);

			if (TESAPI.getConfig().hudEntityNamespace()) {
				TESAPI.getConfig().hudEntityNameFontStyle().render(mc.font, guiGraphics.pose(), Component.literal("(" + BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).getNamespace() + ")"), 0, lineHeight, ARGB.color(255, 200, 200, 200), guiGraphics.bufferSource);
				lineHeight += mc.font.lineHeight;
			}
		}

		TESEntityTracking.markNameRendered(entity);
		guiGraphics.bufferSource.endLastBatch();

		return lineHeight;
	}

	public static int renderEntityHealth(GuiGraphics guiGraphics, Minecraft mc, DeltaTracker deltaTracker, LivingEntity entity, float opacity, boolean inWorldHud) {
		EntityState entityState = TESEntityTracking.getStateForEntity(entity);

		if (entityState == null)
			return 0;

		TESConfig config = TESAPI.getConfig();
		int barWidth = inWorldHud ? config.inWorldBarsLength() : config.hudHealthBarLength();
		TESHud.BarRenderType renderType = inWorldHud ? config.inWorldBarsRenderType() : config.hudHealthRenderType();
		PoseStack poseStack = guiGraphics.pose();

		poseStack.pushPose();
		poseStack.translate(0, inWorldHud ? 4 : 1, 0);

		if (inWorldHud) {
			poseStack.translate(barWidth * -0.5f, 0, 0);
			poseStack.scale(1, 1, -1);
		}

		RenderSystem.setShaderColor(1, 1, 1, opacity);
		RenderSystem.enableDepthTest();

		if (renderType != TESHud.BarRenderType.NUMERIC) {
			float percentHealth = entityState.getHealth() / entity.getMaxHealth();
			float percentTransitionHealth = entityState.getLastTransitionHealth() / entity.getMaxHealth();
			boolean doSegments = inWorldHud ? config.inWorldBarsSegments() : config.hudHealthBarSegments();
			TESEntityType entityType = TESConstants.UTILS.getEntityType(entity);
			TextureAtlasSprite backgroundSprite = TESClientUtil.getAtlasSprite(entityType.backgroundTexture());
			TextureAtlasSprite progressSprite = TESClientUtil.getAtlasSprite(entityType.overlayTexture());
			TextureAtlasSprite emptyBarSprite = TESClientUtil.getAtlasSprite(TESClientUtil.BAR_EMPTY);
			TESClientUtil.prepRenderForTexture(backgroundSprite.atlasLocation());

			TESClientUtil.constructBarRender(guiGraphics, 0, 0, barWidth, emptyBarSprite, 1, false, opacity);
			poseStack.translate(0, 0, 0.001f);

			if (percentTransitionHealth > percentHealth)
				TESClientUtil.constructBarRender(guiGraphics, 0, 0, barWidth, backgroundSprite, entityState.getLastTransitionHealth() / entity.getMaxHealth(), false, opacity);

			poseStack.translate(0, 0, 0.001f);

			RenderSystem.enableBlend();
			TESClientUtil.constructBarRender(guiGraphics, 0, 0, barWidth, progressSprite, percentHealth, doSegments, opacity);
		}

		if (renderType != TESHud.BarRenderType.BAR && renderType != TESHud.BarRenderType.BAR_ICONS) {
			String healthText = TESUtil.roundToDecimal(entityState.getHealth(), 1) + "/" + TESUtil.roundToDecimal(entity.getMaxHealth(), 1);
			float halfTextWidth = mc.font.width(healthText) / 2f;
			float center = barWidth / 2f;

			RenderSystem.setShader(CoreShaders.POSITION_COLOR);

			poseStack.translate(0, 0, 0.001f);
			TESClientUtil.drawColouredSquare(guiGraphics, (int)(center - halfTextWidth - 1), -2, (int)(halfTextWidth * 2) + 1, 9, 0x090909 | (int)(opacity * 255 * TESAPI.getConfig().hudBarFontBackingOpacity()) << 24);
			poseStack.translate(0, 0, 0.001f);

			(inWorldHud ? TESAPI.getConfig().inWorldHudHealthFontStyle() : TESAPI.getConfig().hudHealthFontStyle()).render(mc.font, guiGraphics.pose(), Component.literal(healthText), center - halfTextWidth, -1, ARGB.color((int)(opacity * 255f), 255, 255, 255), guiGraphics.bufferSource);
		}

		poseStack.popPose();

		return mc.font.lineHeight;
	}

	public static int renderEntityStats(GuiGraphics guiGraphics, Minecraft mc, DeltaTracker deltaTracker, LivingEntity entity, float opacity, boolean inWorldHud) {
		if (inWorldHud) {
			if (!TESAPI.getConfig().inWorldHudStats())
				return 0;
		}
		else {
			if (!TESAPI.getConfig().hudStats())
				return 0;
		}

		int armour = TESUtil.getArmour(entity);
		float toughness = TESUtil.getArmourToughness(entity);
		float meleeDamage = TESUtil.getMeleeDamage(entity);
		float health = TESUtil.getHealth(entity);
		int hearts = health <= 0 ? 0 : Math.max(1, Mth.floor(health / 2f));
		int textColour = ARGB.color((int)(opacity * 255f), 255, 255, 255);
		Component armourString = armour > 0 ? Component.literal("x" + armour) : null;
		Component toughnessString  = toughness > 0 ? Component.literal("x" + TESUtil.roundToDecimal(toughness, 1)) : null;
		Component meleeDamageString  = meleeDamage > 0 ? Component.literal("x" + TESUtil.roundToDecimal(meleeDamage, 1)) : null;
		Component heartsString  = (inWorldHud ? TESAPI.getConfig().inWorldBarsRenderType() : TESAPI.getConfig().hudHealthRenderType()) == TESHud.BarRenderType.BAR_ICONS ? Component.literal("x" + hearts) : null;
		int armourX = 0;
		int toughnessX = armourX + (armourString == null ? 0 : 11 + mc.font.width(armourString));
		int meleeDamageX = toughnessX + (toughnessString == null ? 0 : 11 + mc.font.width(toughnessString));
		int healthX = meleeDamageX + (meleeDamageString == null ? 0 : 11 + mc.font.width(meleeDamageString));
		PoseStack poseStack = guiGraphics.pose();
		TextureAtlasSprite armourSprite = TESClientUtil.getAtlasSprite(TESClientUtil.STAT_ARMOUR);
		TextureAtlasSprite toughnessSprite = TESClientUtil.getAtlasSprite(TESClientUtil.STAT_TOUGHNESS);
		TextureAtlasSprite meleeDamageSprite = TESClientUtil.getAtlasSprite(TESClientUtil.STAT_MELEE_DAMAGE);

		poseStack.pushPose();

		if (inWorldHud)
			poseStack.translate((healthX + (heartsString == null ? 0 : mc.font.width(heartsString)) + 2) * -0.5f, 0, 0);

		TESClientUtil.prepRenderForTexture(armourSprite.atlasLocation());
		RenderSystem.enableBlend();
		RenderSystem.setShaderColor(1, 1, 1, opacity);

		if (armour > 0)
			TESClientUtil.drawSprite(guiGraphics, armourSprite, armourX, 0, 9, 9, 0, 0);

		if (toughness > 0)
			TESClientUtil.drawSprite(guiGraphics, toughnessSprite, toughnessX, 0, 9, 9, 0, 0);

		if (meleeDamage > 0)
			TESClientUtil.drawSprite(guiGraphics, meleeDamageSprite, meleeDamageX, 0, 9, 9, 0, 0);

		if (heartsString != null) {
			TESClientUtil.drawSprite(guiGraphics, TESClientUtil.getAtlasSprite(ResourceLocation.withDefaultNamespace("hud/heart/container")), healthX, 0, 9, 9, 0, 0);
			TESClientUtil.drawSprite(guiGraphics, TESClientUtil.getAtlasSprite(ResourceLocation.withDefaultNamespace("hud/heart/full")), healthX, 0, 9, 9, 0, 0);
		}

		if (armour > 0)
			(inWorldHud ? TESAPI.getConfig().inWorldHudStatsFontStyle() : TESAPI.getConfig().hudStatsFontStyle()).render(mc.font, guiGraphics.pose(), armourString, armourX + 10, 1, textColour, guiGraphics.bufferSource);

		if (toughness > 0)
			(inWorldHud ? TESAPI.getConfig().inWorldHudStatsFontStyle() : TESAPI.getConfig().hudStatsFontStyle()).render(mc.font, guiGraphics.pose(), toughnessString, toughnessX + 10, 1, textColour, guiGraphics.bufferSource);

		if (meleeDamage > 0)
			(inWorldHud ? TESAPI.getConfig().inWorldHudStatsFontStyle() : TESAPI.getConfig().hudStatsFontStyle()).render(mc.font, guiGraphics.pose(), meleeDamageString, meleeDamageX + 10, 1, textColour, guiGraphics.bufferSource);

		if (heartsString != null)
			(inWorldHud ? TESAPI.getConfig().inWorldHudStatsFontStyle() : TESAPI.getConfig().hudStatsFontStyle()).render(mc.font, guiGraphics.pose(), heartsString, healthX + 10, 1, textColour, guiGraphics.bufferSource);

		poseStack.popPose();

		return mc.font.lineHeight;
	}

	public static int renderEntityIcons(GuiGraphics guiGraphics, Minecraft mc, DeltaTracker deltaTracker, LivingEntity entity, float opacity, boolean inWorldHud) {
		if (inWorldHud) {
			if (!TESAPI.getConfig().inWorldHudEntityIcons())
				return 0;
		}
		else {
			if (!TESAPI.getConfig().hudEntityIcons())
				return 0;
		}

		int x = 0;

		TESClientUtil.prepRenderForTexture(TESClientUtil.SPRITES_ATLAS);

		for (TESHudEntityIcon icon : TESHud.getEntityIcons()) {
			if (icon.renderIfApplicable(guiGraphics, entity, x, 0))
				x += 9;
		}

		return x == 0 ? 0 : 8;
	}

	public static int renderEntityEffects(GuiGraphics guiGraphics, Minecraft mc, DeltaTracker deltaTracker, LivingEntity entity, float opacity, boolean inWorldHud) {
		if (inWorldHud) {
			if (!TESAPI.getConfig().inWorldHudPotionIcons())
				return 0;
		}
		else {
			if (!TESAPI.getConfig().hudPotionIcons())
				return 0;
		}

		EntityState entityState = TESEntityTracking.getStateForEntity(entity);

		if (entityState == null || entityState.getEffects().isEmpty())
			return 0;

		int effectsSize = entityState.getEffects().size();
		MobEffectTextureManager textureManager = mc.getMobEffectTextures();
		int barLength = inWorldHud ? TESAPI.getConfig().inWorldBarsLength() : TESAPI.getConfig().hudHealthBarLength();
		float maxX = barLength * 2f;
		int iconsPerRow = (int)Math.floor(maxX / 18f);
		int rows = (int)Math.ceil(effectsSize / (float)iconsPerRow);
		int x = inWorldHud ? (Math.min(effectsSize, iconsPerRow) * -9) : 0;
		int y = 0;
		int i = 0;
		PoseStack poseStack = guiGraphics.pose();

		poseStack.pushPose();
		RenderSystem.setShader(CoreShaders.POSITION_TEX);
		RenderSystem.enableBlend();
		RenderSystem.setShaderColor(1, 1, 1, opacity);
		poseStack.scale(0.5f, 0.5f, 1);

		if (inWorldHud)
			poseStack.translate(0, Math.floor(effectsSize * 18 / maxX) * -18, 0);

		for (Holder<MobEffect> effect : entityState.getEffects()) {
			TextureAtlasSprite sprite = textureManager.get(effect);

			RenderSystem.setShaderTexture(0, sprite.atlasLocation());
			guiGraphics.blitSprite(RenderType::guiTextured, sprite, i * 18 + x, y, 18, 18);

			if (++i >= iconsPerRow) {
				i = 0;
				y += 18;

				if (inWorldHud && y / 18 == rows - 1)
					x = (effectsSize % iconsPerRow) % iconsPerRow * -9;
			}
		}

		RenderSystem.disableBlend();
		poseStack.popPose();

		return (int)Math.ceil(effectsSize / (float)iconsPerRow) * 9;
	}

	public static int renderHorseStats(GuiGraphics guiGraphics, Minecraft mc, DeltaTracker deltaTracker, LivingEntity entity, float opacity, boolean inWorldHud) {
		if (inWorldHud) {
			if (!TESAPI.getConfig().inWorldHudHorseStats())
				return 0;
		}
		else {
			if (!TESAPI.getConfig().hudHorseStats())
				return 0;
		}

		if (!(entity instanceof AbstractHorse horse))
			return 0;

		boolean hasChest = entity instanceof AbstractChestedHorse chestedHorse && chestedHorse.hasChest();
		int x = inWorldHud ? hasChest ? -18 : -11 : 1;

		guiGraphics.drawString(mc.font, "H", x, 1, Mth.hsvToArgb(Mth.clamp(0.35f * ((float)horse.getAttributeBaseValue(Attributes.MAX_HEALTH) - AbstractHorse.MIN_HEALTH) / (AbstractHorse.MAX_HEALTH - AbstractHorse.MIN_HEALTH), 0, 0.35f), 1, 1, 255), false);
		guiGraphics.drawString(mc.font, "S", x + 8, 1, Mth.hsvToArgb(Mth.clamp(0.35f * ((float)horse.getAttributeBaseValue(Attributes.MOVEMENT_SPEED) - AbstractHorse.MIN_MOVEMENT_SPEED) / (AbstractHorse.MAX_MOVEMENT_SPEED - AbstractHorse.MIN_MOVEMENT_SPEED), 0, 0.35f), 1, 1, 255), false);
		guiGraphics.drawString(mc.font, "J", x + 16, 1, Mth.hsvToArgb(Mth.clamp(0.35f * ((float)horse.getAttributeBaseValue(Attributes.JUMP_STRENGTH) - AbstractHorse.MIN_JUMP_STRENGTH) / (AbstractHorse.MAX_JUMP_STRENGTH - AbstractHorse.MIN_JUMP_STRENGTH), 0, 0.35f), 1, 1, 255), false);

		if (hasChest) {
			TESClientUtil.prepRenderForTexture(TESClientUtil.SPRITES_ATLAS);
			TESClientUtil.drawSprite(guiGraphics, TESClientUtil.getAtlasSprite(TESClientUtil.PROPERTY_STORAGE), x + 24, 1, 8, 8, 0, 0, 36, 36, 36, 36);
		}

		return 9;
	}
}

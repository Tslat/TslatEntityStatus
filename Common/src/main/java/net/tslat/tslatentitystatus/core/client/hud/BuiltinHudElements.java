package net.tslat.tslatentitystatus.core.client.hud;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.tslat.tslatentitystatus.api.TESConfig;
import net.tslat.tslatentitystatus.api.TESConstants;
import net.tslat.tslatentitystatus.api.client.constant.TESTextures;
import net.tslat.tslatentitystatus.api.client.object.TESEntityRenderState;
import net.tslat.tslatentitystatus.api.client.object.TESHudEntityIcon;
import net.tslat.tslatentitystatus.api.client.object.TESHudRenderContext;
import net.tslat.tslatentitystatus.api.client.object.renderhelper.TextRenderHelper;
import net.tslat.tslatentitystatus.api.client.object.renderhelper.TextureRenderHelper;
import net.tslat.tslatentitystatus.api.client.util.TESRenderUtil;
import net.tslat.tslatentitystatus.api.common.util.TESUtil;

import java.util.List;

/// Built-in HUD handles for the default rendering capabilities for the mod
public final class BuiltinHudElements {
	public static int submitEntityName(TESHudRenderContext renderContext, TESEntityRenderState tesRenderState, float opacity) {
		final TESConfig config = TESConstants.getConfig();

		if (!willRenderName(renderContext.isInWorld(), tesRenderState.hasCustomName))
			return 0;

		final Minecraft mc = Minecraft.getInstance();
		final boolean inWorldHud = renderContext.isInWorld();
		final TextRenderHelper nameRenderer = TextRenderHelper.of(tesRenderState.displayName).colour(ARGB.white(opacity));
		final TextRenderHelper namespaceRenderer = (inWorldHud ? config.inWorldHudEntityNamespace() : config.hudEntityNamespace()) ?
											 TextRenderHelper.of(Component.literal("(" + BuiltInRegistries.ENTITY_TYPE.getKey(tesRenderState.entityType).getNamespace() + ")"))
													 .colour(200, 200, 200, Mth.floor(opacity * 255)) : null;
		int lineHeight = mc.font.lineHeight;

		if (inWorldHud) {
			nameRenderer.style(config.inWorldHudEntityNameFontStyle()).centered();

			if (namespaceRenderer != null)
				namespaceRenderer.style(config.inWorldHudEntityNameFontStyle()).centered();
		}
		else {
			nameRenderer.style(config.hudEntityNameFontStyle());

			if (namespaceRenderer != null)
				namespaceRenderer.style(config.hudEntityNameFontStyle());
		}

		nameRenderer.submit(renderContext, 0, 0);

		if (namespaceRenderer != null) {
			namespaceRenderer.submit(renderContext, 0, lineHeight);
			lineHeight += mc.font.lineHeight;
		}

		return lineHeight;
	}

	public static int submitEntityHealth(TESHudRenderContext renderContext, TESEntityRenderState tesRenderState, float opacity) {
		final Minecraft mc = Minecraft.getInstance();
		final TESConfig config = TESConstants.getConfig();
		final boolean inWorldHud = renderContext.isInWorld();
		final int barWidth = inWorldHud ? config.inWorldBarsLength() : config.hudHealthBarLength();
		final TESHud.BarRenderType renderType = inWorldHud ? config.inWorldBarsRenderType() : config.hudHealthRenderType();

		renderContext.pushMatrix();
		renderContext.translate(inWorldHud ? barWidth * -0.5f : 0, inWorldHud ? 4 : 1, 0);

		if (renderType != TESHud.BarRenderType.NUMERIC) {
			final float filledPercent = tesRenderState.health / tesRenderState.maxHealth;
			final float transitionPercent = tesRenderState.prevTransitionHealth / tesRenderState.maxHealth;
			final boolean doSegmentsOverlay = inWorldHud ? config.inWorldBarsSegments() : config.hudHealthBarSegments();
			final TextureAtlasSprite emptyBar = TESRenderUtil.getGuiAtlasSprite(tesRenderState.entityRelation.backgroundTexture());
			final TextureAtlasSprite filledBar = TESRenderUtil.getGuiAtlasSprite(tesRenderState.entityRelation.overlayTexture());
			final TextureAtlasSprite barBackground = TESRenderUtil.getGuiAtlasSprite(TESTextures.BAR_EMPTY);
			final TextureAtlasSprite barOverlay = doSegmentsOverlay ? TESRenderUtil.getGuiAtlasSprite(TESTextures.BAR_OVERLAY_SEGMENTS) : null;

			TESRenderUtil.renderBar(renderContext, 0, 0, barWidth, filledPercent, transitionPercent, opacity, barBackground, emptyBar, filledBar, barOverlay);
		}

		if (renderType != TESHud.BarRenderType.BAR && renderType != TESHud.BarRenderType.BAR_ICONS) {
			final String healthText = TESUtil.roundToDecimal(tesRenderState.health, 1) + "/" + TESUtil.roundToDecimal(tesRenderState.maxHealth, 1);

			if (inWorldHud)
				renderContext.translate(0, 0, 0.019f);

			final TextRenderHelper healthRenderer = TextRenderHelper.of(Component.literal(healthText))
					.colour(255, 255, 255, Mth.floor(opacity * 255f))
					.withBackdrop(ARGB.color(Mth.floor(opacity * 255 * config.hudBarFontBackingOpacity()), 9, 9, 9))
					.centered();

			if (inWorldHud)
				healthRenderer.lightLevel(renderContext.getPackedLight());

			healthRenderer.submit(renderContext, barWidth / 2f, -1);
		}

		renderContext.popMatrix();

		return mc.font.lineHeight;
	}

	public static int submitEntityStats(TESHudRenderContext renderContext, TESEntityRenderState tesRenderState, float opacity) {
		final TESConfig config = TESConstants.getConfig();

		if (renderContext.isInWorld() ? !config.inWorldHudStats() : !config.hudStats())
			return 0;

		final Minecraft mc = Minecraft.getInstance();
		final boolean inWorldHud = renderContext.isInWorld();
		final int armour = tesRenderState.armour;
		final float toughness = tesRenderState.armorToughness;
		final float meleeDamage = tesRenderState.meleeDamage;
		final int hearts = tesRenderState.health <= 0 ? 0 : Math.max(1, Mth.floor(tesRenderState.health / 2f));
		final int colour = ARGB.white(opacity);
		final Component armourString = armour > 0 ? Component.literal("x" + armour) : null;
		final Component toughnessString  = toughness > 0 ? Component.literal("x" + TESUtil.roundToDecimal(toughness, 1)) : null;
		final Component meleeDamageString  = meleeDamage > 0 ? Component.literal("x" + TESUtil.roundToDecimal(meleeDamage, 1)) : null;
		final Component heartsString  = (inWorldHud ? config.inWorldBarsRenderType() : config.hudHealthRenderType()) == TESHud.BarRenderType.BAR_ICONS ? Component.literal("x" + hearts) : null;
		final int armourX = 0;
		final int toughnessX = armourX + (armourString == null ? 0 : 11 + mc.font.width(armourString));
		final int meleeDamageX = toughnessX + (toughnessString == null ? 0 : 11 + mc.font.width(toughnessString));
		final int healthX = meleeDamageX + (meleeDamageString == null ? 0 : 11 + mc.font.width(meleeDamageString));

		if (healthX == 0 && heartsString == null)
			return 0;

		renderContext.pushMatrix();

		if (inWorldHud)
			renderContext.translate((healthX + (heartsString == null ? 0 : mc.font.width(heartsString)) + 2) * -0.5f, 0, 0);

		if (armour > 0)
			TextureRenderHelper.ofSprite(TESTextures.STAT_ARMOUR).sized(9, 9).colour(colour).render(renderContext, armourX, 0);

		if (toughness > 0)
			TextureRenderHelper.ofSprite(TESTextures.STAT_TOUGHNESS).sized(9, 9).colour(colour).render(renderContext, toughnessX, 0);

		if (meleeDamage > 0)
			TextureRenderHelper.ofSprite(TESTextures.STAT_MELEE_DAMAGE).sized(9, 9).colour(colour).render(renderContext, meleeDamageX, 0);

		if (heartsString != null) {
			TextureRenderHelper.ofSprite(Identifier.withDefaultNamespace("hud/heart/container")).sized(9, 9).colour(colour).render(renderContext, healthX, 0);
			TextureRenderHelper.ofSprite(Identifier.withDefaultNamespace("hud/heart/full")).sized(9, 9).colour(colour).render(renderContext, healthX, 0);
		}

		if (armour > 0)
			TextRenderHelper.of(armourString).colour(colour).style(inWorldHud ? config.inWorldHudStatsFontStyle() : config.hudStatsFontStyle()).submit(renderContext, armourX + 10, 1);

		if (toughness > 0)
			TextRenderHelper.of(toughnessString).colour(colour).style(inWorldHud ? config.inWorldHudStatsFontStyle() : config.hudStatsFontStyle()).submit(renderContext, toughnessX + 10, 1);

		if (meleeDamage > 0)
			TextRenderHelper.of(meleeDamageString).colour(colour).style(inWorldHud ? config.inWorldHudStatsFontStyle() : config.hudStatsFontStyle()).submit(renderContext, meleeDamageX + 10, 1);

		if (heartsString != null)
			TextRenderHelper.of(heartsString).colour(colour).style(inWorldHud ? config.inWorldHudStatsFontStyle() : config.hudStatsFontStyle()).submit(renderContext, healthX + 10, 1);

		renderContext.popMatrix();

		return mc.font.lineHeight;
	}

	public static int submitEntityIcons(TESHudRenderContext renderContext, TESEntityRenderState tesRenderState, float opacity) {
		TESConfig config = TESConstants.getConfig();

		if (renderContext.isInWorld() ? !config.inWorldHudEntityIcons() : !config.hudEntityIcons())
			return 0;

		float x = 0;
		List<TESHudEntityIcon> icons = TESHud.getEntityIcons();
		List<TESHudEntityIcon> toRender = new ObjectArrayList<>(icons.size());

		for (TESHudEntityIcon icon : icons) {
			if (icon.shouldRender(tesRenderState)) {
				toRender.add(icon);
				x += 9;
			}
		}

		x = renderContext.isInWorld() ? -(x / 2f) : 0;

		for (TESHudEntityIcon icon : toRender) {
			icon.render(renderContext, tesRenderState, x, 0, opacity);

			x += 9;
		}

		return toRender.isEmpty() ? 0 : 8;
	}

	public static int submitEntityEffects(TESHudRenderContext renderContext, TESEntityRenderState tesRenderState, float opacity) {
		TESConfig config = TESConstants.getConfig();

		if (renderContext.isInWorld() ? !config.inWorldHudPotionIcons() : !config.hudPotionIcons())
			return 0;

		if (tesRenderState.effects.isEmpty())
			return 0;

		boolean inWorldHud = renderContext.isInWorld();
		int effectsSize = tesRenderState.effects.size();
		int barLength = inWorldHud ? config.inWorldBarsLength() : config.hudHealthBarLength();
		float maxX = barLength * 2f;
		int iconsPerRow = (int)Math.floor(maxX / 18f);
		int rows = (int)Math.ceil(effectsSize / (float)iconsPerRow);

		int x = inWorldHud ? (Math.min(effectsSize, iconsPerRow) * -9) : 0;
		int y = 0;
		int i = 0;

		renderContext.pushMatrix();
		renderContext.scale(0.5f, 0.5f, 1f);

		if (inWorldHud)
			renderContext.translate(0, Mth.floor(effectsSize * 18 / maxX) * -18, 0);

		for (Holder<MobEffect> effect : tesRenderState.effects) {
			TextureRenderHelper effectRenderer = TextureRenderHelper.ofSprite(Gui.getMobEffectSprite(effect)).sized(18, 18).colour(ARGB.white(opacity));

			if (inWorldHud)
				effectRenderer.lightLevel(renderContext.getPackedLight());

			effectRenderer.render(renderContext, i * 18 + x, y);

			if (++i >= iconsPerRow) {
				i = 0;
				y += 18;

				if (inWorldHud && y / 18 == rows - 1)
					x = (effectsSize % iconsPerRow) % iconsPerRow * -9;
			}
		}

		renderContext.popMatrix();

		return (int)Math.ceil(effectsSize / (float)iconsPerRow) * 9;
	}

	public static int submitHorseStats(TESHudRenderContext renderContext, TESEntityRenderState tesRenderState, float opacity) {
		final TESConfig config = TESConstants.getConfig();

		if (renderContext.isInWorld() ? !config.inWorldHudHorseStats() : !config.hudHorseStats())
			return 0;

		if (!tesRenderState.isHorse)
			return 0;

		final double healthRatio = (tesRenderState.maxHealth - AbstractHorse.MIN_HEALTH) / (AbstractHorse.MAX_HEALTH - AbstractHorse.MIN_HEALTH);
		final double moveSpeedRatio = (tesRenderState.moveSpeed - AbstractHorse.MIN_MOVEMENT_SPEED) / (AbstractHorse.MAX_MOVEMENT_SPEED - AbstractHorse.MIN_MOVEMENT_SPEED);
		final double jumpStrengthRatio = (tesRenderState.jumpStrength - AbstractHorse.MIN_JUMP_STRENGTH) / (AbstractHorse.MAX_JUMP_STRENGTH - AbstractHorse.MIN_JUMP_STRENGTH);
		final TextRenderHelper health = TextRenderHelper.of("H:" + Mth.floor(healthRatio * 100) + "%").colour(ARGB.color(opacity, TESRenderUtil.colourGradeForValue(healthRatio)));
		final TextRenderHelper speed = TextRenderHelper.of("S:" + Mth.floor(moveSpeedRatio * 100) + "%").colour(ARGB.color(opacity, TESRenderUtil.colourGradeForValue(moveSpeedRatio)));
		final TextRenderHelper jumpStrength = TextRenderHelper.of("J:" + Mth.floor(jumpStrengthRatio * 100) + "%").colour(ARGB.color(opacity, TESRenderUtil.colourGradeForValue(jumpStrengthRatio)));
		final TextureRenderHelper storageSprite = TextureRenderHelper.ofSprite(TESTextures.PROPERTY_STORAGE).sized(8, 8).colour(ARGB.white(opacity));
		int x = renderContext.isInWorld() ? tesRenderState.hasContainer ? -18 : -11 : 1;

		if (tesRenderState.hasContainer) {
			storageSprite.render(renderContext, x, 1);

			x += 10;
		}

		health.submit(renderContext, x, 1);
		speed.submit(renderContext, x + 34, 1);
		jumpStrength.submit(renderContext, x + 68, 1);

		return 9;
	}

	/// @return Whether the builtin TES name renderer will render the name this pass
	public static boolean willRenderName(boolean isInWorld, boolean hasCustomName) {
		final TESConfig config = TESConstants.getConfig();

		if (isInWorld ? !config.inWorldHudEntityName() && (!config.inWorldHudNameOverride() || !hasCustomName) : !config.hudEntityName())
			return false;

		return true;
	}
}

package net.tslat.tes.core.hud.element;

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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.equine.AbstractChestedHorse;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.tslat.tes.api.TESAPI;
import net.tslat.tes.api.TESConfig;
import net.tslat.tes.api.TESConstants;
import net.tslat.tes.api.TESTextures;
import net.tslat.tes.api.object.TESEntityType;
import net.tslat.tes.api.object.TESHudRenderContext;
import net.tslat.tes.api.util.TESRenderUtil;
import net.tslat.tes.api.util.TESUtil;
import net.tslat.tes.api.util.render.TextRenderHelper;
import net.tslat.tes.api.util.render.TextureRenderHelper;
import net.tslat.tes.core.hud.TESHud;
import net.tslat.tes.core.state.EntityState;
import net.tslat.tes.core.state.TESEntityTracking;

import java.util.List;

/**
 * Built-in HUD handles for the default rendering capabilities for the mod
 */
public final class BuiltinHudElements {
	public static int renderEntityName(TESHudRenderContext renderContext, Minecraft mc, LivingEntity entity, float opacity) {
		TESConfig config = TESAPI.getConfig();

		if (renderContext.isInWorld() ? !config.inWorldHudEntityName() && (!config.inWorldHudNameOverride() || !entity.hasCustomName()) : !config.hudEntityName())
			return 0;

		int lineHeight = mc.font.lineHeight;
		boolean inWorldHud = renderContext.isInWorld();
		TextRenderHelper nameRenderer = TextRenderHelper.of(entity.getDisplayName()).colour(ARGB.white(opacity));
		TextRenderHelper namespaceRenderer = (inWorldHud ? config.inWorldHudEntityNamespace() : config.hudEntityNamespace()) ?
											 TextRenderHelper.of(Component.literal("(" + BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).getNamespace() + ")"))
													 .colour(200, 200, 200, Mth.floor(opacity * 255)) : null;

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

		nameRenderer.render(renderContext, 0, 0);

		if (namespaceRenderer != null) {
			namespaceRenderer.render(renderContext, 0, lineHeight);
			lineHeight += mc.font.lineHeight;
		}

		TESEntityTracking.markNameRendered(entity);

		return lineHeight;
	}

	public static int renderEntityHealth(TESHudRenderContext renderContext, Minecraft mc, LivingEntity entity, float opacity) {
		EntityState entityState = TESEntityTracking.getStateForEntity(entity);

		if (entityState == null)
			return 0;

		TESConfig config = TESAPI.getConfig();
		boolean inWorldHud = renderContext.isInWorld();
		int barWidth = inWorldHud ? config.inWorldBarsLength() : config.hudHealthBarLength();
		TESHud.BarRenderType renderType = inWorldHud ? config.inWorldBarsRenderType() : config.hudHealthRenderType();

		renderContext.pushMatrix();
		renderContext.translate(inWorldHud ? barWidth * -0.5f : 0, inWorldHud ? 4 : 1, 0);

		if (renderType != TESHud.BarRenderType.NUMERIC) {
			float filledPercent = entityState.getHealth() / entity.getMaxHealth();
			float transitionPercent = entityState.getLastTransitionHealth() / entity.getMaxHealth();
			boolean doSegmentsOverlay = inWorldHud ? config.inWorldBarsSegments() : config.hudHealthBarSegments();
			TESEntityType entityType = TESConstants.UTILS.getEntityType(entity);
			TextureAtlasSprite emptyBar = TESRenderUtil.getGuiAtlasSprite(entityType.backgroundTexture());
			TextureAtlasSprite filledBar = TESRenderUtil.getGuiAtlasSprite(entityType.overlayTexture());
			TextureAtlasSprite barBackground = TESRenderUtil.getGuiAtlasSprite(TESTextures.BAR_EMPTY);
			TextureAtlasSprite barOverlay = doSegmentsOverlay ? TESRenderUtil.getGuiAtlasSprite(TESTextures.BAR_OVERLAY_SEGMENTS) : null;

			TESRenderUtil.renderBar(renderContext, 0, 0, barWidth, filledPercent, transitionPercent, opacity, barBackground, emptyBar, filledBar, barOverlay);
		}

		if (renderType != TESHud.BarRenderType.BAR && renderType != TESHud.BarRenderType.BAR_ICONS) {
			String healthText = TESUtil.roundToDecimal(entityState.getHealth(), 1) + "/" + TESUtil.roundToDecimal(entity.getMaxHealth(), 1);

			if (inWorldHud)
				renderContext.translate(0, 0, 0.019f);

			TextRenderHelper healthRenderer = TextRenderHelper.of(Component.literal(healthText))
					.colour(255, 255, 255, Mth.floor(opacity * 255f))
					.withBackdrop(ARGB.color(Mth.floor(opacity * 255 * config.hudBarFontBackingOpacity()), 9, 9, 9))
					.centered();

			if (inWorldHud)
				healthRenderer.lightLevel(renderContext.getPackedLight());

			healthRenderer.render(renderContext, barWidth / 2f, -1);
		}

		renderContext.popMatrix();

		return mc.font.lineHeight;
	}

	public static int renderEntityStats(TESHudRenderContext renderContext, Minecraft mc, LivingEntity entity, float opacity) {
		TESConfig config = TESAPI.getConfig();

		if (renderContext.isInWorld() ? !config.inWorldHudStats() : !config.hudStats())
			return 0;

		boolean inWorldHud = renderContext.isInWorld();
		int armour = TESUtil.getArmour(entity);
		float toughness = TESUtil.getArmourToughness(entity);
		float meleeDamage = TESUtil.getMeleeDamage(entity);
		float health = TESUtil.getHealth(entity);
		int hearts = health <= 0 ? 0 : Math.max(1, Mth.floor(health / 2f));
		int colour = ARGB.white(opacity);
		Component armourString = armour > 0 ? Component.literal("x" + armour) : null;
		Component toughnessString  = toughness > 0 ? Component.literal("x" + TESUtil.roundToDecimal(toughness, 1)) : null;
		Component meleeDamageString  = meleeDamage > 0 ? Component.literal("x" + TESUtil.roundToDecimal(meleeDamage, 1)) : null;
		Component heartsString  = (inWorldHud ? config.inWorldBarsRenderType() : config.hudHealthRenderType()) == TESHud.BarRenderType.BAR_ICONS ? Component.literal("x" + hearts) : null;
		int armourX = 0;
		int toughnessX = armourX + (armourString == null ? 0 : 11 + mc.font.width(armourString));
		int meleeDamageX = toughnessX + (toughnessString == null ? 0 : 11 + mc.font.width(toughnessString));
		int healthX = meleeDamageX + (meleeDamageString == null ? 0 : 11 + mc.font.width(meleeDamageString));

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
			TextRenderHelper.of(armourString).colour(colour).style(inWorldHud ? config.inWorldHudStatsFontStyle() : config.hudStatsFontStyle()).render(renderContext, armourX + 10, 1);

		if (toughness > 0)
			TextRenderHelper.of(toughnessString).colour(colour).style(inWorldHud ? config.inWorldHudStatsFontStyle() : config.hudStatsFontStyle()).render(renderContext, toughnessX + 10, 1);

		if (meleeDamage > 0)
			TextRenderHelper.of(meleeDamageString).colour(colour).style(inWorldHud ? config.inWorldHudStatsFontStyle() : config.hudStatsFontStyle()).render(renderContext, meleeDamageX + 10, 1);

		if (heartsString != null)
			TextRenderHelper.of(heartsString).colour(colour).style(inWorldHud ? config.inWorldHudStatsFontStyle() : config.hudStatsFontStyle()).render(renderContext, healthX + 10, 1);

		renderContext.popMatrix();

		return mc.font.lineHeight;
	}

	public static int renderEntityIcons(TESHudRenderContext renderContext, Minecraft mc, LivingEntity entity, float opacity) {
		TESConfig config = TESAPI.getConfig();

		if (renderContext.isInWorld() ? !config.inWorldHudEntityIcons() : !config.hudEntityIcons())
			return 0;

		float x = 0;
		List<TESHudEntityIcon> icons = TESHud.getEntityIcons();
		List<TESHudEntityIcon> toRender = new ObjectArrayList<>(icons.size());

		for (TESHudEntityIcon icon : icons) {
			if (icon.shouldRender(entity)) {
				toRender.add(icon);
				x += 9;
			}
		}

		x = renderContext.isInWorld() ? -(x / 2f) : 0;

		for (TESHudEntityIcon icon : toRender) {
			icon.render(renderContext, entity, x, 0, opacity);

			x += 9;
		}

		return toRender.isEmpty() ? 0 : 8;
	}

	public static int renderEntityEffects(TESHudRenderContext renderContext, Minecraft mc, LivingEntity entity, float opacity) {
		TESConfig config = TESAPI.getConfig();

		if (renderContext.isInWorld() ? !config.inWorldHudPotionIcons() : !config.hudPotionIcons())
			return 0;

		EntityState entityState = TESEntityTracking.getStateForEntity(entity);

		if (entityState == null || entityState.getEffects().isEmpty())
			return 0;

		boolean inWorldHud = renderContext.isInWorld();
		int effectsSize = entityState.getEffects().size();
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

		for (Holder<MobEffect> effect : entityState.getEffects()) {
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

	public static int renderHorseStats(TESHudRenderContext renderContext, Minecraft mc, LivingEntity entity, float opacity) {
		TESConfig config = TESAPI.getConfig();

		if (renderContext.isInWorld() ? !config.inWorldHudHorseStats() : !config.hudHorseStats())
			return 0;

		if (!(entity instanceof AbstractHorse horse))
			return 0;

		boolean hasChest = entity instanceof AbstractChestedHorse chestedHorse && chestedHorse.hasChest();
		int x = renderContext.isInWorld() ? hasChest ? -18 : -11 : 1;
		double healthRatio = (horse.getAttributeValue(Attributes.MAX_HEALTH) - AbstractHorse.MIN_HEALTH) / (AbstractHorse.MAX_HEALTH - AbstractHorse.MIN_HEALTH);
		double moveSpeedRatio = (horse.getAttributeValue(Attributes.MOVEMENT_SPEED) - AbstractHorse.MIN_MOVEMENT_SPEED) / (AbstractHorse.MAX_MOVEMENT_SPEED - AbstractHorse.MIN_MOVEMENT_SPEED);
		double jumpStrengthRatio = (horse.getAttributeValue(Attributes.JUMP_STRENGTH) - AbstractHorse.MIN_JUMP_STRENGTH) / (AbstractHorse.MAX_JUMP_STRENGTH - AbstractHorse.MIN_JUMP_STRENGTH);
		TextRenderHelper health = TextRenderHelper.of("H:" + Mth.floor(healthRatio * 100) + "%").colour(ARGB.color(opacity, TESRenderUtil.colourGradeForValue(healthRatio)));
		TextRenderHelper speed = TextRenderHelper.of("S:" + Mth.floor(moveSpeedRatio * 100) + "%").colour(ARGB.color(opacity, TESRenderUtil.colourGradeForValue(moveSpeedRatio)));
		TextRenderHelper jumpStrength = TextRenderHelper.of("J:" + Mth.floor(jumpStrengthRatio * 100) + "%").colour(ARGB.color(opacity, TESRenderUtil.colourGradeForValue(jumpStrengthRatio)));
		TextureRenderHelper storageSprite = TextureRenderHelper.ofSprite(TESTextures.PROPERTY_STORAGE).sized(8, 8).colour(ARGB.white(opacity));

		if (hasChest) {
			storageSprite.render(renderContext, x, 1);

			x += 10;
		}

		health.render(renderContext, x, 1);
		speed.render(renderContext, x + 34, 1);
		jumpStrength.render(renderContext, x + 68, 1);

		return 9;
	}
}

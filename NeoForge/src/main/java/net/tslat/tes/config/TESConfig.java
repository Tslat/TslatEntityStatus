package net.tslat.tes.config;

import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.tslat.tes.api.TESConstants;
import net.tslat.tes.api.TESHUDActivation;
import net.tslat.tes.api.util.TESClientUtil;
import net.tslat.tes.core.hud.TESHud;
import net.tslat.tes.core.hud.TESHudPosition;
import org.apache.commons.lang3.tuple.Pair;

import java.util.function.BiConsumer;

public final class TESConfig implements net.tslat.tes.api.TESConfig {
	private final ModConfigSpec.DoubleValue entityTrackingDistance;
	private final ModConfigSpec.IntValue cacheCleanFrequency;

	private final ModConfigSpec.BooleanValue hudEnabled;
	private final ModConfigSpec.EnumValue<TESHudPosition> hudRenderPosition;
	private final ModConfigSpec.IntValue hudPositionLeftAdjust;
	private final ModConfigSpec.IntValue hudPositionTopAdjust;
	private final ModConfigSpec.DoubleValue hudTargetDistance;
	private final ModConfigSpec.IntValue hudTargetGracePeriod;
	private final ModConfigSpec.BooleanValue hudEntityRender;
	private final ModConfigSpec.BooleanValue hudPreventEntityOverflow;
	private final ModConfigSpec.EnumValue<TESHud.BarRenderType> hudHealthRenderType;
	private final ModConfigSpec.EnumValue<TESClientUtil.TextRenderType> hudHealthFontStyle;
	private final ModConfigSpec.BooleanValue hudHealthBarSegments;
	private final ModConfigSpec.IntValue hudHealthBarLength;
	private final ModConfigSpec.BooleanValue hudEntityDamageTint;
	private final ModConfigSpec.BooleanValue hudEntityName;
	private final ModConfigSpec.EnumValue<TESClientUtil.TextRenderType> hudEntityNameFontStyle;
	private final ModConfigSpec.BooleanValue hudBossesEnabled;
	private final ModConfigSpec.BooleanValue hudStats;
	private final ModConfigSpec.EnumValue<TESClientUtil.TextRenderType> hudStatsFontStyle;
	private final ModConfigSpec.BooleanValue hudEntityIcons;
	private final ModConfigSpec.BooleanValue hudPotionIcons;
	private final ModConfigSpec.BooleanValue hudHorseStats;
	private final ModConfigSpec.DoubleValue hudOpacity;
	private final ModConfigSpec.DoubleValue hudBarFontBackingOpacity;

	private final ModConfigSpec.BooleanValue inWorldBarsEnabled;
	private final ModConfigSpec.BooleanValue inWorldHudForSelf;
	private final ModConfigSpec.EnumValue<TESHUDActivation> inWorldHUDActivation;
	private final ModConfigSpec.DoubleValue inWorldHudOpacity;
	private final ModConfigSpec.EnumValue<TESHud.BarRenderType> inWorldBarsRenderType;
	private final ModConfigSpec.EnumValue<TESClientUtil.TextRenderType> inWorldHudHealthFontStyle;
	private final ModConfigSpec.IntValue inWorldBarsLength;
	private final ModConfigSpec.BooleanValue inWorldBarsSegments;
	private final ModConfigSpec.BooleanValue inWorldHudEntityName;
	private final ModConfigSpec.EnumValue<TESClientUtil.TextRenderType> inWorldHudEntityNameFontStyle;
	private final ModConfigSpec.BooleanValue inWorldHudStats;
	private final ModConfigSpec.EnumValue<TESClientUtil.TextRenderType> inWorldHudStatsFontStyle;
	private final ModConfigSpec.BooleanValue inWorldHudEntityIcons;
	private final ModConfigSpec.BooleanValue inWorldHudPotionIcons;
	private final ModConfigSpec.BooleanValue inWorldHudHorseStats;
	private final ModConfigSpec.BooleanValue inWorldHudNameOverride;
	private final ModConfigSpec.DoubleValue inWorldHudManualVerticalOffset;

	private final ModConfigSpec.BooleanValue particlesEnabled;
	private final ModConfigSpec.EnumValue<TESClientUtil.TextRenderType> particleFontStyle;
	private final ModConfigSpec.IntValue defaultParticleLifespan;
	private final ModConfigSpec.IntValue particleDecimalPoints;
	private final ModConfigSpec.DoubleValue particleScale;
	private final ModConfigSpec.BooleanValue verbalHealthParticles;
	private final ModConfigSpec.IntValue damageParticleColour;
	private final ModConfigSpec.IntValue healParticleColour;
	private final ModConfigSpec.BooleanValue teamBasedDamageParticleColours;

	public TESConfig(final ModConfigSpec.Builder config) {
		config.push("General Settings");

		this.entityTrackingDistance = config
				.comment("How close (in blocks) entities should be before TES starts tracking them")
				.translation("config.tes.general.entityTrackingDistance")
				.defineInRange("entityTrackingDistance", 64d, 8, 512);

		this.cacheCleanFrequency = config
				.comment("How frequently TES should clear out its tracking cache. Generally this should stay at default, but if you are noticing issues you can try adjusting it")
				.translation("config.tes.general.cacheCleanFrequency")
				.defineInRange("cacheCleanFrequency", 400, 20, Integer.MAX_VALUE);

		config.pop();
		config.push("HUD Settings");

		this.hudEnabled = config
				.comment("Whether the TES HUD should be enabled or not")
				.translation("config.tes.hud.enabled")
				.define("hudEnabled", true);

		this.hudRenderPosition = config
				.comment("What position the TES HUD should render in")
				.translation("config.tes.hud.renderPosition")
				.defineEnum("hudRenderPosition", TESHudPosition.TOP_LEFT);

		this.hudPositionLeftAdjust = config
				.comment("Manually adjust the left-offset rendering position of the TES HUD")
				.translation("config.tes.hud.leftOffset")
				.defineInRange("hudPositionLeftAdjust", 0, -100000, 100000);

		this.hudPositionTopAdjust = config
				.comment("Manually adjust the top-offset rendering position of the TES HUD")
				.translation("config.tes.hud.topOffset")
				.defineInRange("hudPositionTopAdjust", 0, -100000, 100000);

		this.hudTargetDistance = config
				.comment("How close (in blocks) the player has to be to render a HUD for an entity under the crosshairs",
						"Larger values may cost more performance")
				.translation("config.tes.hud.targetDistance")
				.defineInRange("hudTargetDistance", 64d, 4d, 256d);

		this.hudTargetGracePeriod = config
				.comment("How long (in ticks) after looking away from an entity before its HUD should stop rendering")
				.translation("config.tes.hud.targetGracePeriod")
				.defineInRange("hudTargetGracePeriod", 10, 0, Integer.MAX_VALUE);

		this.hudEntityRender = config
				.comment("Whether the TES HUD should render the entity's image")
				.translation("config.tes.hud.entityRender")
				.define("hudEntityRender", true);

		this.hudPreventEntityOverflow = config
				.comment("Whether the TES HUD should cull any overflow for entities that don't scale properly to their rendering frame")
				.translation("config.tes.hud.preventEntityOverflow")
				.define("hudPreventEntityIconOverflow", false);

		this.hudHealthRenderType = config
				.comment("Select the health render type for the TES HUD",
						"Options:",
						"NUMERIC - Use numeric values for health only",
						"BAR - Use a health-bar style render",
						"COMBINED - Use a health-bar style render with numeric values overlaid")
				.translation("config.tes.hud.healthRenderType")
				.defineEnum("healthRenderType", TESHud.BarRenderType.COMBINED);

		this.hudHealthFontStyle = config
				.comment("What style TES font should render in for entity health in the HUD (if applicable)")
				.translation("config.tes.hud.healthRenderType.fontStyle")
				.defineEnum("hudHealthFontStyle", TESClientUtil.TextRenderType.NORMAL);

		this.hudHealthBarSegments = config
				.comment("Set whether the TES HUD health bar should render bar-segments")
				.translation("config.tes.hud.healthBarSegments")
				.define("hudHealthBarSegments", true);

		this.hudHealthBarLength = config
				.comment("Set how long the TES HUD health bar should be")
				.translation("config.tes.hud.healthBarLength")
				.defineInRange("hudHealthBarLength", 100, 10, Integer.MAX_VALUE);

		this.hudEntityDamageTint = config
				.comment("Set whether the TES HUD's entity icon should keep the red 'tint' entities get when the real entity takes damage or not")
				.translation("config.tes.hud.entityDamageTint")
				.define("hudEntityDamageTint", false);

		this.hudEntityName = config
				.comment("Whether the TES HUD should render the entity's name")
				.translation("config.tes.hud.entityName")
				.define("hudEntityName", true);

		this.hudEntityNameFontStyle = config
				.comment("What style TES font should render in for entity names in the HUD")
				.translation("config.tes.hud.entityName.fontStyle")
				.defineEnum("hudEntityNameFontStyle", TESClientUtil.TextRenderType.DROP_SHADOW);

		this.hudBossesEnabled = config
				.comment("Whether the TES HUD should render if the entity is a boss (they usually have their own boss bars)")
				.translation("config.tes.hud.bossesEnabled")
				.define("hudBossesEnabled", true);

		this.hudStats = config
				.comment("Whether the TES HUD should render the entity's stats")
				.translation("config.tes.hud.stats")
				.define("hudStats", true);

		this.hudStatsFontStyle = config
				.comment("What style TES font should render in for entity stats values in the HUD")
				.translation("config.tes.hud.stats.fontStyle")
				.defineEnum("hudStatsFontStyle", TESClientUtil.TextRenderType.DROP_SHADOW);

		this.hudEntityIcons = config
				.comment("Whether the TES HUD should render the entity's alignment icons")
				.translation("config.tes.hud.entityIcons")
				.define("hudEntityIcons", true);

		this.hudPotionIcons = config
				.comment("Whether the TES HUD should render the entity's effect icons")
				.translation("config.tes.hud.potionIcons")
				.define("hudPotionIcons", true);

		this.hudHorseStats = config
				.comment("Whether the TES HUD should render horses' stats")
				.translation("config.tes.hud.horseStats")
				.define("hudHorseStats", true);

		this.hudOpacity = config
				.comment("Set how opaque the TES HUD should be, overall. The lower the value, the more transparent the HUD will be")
				.translation("config.tes.hud.opacity")
				.defineInRange("hudOpacity", 1d, 0d, 1d);

		this.hudBarFontBackingOpacity = config
				.comment("Set how opaque the background behind the text on TES bars, if a render type is set that renders text")
				.translation("config.tes.hud.barFontBackingOpacity")
				.defineInRange("hudBarFontBackingOpacity", 0.5f, 0d, 1d);

		config.pop();
		config.push("In-World Bars Settings");

		this.inWorldBarsEnabled = config
				.comment("Whether TES should do in-world entity status bars")
				.translation("config.tes.inWorldHud.enabled")
				.define("inWorldBarsEnabled", true);

		this.inWorldHudForSelf = config
				.comment("Whether the TES in-world HUD should be enabled for the player or not")
				.translation("config.tes.hud.self")
				.define("inWorldHudForSelf", false);

		this.inWorldHUDActivation = config
				.comment("When the TES in-world status bars should render",
						"Options:",
						"ALWAYS - Any entity currently visible",
						"NEARBY_ONLY - Only entities nearby",
						"DAMAGED_ONLY - Only entities that have less than full health",
						"DAMAGED_AND_NEARBY - Only entities that are nearby and have less than full health",
						"LOOKING_AT - Only the currently targeted entity",
						"LOOKING_AT_AND_DAMAGED - Only the currently targeted entity, if it has less than full health",
						"LOOKING_AT_NEARBY_AND_DAMAGED - Only the currently targeted entity if it is nearby and has less than full health",
						"NOT_LOOKING_AT - Only when the entity isn't the one being rendered for the on-screen HUD",
						"NOT_LOOKING_AT_AND_DAMAGED - Only if not the currently targeted entity, if it has less than full health",
						"NOT_LOOKING_AT_NEARBY_AND_DAMAGED - Only if not the currently targeted entity, it is nearby and has less than full health")
				.translation("config.tes.inWorldHud.activation")
				.defineEnum("inWorldHudActivation", TESHUDActivation.DAMAGED_AND_NEARBY);

		this.inWorldBarsRenderType = config
				.comment("Select the bar render type for the in-game TES entity status HUD",
						"Options:",
						"NUMERIC - Use numeric values only",
						"BAR - Use a health-bar style render",
						"COMBINED - Use a health-bar style render with numeric values overlaid")
				.translation("config.tes.inWorldHud.barsRenderType")
				.defineEnum("inWorldBarsRenderType", TESHud.BarRenderType.BAR);

		this.inWorldHudHealthFontStyle = config
				.comment("What style TES font should render in for entity health in the in-world HUD (if applicable)")
				.translation("config.tes.inWorldHud.health.fontStyle")
				.defineEnum("inWorldHudHealthFontStyle", TESClientUtil.TextRenderType.NORMAL);

		this.inWorldHudOpacity = config
				.comment("How opaque the TES in-world entity HUD should be.")
				.translation("config.tes.inWorldHud.opacity")
				.defineInRange("inWorldHudOpacity", 1d, 0d, 1d);

		this.inWorldBarsLength = config
				.comment("Set how long the TES in-world entity status bars should be")
				.translation("config.tes.inWorldHud.barLength")
				.defineInRange("inWorldBarsLength", 50, 10, Integer.MAX_VALUE);

		this.inWorldBarsSegments = config
				.comment("Whether the in-world entity status bars should be segmented")
				.translation("config.tes.inWorldHud.barSegments")
				.define("inWorldBarsSegments", true);

		this.inWorldHudEntityName = config
				.comment("Whether the in-world entity status HUD should render the entity's name")
				.translation("config.tes.inWorldHud.entityName")
				.define("inWorldHudEntityName", false);

		this.inWorldHudEntityNameFontStyle = config
				.comment("What style TES font should render in for entity names in the in-world HUD")
				.translation("config.tes.inWorldHud.entityName.fontStyle")
				.defineEnum("inWorldHudEntityNameFontStyle", TESClientUtil.TextRenderType.DROP_SHADOW);

		this.inWorldHudStats = config
				.comment("Whether the in-world entity status HUD should render the entity's stats values")
				.translation("config.tes.inWorldHud.stats")
				.define("inWorldHudStats", false);

		this.inWorldHudStatsFontStyle = config
				.comment("What style TES font should render in for entity stats values in the in-world HUD")
				.translation("config.tes.inWorldHud.stats.fontStyle")
				.defineEnum("inWorldHudStatsFontStyle", TESClientUtil.TextRenderType.DROP_SHADOW);

		this.inWorldHudEntityIcons = config
				.comment("Whether the in-world entity status HUD should render the entity's alignment icons")
				.translation("config.tes.inWorldHud.icons")
				.define("inWorldHudEntityIcons", false);

		this.inWorldHudPotionIcons = config
				.comment("Whether the in-world entity status HUD should render the entity's effects icons")
				.translation("config.tes.inWorldHud.potionIcons")
				.define("inWorldHudPotionIcons", false);

		this.inWorldHudHorseStats = config
				.comment("Whether the in-world TES entity status HUD should include horse stats")
				.translation("config.tes.inWorldHud.horseStats")
				.define("inWorldHudHorseStats", false);

		this.inWorldHudNameOverride = config
				.comment("Whether the in-world TES entity status HUD should override vanilla name rendering")
				.translation("config.tes.inWorldHud.nameOverride")
				.define("inWorldHudNameOverride", true);

		this.inWorldHudManualVerticalOffset = config
				.comment("Set a manual vertical offset for the TES in-world HUD (in blocks) in the event of other mods doing overhead rendering")
				.translation("config.tes.inWorldHud.manualVerticalOffset")
				.defineInRange("inWorldHudManualVerticalOffset", 0, Float.MIN_VALUE, Float.MAX_VALUE);

		config.pop();
		config.push("Particle Settings");

		this.particlesEnabled = config
				.comment("Whether TES should do particles for various status changes such as damage dealt or health healed")
				.translation("config.tes.particle.enabled")
				.define("tesParticlesEnabled", true);

		this.particleFontStyle = config
				.comment("What style TES particles' font should render in")
				.translation("config.tes.particle.fontStyle")
				.defineEnum("particleFontStyle", TESClientUtil.TextRenderType.OUTLINED);

		this.defaultParticleLifespan = config
				.comment("How long (in ticks) TES particles should display for")
				.translation("config.tes.particle.defaultLifespan")
				.defineInRange("defaultParticleLifespan", 60, 5, 200);

		this.particleDecimalPoints = config
				.comment("How many decimals the numeric TES Particles should round to",
						"Or set to 0 to only use whole-numbers")
				.translation("config.tes.particle.decimalPoints")
				.defineInRange("particleDecimalPoints", 1, 0, 8);

		this.particleScale = config
				.comment("Scale modifier for TES-Particles. The higher the value, the larger the particles")
				.translation("config.tes.particle.scale")
				.defineInRange("particleScale", 1d, 0d, 10d);

		this.verbalHealthParticles = config
				.comment("Whether TES should do verbal health-status particles (E.G. INSTAKILL) in certain situations")
				.translation("config.tes.particle.verbal")
				.define("verbalHealthParticles", true);

		this.damageParticleColour = config
				.comment("What colour the damage-type particles should be. Value can be a packed int, byte, or hex value. Format is ARGB")
				.translation("config.tes.particle.damageParticleColour")
				.defineInRange("damageParticleColour", 0xFFFF0000, Integer.MIN_VALUE, Integer.MAX_VALUE);

		this.healParticleColour = config
				.comment("What colour the healing-type particles should be. Value can be a packed int, byte, or hex value. Format is ARGB")
				.translation("config.tes.particle.healParticleColour")
				.defineInRange("healParticleColour", 0xFF00FF00, Integer.MIN_VALUE, Integer.MAX_VALUE);

		this.teamBasedDamageParticleColours = config
				.comment("Whether TES should change the colour of damage particles to the colour of the team that dealt the damage (if applicable)")
				.translation("config.tes.particle.teamColours")
				.define("teamBasedDamageParticleColours", false);

		config.pop();
	}

	@Override
	public double getEntityTrackingDistance() {
		return this.entityTrackingDistance.get();
	}

	@Override
	public int getCacheCleanFrequency() {
		return this.cacheCleanFrequency.get();
	}

	@Override
	public boolean hudEnabled() {
		return this.hudEnabled.get();
	}

	@Override
	public TESHudPosition hudRenderPosition() {
		return this.hudRenderPosition.get();
	}

	@Override
	public int hudPositionLeftAdjust() {
		return this.hudPositionLeftAdjust.get();
	}

	@Override
	public int hudPositionTopAdjust() {
		return this.hudPositionTopAdjust.get();
	}

	@Override
	public double getHudTargetDistance() {
		return this.hudTargetDistance.get();
	}

	@Override
	public int hudTargetGracePeriod() {
		return this.hudTargetGracePeriod.get();
	}

	@Override
	public boolean hudEntityRender() {
		return this.hudEntityRender.get();
	}

	@Override
	public boolean hudPreventEntityOverflow() {
		return this.hudPreventEntityOverflow.get();
	}

	@Override
	public boolean hudEntityDamageOverlay() {
		return this.hudEntityDamageTint.get();
	}

	@Override
	public boolean hudEntityName() {
		return this.hudEntityName.get();
	}

	@Override
	public TESClientUtil.TextRenderType hudEntityNameFontStyle() {
		return this.hudEntityNameFontStyle.get();
	}

	@Override
	public boolean hudBossesEnabled() {
		return this.hudBossesEnabled.get();
	}

	@Override
	public boolean hudStats() {
		return hudStats.get();
	}

	@Override
	public TESClientUtil.TextRenderType hudStatsFontStyle() {
		return this.hudStatsFontStyle.get();
	}

	@Override
	public boolean hudEntityIcons() {
		return hudEntityIcons.get();
	}

	@Override
	public boolean hudPotionIcons() {
		return hudPotionIcons.get();
	}

	@Override
	public boolean hudHorseStats() {
		return hudHorseStats.get();
	}

	@Override
	public float hudOpacity() {
		return this.hudOpacity.get().floatValue();
	}

	@Override
	public float hudBarFontBackingOpacity() {
		return this.hudBarFontBackingOpacity.get().floatValue();
	}

	@Override
	public TESHud.BarRenderType hudHealthRenderType() {
		return this.hudHealthRenderType.get();
	}

	@Override
	public TESClientUtil.TextRenderType hudHealthFontStyle() {
		return this.hudHealthFontStyle.get();
	}

	@Override
	public boolean hudHealthBarSegments() {
		return this.hudHealthBarSegments.get();
	}

	@Override
	public int hudHealthBarLength() {
		return this.hudHealthBarLength.get();
	}

	@Override
	public boolean inWorldBarsEnabled() {
		return this.inWorldBarsEnabled.get();
	}

	@Override
	public boolean inWorldHudForSelf() {
		return this.inWorldHudForSelf.get();
	}

	@Override
	public TESHUDActivation inWorldHUDActivation() {
		return this.inWorldHUDActivation.get();
	}

	@Override
	public float inWorldHudOpacity() {
		return this.inWorldHudOpacity.get().floatValue();
	}

	@Override
	public TESHud.BarRenderType inWorldBarsRenderType() {
		return this.inWorldBarsRenderType.get();
	}

	@Override
	public TESClientUtil.TextRenderType inWorldHudHealthFontStyle() {
		return this.inWorldHudHealthFontStyle.get();
	}

	@Override
	public int inWorldBarsLength() {
		return this.inWorldBarsLength.get();
	}

	@Override
	public boolean inWorldBarsSegments() {
		return this.inWorldBarsSegments.get();
	}

	@Override
	public boolean inWorldHudEntityName() {
		return this.inWorldHudEntityName.get();
	}

	@Override
	public TESClientUtil.TextRenderType inWorldHudEntityNameFontStyle() {
		return this.inWorldHudEntityNameFontStyle.get();
	}

	@Override
	public boolean inWorldHudStats() {
		return this.inWorldHudStats.get();
	}

	@Override
	public TESClientUtil.TextRenderType inWorldHudStatsFontStyle() {
		return this.inWorldHudStatsFontStyle.get();
	}

	@Override
	public boolean inWorldHudEntityIcons() {
		return this.inWorldHudEntityIcons.get();
	}

	@Override
	public boolean inWorldHudPotionIcons() {
		return this.inWorldHudPotionIcons.get();
	}

	@Override
	public boolean inWorldHudHorseStats() {
		return this.inWorldHudHorseStats.get();
	}

	@Override
	public boolean inWorldHudNameOverride() {
		return this.inWorldHudNameOverride.get();
	}

	@Override
	public float inWorldHudManualVerticalOffset() {
		return this.inWorldHudManualVerticalOffset.get().floatValue();
	}

	@Override
	public boolean particlesEnabled() {
		return this.particlesEnabled.get();
	}

	@Override
	public TESClientUtil.TextRenderType particleFontStyle() {
		return this.particleFontStyle.get();
	}

	@Override
	public int defaultParticleLifespan() {
		return this.defaultParticleLifespan.get();
	}

	@Override
	public int particleDecimalPoints() {
		return this.particleDecimalPoints.get();
	}

	@Override
	public float getParticleScale() {
		return this.particleScale.get().floatValue();
	}

	@Override
	public boolean verbalHealthParticles() {
		return this.verbalHealthParticles.get();
	}

	@Override
	public int getDamageParticleColour() {
		return this.damageParticleColour.get();
	}

	@Override
	public int getHealParticleColour() {
		return this.healParticleColour.get();
	}

	@Override
	public boolean teamBasedDamageParticleColours() {
		return this.teamBasedDamageParticleColours.get();
	}

	public static void init(BiConsumer<ModConfig.Type, ModConfigSpec> registrar) {
		Pair<TESConfig, ModConfigSpec> configSpec = new ModConfigSpec.Builder().configure(TESConfig::new);

		registrar.accept(ModConfig.Type.CLIENT, configSpec.getRight());
		TESConstants.setConfig(configSpec.getLeft());
	}
}

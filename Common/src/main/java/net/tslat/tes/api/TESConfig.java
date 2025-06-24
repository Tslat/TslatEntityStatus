package net.tslat.tes.api;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.tslat.tes.api.object.TESHUDActivation;
import net.tslat.tes.api.object.TextRenderStyle;
import net.tslat.tes.core.hud.TESHud;
import net.tslat.tes.core.hud.TESHudPosition;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Common configuration interface for TES' configurable settings.
 */
public final class TESConfig {
	private final ModConfigSpec.DoubleValue entityTrackingDistance;

	private final ModConfigSpec.BooleanValue hudEnabled;
	private final ModConfigSpec.EnumValue<TESHudPosition> hudRenderPosition;
	private final ModConfigSpec.IntValue hudPositionLeftAdjust;
	private final ModConfigSpec.IntValue hudPositionTopAdjust;
	private final ModConfigSpec.DoubleValue hudTargetDistance;
	private final ModConfigSpec.IntValue hudTargetGracePeriod;
	private final ModConfigSpec.BooleanValue hudEntityRender;
	private final ModConfigSpec.BooleanValue hudPreventEntityOverflow;
	private final ModConfigSpec.EnumValue<TESHud.BarRenderType> hudHealthRenderType;
	private final ModConfigSpec.EnumValue<TextRenderStyle> hudHealthFontStyle;
	private final ModConfigSpec.BooleanValue hudHealthBarSegments;
	private final ModConfigSpec.IntValue hudHealthBarLength;
	private final ModConfigSpec.BooleanValue hudEntityDamageTint;
	private final ModConfigSpec.BooleanValue hudEntityName;
	private final ModConfigSpec.BooleanValue hudEntityNamespace;
	private final ModConfigSpec.EnumValue<TextRenderStyle> hudEntityNameFontStyle;
	private final ModConfigSpec.BooleanValue hudBossesEnabled;
	private final ModConfigSpec.BooleanValue hudStats;
	private final ModConfigSpec.EnumValue<TextRenderStyle> hudStatsFontStyle;
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
	private final ModConfigSpec.EnumValue<TextRenderStyle> inWorldHudHealthFontStyle;
	private final ModConfigSpec.IntValue inWorldBarsLength;
	private final ModConfigSpec.BooleanValue inWorldBarsSegments;
	private final ModConfigSpec.BooleanValue inWorldHudEntityName;
	private final ModConfigSpec.BooleanValue inWorldHudEntityNamespace;
	private final ModConfigSpec.EnumValue<TextRenderStyle> inWorldHudEntityNameFontStyle;
	private final ModConfigSpec.BooleanValue inWorldHudBossesEnabled;
	private final ModConfigSpec.BooleanValue inWorldHudStats;
	private final ModConfigSpec.EnumValue<TextRenderStyle> inWorldHudStatsFontStyle;
	private final ModConfigSpec.BooleanValue inWorldHudEntityIcons;
	private final ModConfigSpec.BooleanValue inWorldHudPotionIcons;
	private final ModConfigSpec.BooleanValue inWorldHudHorseStats;
	private final ModConfigSpec.BooleanValue inWorldHudNameOverride;
	private final ModConfigSpec.DoubleValue inWorldHudManualVerticalOffset;

	private final ModConfigSpec.BooleanValue particlesEnabled;
	private final ModConfigSpec.EnumValue<TextRenderStyle> particleFontStyle;
	private final ModConfigSpec.IntValue defaultParticleLifespan;
	private final ModConfigSpec.IntValue particleDecimalPoints;
	private final ModConfigSpec.DoubleValue particleScale;
	private final ModConfigSpec.BooleanValue verbalHealthParticles;
	private final ModConfigSpec.IntValue damageParticleColour;
	private final ModConfigSpec.IntValue healParticleColour;
	private final ModConfigSpec.BooleanValue teamBasedDamageParticleColours;

	TESConfig(final ModConfigSpec.Builder config) {
		config.push("General Settings");

		this.entityTrackingDistance = config
				.comment("How close (in blocks) entities should be before TES starts tracking them")
				.translation("config.tes.general.entityTrackingDistance")
				.defineInRange("entityTrackingDistance", 64d, 8, 512);

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
						"BAR_ICONS - Display in the BAR style, but with the health numerically displayed in the stats icons row",
						"COMBINED - Use a health-bar style render with numeric values overlaid")
				.translation("config.tes.hud.healthRenderType")
				.defineEnum("healthRenderType", TESHud.BarRenderType.COMBINED);

		this.hudHealthFontStyle = config
				.comment("What style TES font should render in for entity health in the HUD (if applicable)")
				.translation("config.tes.hud.healthRenderType.fontStyle")
				.defineEnum("hudHealthFontStyle", TextRenderStyle.NORMAL);

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

		this.hudEntityNamespace = config
				.comment("Whether the TES HUD should render the entity's mod ID under its name")
				.translation("config.tes.hud.entityNamespace")
				.define("hudEntityNamespace", false);

		this.hudEntityNameFontStyle = config
				.comment("What style TES font should render in for entity names in the HUD")
				.translation("config.tes.hud.entityName.fontStyle")
				.defineEnum("hudEntityNameFontStyle", TextRenderStyle.DROP_SHADOW);

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
				.defineEnum("hudStatsFontStyle", TextRenderStyle.DROP_SHADOW);

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
						"BAR_ICONS - Display in the BAR style, but with the health numerically displayed in the stats icons row",
						"COMBINED - Use a health-bar style render with numeric values overlaid")
				.translation("config.tes.inWorldHud.barsRenderType")
				.defineEnum("inWorldBarsRenderType", TESHud.BarRenderType.BAR);

		this.inWorldHudHealthFontStyle = config
				.comment("What style TES font should render in for entity health in the in-world HUD (if applicable)")
				.translation("config.tes.inWorldHud.health.fontStyle")
				.defineEnum("inWorldHudHealthFontStyle", TextRenderStyle.NORMAL);

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

		this.inWorldHudEntityNamespace = config
				.comment("Whether the in-world entity status HUD should render the entity's mod ID under its name")
				.translation("config.tes.inWorldHud.entityNamespace")
				.define("inWorldHudEntityNamespace", false);

		this.inWorldHudBossesEnabled = config
				.comment("Whether the in-world entity status HUD should render if the entity is a boss (they usually have their own boss bars)")
				.translation("config.tes.hud.inWorldHud.bossesEnabled")
				.define("inWorldHudBossesEnabled", true);

		this.inWorldHudEntityNameFontStyle = config
				.comment("What style TES font should render in for entity names in the in-world HUD")
				.translation("config.tes.inWorldHud.entityName.fontStyle")
				.defineEnum("inWorldHudEntityNameFontStyle", TextRenderStyle.DROP_SHADOW);

		this.inWorldHudStats = config
				.comment("Whether the in-world entity status HUD should render the entity's stats values")
				.translation("config.tes.inWorldHud.stats")
				.define("inWorldHudStats", false);

		this.inWorldHudStatsFontStyle = config
				.comment("What style TES font should render in for entity stats values in the in-world HUD")
				.translation("config.tes.inWorldHud.stats.fontStyle")
				.defineEnum("inWorldHudStatsFontStyle", TextRenderStyle.DROP_SHADOW);

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
				.defineEnum("particleFontStyle", TextRenderStyle.OUTLINED);

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

	/**
	 * Gets the distance (in blocks) that entities must be within for TES to track them
	 */
	public double getEntityTrackingDistance() {
		return this.entityTrackingDistance.get();
	}

	/**
	 * Whether the TES HUD should be rendered or not
	 */
	public boolean hudEnabled() {
		return this.hudEnabled.get();
	}

	/**
	 * What position the TES HUD should render in
	 */
	public TESHudPosition hudRenderPosition() {
		return this.hudRenderPosition.get();
	}

	/**
	 * Manually adjust the left-offset rendering position of the TES HUD
	 */
	public int hudPositionLeftAdjust() {
		return this.hudPositionLeftAdjust.get();
	}

	/**
	 * Manually adjust the top-offset rendering position of the TES HUD
	 */
	public int hudPositionTopAdjust() {
		return this.hudPositionTopAdjust.get();
	}

	/**
	 * How far away a targeted entity can be before TES stops rendering a HUD for it
	 */
	public double hudTargetDistance() {
		return this.hudTargetDistance.get();
	}

	/**
	 * How long (in ticks) after looking away from an entity before its HUD should stop rendering
	 */
	public int hudTargetGracePeriod() {
		return this.hudTargetGracePeriod.get();
	}

	/**
	 * Whether the TES HUD should render the entity's image
	 */
	public boolean hudEntityRender() {
		return this.hudEntityRender.get();
	}

	/**
	 * Whether the TES HUD should cull any overflow for entities that don't scale properly to their rendering frame
	 */
	public boolean hudPreventEntityOverflow() {
		return this.hudPreventEntityOverflow.get();
	}

	/**
	 * Whether the entity rendered in the TES HUD should keep the red overlay when the real entity takes damage
	 */
	public boolean hudEntityDamageOverlay() {
		return this.hudEntityDamageTint.get();
	}

	/**
	 * Whether the TES HUD Should render the entity's name
	 */
	public boolean hudEntityName() {
		return this.hudEntityName.get();
	}

	/**
	 * Whether the TES HUD Should render the entity's name
	 */
	public boolean hudEntityNamespace() {
		return this.hudEntityNamespace.get();
	}

	/**
	 * What style TES font should render in for entity names in the HUD
	 */
	public TextRenderStyle hudEntityNameFontStyle() {
		return this.hudEntityNameFontStyle.get();
	}

	/**
	 * Whether the TES HUD Should render for boss-type entities
	 */
	public boolean hudBossesEnabled() {
		return this.hudBossesEnabled.get();
	}

	/**
	 * Whether the TES HUD should render the entity's stats
	 */
	public boolean hudStats() {
		return hudStats.get();
	}

	/**
	 * What style TES font should render in for entity stats values in the HUD
	 */
	public TextRenderStyle hudStatsFontStyle() {
		return this.hudStatsFontStyle.get();
	}

	/**
	 * Whether the TES HUD should render the entity's alignment icons
	 */
	public boolean hudEntityIcons() {
		return hudEntityIcons.get();
	}

	/**
	 * Whether the TES HUD should render the entity's potion icons
	 */
	public boolean hudPotionIcons() {
		return hudPotionIcons.get();
	}

	/**
	 * Whether the TES HUD should render horses' stats
	 */
	public boolean hudHorseStats() {
		return hudHorseStats.get();
	}

	/**
	 * Get the rendered opacity of the TES HUD.<br>
	 * This affects the entire HUD.<br>
	 * Value range is 0->1, with 1 being 100% opacity
	 */
	public float hudOpacity() {
		return this.hudOpacity.get().floatValue();
	}

	/**
	 * Set how opaque the background behind the text on TES bars, if a render type is set that renders text
	 */
	public float hudBarFontBackingOpacity() {
		return this.hudBarFontBackingOpacity.get().floatValue();
	}

	/**
	 * Get the health render type for the TES HUD.<br>
	 * Available options are:
	 * <ul>
	 *     <li>NUMERIC - Use numeric values for health only</li>
	 *     <li>BAR - Use a health-bar style render</li>
	 *     <li>BAR_ICONS - Display in the BAR style, but with the health numerically displayed in the stats icons row</li>
	 *     <li>COMBINED - Use a health-bar style render with numeric values overlaid</li>
	 * </ul>
	 */
	public TESHud.BarRenderType hudHealthRenderType() {
		return this.hudHealthRenderType.get();
	}

	/**
	 * What style TES font should render in for entity health in the HUD (if applicable)
	 */
	public TextRenderStyle hudHealthFontStyle() {
		return this.hudHealthFontStyle.get();
	}

	/**
	 * Whether the health bar in the TES HUD should render with health bar segments
	 */
	public boolean hudHealthBarSegments() {
		return this.hudHealthBarSegments.get();
	}

	/**
	 * How long the TES HUD health bar should be (in pixels)
	 */
	public int hudHealthBarLength() {
		return this.hudHealthBarLength.get();
	}

	/**
	 * Whether the TES in-world entity status GUIs are enabled
	 */
	public boolean inWorldBarsEnabled() {
		return this.inWorldBarsEnabled.get();
	}

	/**
	 * Whether the TES HUD should be rendered for the player or not
	 */
	public boolean inWorldHudForSelf() {
		return this.inWorldHudForSelf.get();
	}

	/**
	 * When the TES in-world entity status bars should render
	 */
	public TESHUDActivation inWorldHUDActivation() {
		return this.inWorldHUDActivation.get();
	}

	/**
	 * How opaque the in-world TES entity status HUD should be
	 */
	public float inWorldHudOpacity() {
		return this.inWorldHudOpacity.get().floatValue();
	}

	/**
	 * Get the bar render type for the in-world TES entity status HUD.<br>
	 * Available options are:
	 * <ul>
	 *     <li>NUMERIC - Use numeric values only</li>
	 *     <li>BAR - Use a health-bar style render</li>
	 *     <li>BAR_ICONS - Display in the BAR style, but with the health numerically displayed in the stats icons row</li>
	 *     <li>COMBINED - Use a health-bar style render with numeric values overlaid</li>
	 * </ul>
	 */
	public TESHud.BarRenderType inWorldBarsRenderType() {
		return this.inWorldBarsRenderType.get();
	}

	/**
	 * What style TES font should render in for entity health in the in-world HUD (if applicable)
	 */
	public TextRenderStyle inWorldHudHealthFontStyle() {
		return this.inWorldHudHealthFontStyle.get();
	}

	/**
	 * How long the TES in-world entity status health bars should be (in pixels)
	 */
	public int inWorldBarsLength() {
		return this.inWorldBarsLength.get();
	}

	/**
	 * Whether the in-world TES entity status HUD's bars should be segmented
	 */
	public boolean inWorldBarsSegments() {
		return this.inWorldBarsSegments.get();
	}

	/**
	 * Whether the in-world TES entity status HUD should include the entity's name
	 */
	public boolean inWorldHudEntityName() {
		return this.inWorldHudEntityName.get();
	}

	/**
	 * Whether the in-world TES entity status HUD should include the entity's mod ID under its name
	 */
	public boolean inWorldHudEntityNamespace() {
		return this.inWorldHudEntityNamespace.get();
	}

	/**
	 * What style TES font should render in for entity names in the in-world HUD
	 */
	public TextRenderStyle inWorldHudEntityNameFontStyle() {
		return this.inWorldHudEntityNameFontStyle.get();
	}

	/**
	 * What style TES font should render in for entity names in the in-world HUD
	 */
	public boolean inWorldHudBossesEnabled() {
		return this.inWorldHudBossesEnabled.get();
	}

	/**
	 * Whether the in-world TES entity status HUD should include the entity's stats values
	 */
	public boolean inWorldHudStats() {
		return this.inWorldHudStats.get();
	}

	/**
	 * What style TES font should render in for entity stats values in the in-world HUD
	 */
	public TextRenderStyle inWorldHudStatsFontStyle() {
		return this.inWorldHudStatsFontStyle.get();
	}

	/**
	 * Whether the in-world TES entity status HUD should include the entity's alignment icons
	 */
	public boolean inWorldHudEntityIcons() {
		return this.inWorldHudEntityIcons.get();
	}

	/**
	 * Whether the in-world TES entity status HUD should include the entity's effect icons
	 */
	public boolean inWorldHudPotionIcons() {
		return this.inWorldHudPotionIcons.get();
	}

	/**
	 * Whether the in-world TES entity status HUD should include horse stats
	 */
	public boolean inWorldHudHorseStats() {
		return this.inWorldHudHorseStats.get();
	}

	/**
	 * Whether the in-world TES entity status HUD should override vanilla name rendering
	 */
	public boolean inWorldHudNameOverride() {
		return this.inWorldHudNameOverride.get();
	}

	/**
	 * A manual vertical offset for the TES in-world HUD (in blocks)
	 */
	public float inWorldHudManualVerticalOffset() {
		return this.inWorldHudManualVerticalOffset.get().floatValue();
	}

	/**
	 * Whether TES should do particles for various status changes such as damage dealt or health healed
	 */
	public boolean particlesEnabled() {
		return this.particlesEnabled.get();
	}

	/**
	 * What style TES particles' font should render in
	 */
	public TextRenderStyle particleFontStyle() {
		return this.particleFontStyle.get();
	}

	/**
	 * How long (in ticks) particles should survive for by default
	 */
	public int defaultParticleLifespan() {
		return this.defaultParticleLifespan.get();
	}

	/**
	 * How many decimals the numeric TES Particles should round to
	 */
	public int particleDecimalPoints() {
		return this.particleDecimalPoints.get();
	}

	/**
	 * Get the scale modifier for TES Particle rendering.<br>
	 * A value of 2 is double the standard rendering size.
	 */
	public float particleScale() {
		return this.particleScale.get().floatValue();
	}

	/**
	 * Gets whether the verbal health particles are enabled
	 */
	public boolean verbalHealthParticles() {
		return this.verbalHealthParticles.get();
	}

	/**
	 * Get the packed-int colour value of the damage-type TES particle.<br>
	 * Format is ARGB
	 */
	public int getDamageParticleColour() {
		return this.damageParticleColour.get();
	}

	/**
	 * Get the packed-int colour value of the healing-type TES particle<br>
	 * Format is ARGB
	 */
	public int getHealParticleColour() {
		return this.healParticleColour.get();
	}

	/**
	 * Whether TES should colour its damage particles based on the team that dealt the damage
	 */
	public boolean teamBasedDamageParticleColours() {
		return this.teamBasedDamageParticleColours.get();
	}

	/**
	 * Whether TES currently has mob effect syncing active
	 */
	public boolean isSyncingEffects() {
		return TESConstants.HAS_SERVER_CONNECTION && (hudPotionIcons() || inWorldHudPotionIcons());
	}

	public static ModConfigSpec init() {
		Pair<TESConfig, ModConfigSpec> configSpec = new ModConfigSpec.Builder().configure(TESConfig::new);

		TESConstants.setConfig(configSpec.getLeft());

		return configSpec.getRight();
	}
}

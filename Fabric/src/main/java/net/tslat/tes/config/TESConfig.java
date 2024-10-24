package net.tslat.tes.config;

import eu.midnightdust.lib.config.MidnightConfig;
import net.tslat.tes.api.TESHUDActivation;
import net.tslat.tes.api.util.TESClientUtil;
import net.tslat.tes.core.hud.TESHud;
import net.tslat.tes.core.hud.TESHudPosition;

public final class TESConfig extends MidnightConfig implements net.tslat.tes.api.TESConfig {
	@Comment public static final String entityTrackingDistanceComment = "How close (in blocks) entities should be before TES starts tracking them";
	@Entry(name = "Entity Tracking Distance", isSlider = true, min = 8d, max = 512d)
	public static double entityTrackingDistance = 64;

	@Comment public static final String cacheCleanFrequencyComment = "How frequently TES should clear out its tracking cache. Generally this should stay at default, but if you are noticing issues you can try adjusting it";
	@Entry(name = "Cache Clean Frequency", min = 20, max = Integer.MAX_VALUE)
	public static int cacheCleanFrequency = 400;

	@Comment public static final String hudEnabledComment = "Whether the TES HUD should be enabled or not";
	@Entry(name = "HUD Enabled")
	public static boolean hudEnabled = true;

	@Comment public static final String hudRenderPositionComment = "What position the TES HUD should render in";
	@Entry(name = "HUD Render Position")
	public static TESHudPosition hudRenderPosition = TESHudPosition.TOP_LEFT;

	@Comment public static final String hudLeftOffsetComment = "Manually adjust the left-offset rendering position of the TES HUD";
	@Entry(name = "HUD Position Left Offset", min = -100000, max = 100000)
	public static int hudLeftOffset = 0;

	@Comment public static final String hudTopOffsetComment = "Manually adjust the top-offset rendering position of the TES HUD";
	@Entry(name = "HUD Position Top Offset", min = -100000, max = 100000)
	public static int hudTopOffset = 0;

	@Comment public static final String hudTargetDistanceComment = "How close (in blocks) the player has to be to render a HUD for an entity under the crosshairs";
	@Comment public static final String hudTargetDistanceComment2 = "Larger values may cost more performance";
	@Entry(name = "HUD Target Distance", isSlider = true, min = 4d, max = 256d)
	public static double hudTargetDistance = 64d;

	@Comment public static final String hudTargetGracePeriodComment = "How long (in ticks) after looking away from an entity before its HUD should stop rendering";
	@Entry(name = "HUD Target Grace Period", min = 0, max = Integer.MAX_VALUE)
	public static int hudTargetGracePeriod = 10;

	@Comment public static final String hudEntityRenderComment = "Whether the TES HUD should render the entity's image";
	@Entry(name = "HUD Entity Render")
	public static boolean hudEntityRender = true;

	@Comment public static final String hudPreventEntityOverflowComment = "Whether the TES HUD should cull any overflow for entities that don't scale properly to their rendering frame";
	@Entry(name = "HUD Prevent Entity Overflow")
	public static boolean hudPreventEntityOverflow = false;

	@Comment public static final String hudHealthRenderTypeComment = "Select the health render type for the TES HUD";
	@Comment public static final String hudHealthRenderTypeComment2 = "Options:";
	@Comment public static final String hudHealthRenderTypeComment3 = "NUMERIC - Use numeric values for health only";
	@Comment public static final String hudHealthRenderTypeComment4 = "BAR - Use a health-bar style render";
	@Comment public static final String hudHealthRenderTypeComment5 = "COMBINED - Use a health-bar style render with numeric values overlaid";
	@Entry(name = "HUD Health Render Type")
	public static TESHud.BarRenderType hudBarRenderType = TESHud.BarRenderType.COMBINED;

	@Comment public static final String hudHealthFontStyleComment = "What style TES font should render in for entity health in the HUD (if applicable)";
	@Comment public static final String hudHealthFontStyleComment2 = "Options:";
	@Comment public static final String hudHealthFontStyleComment3 = "NORMAL - Only the text with no additional stylings";
	@Comment public static final String hudHealthFontStyleComment4 = "DROP_SHADOW - Text with a bottom-right offset shadow";
	@Comment public static final String hudHealthFontStyleComment5 = "GLOWING - Text with a large high-contrast outline to emphasize the text.";
	@Comment public static final String hudHealthFontStyleComment6 = "OUTLINED - Text with a thin dark outline to emphasize the text. Selected by default.";
	@Entry(name = "HUD Health Font Style")
	public static TESClientUtil.TextRenderType hudHealthFontStyle = TESClientUtil.TextRenderType.NORMAL;

	@Comment public static final String hudHealthBarSegmentsComment = "Set whether the TES HUD health bar should render bar-segments";
	@Entry(name = "HUD Health Bar Segments")
	public static boolean hudHealthBarSegments = true;

	@Comment public static final String hudHealthBarLengthComment = "Set how long the TES HUD health bar should be";
	@Entry(name = "HUD Health Bar Length", min = 10, max = Integer.MAX_VALUE)
	public static int hudHealthBarLength = 100;

	@Comment public static final String hudEntityDamageTintComment = "Set whether the TES HUD's entity icon should keep the red 'tint' entities get when the real entity takes damage or not";
	@Entry(name = "HUD Entity Damage Tint")
	public static boolean hudEntityDamageTint = false;

	@Comment public static final String hudEntityNameComment = "Whether the TES HUD should render the entity's name";
	@Entry(name = "HUD Entity Name")
	public static boolean hudEntityName = true;

	@Comment public static final String hudEntityNameFontRenderStyleComment = "What style TES font should render in for entity names in the HUD";
	@Comment public static final String hudEntityNameFontRenderStyleComment2 = "Options:";
	@Comment public static final String hudEntityNameFontRenderStyleComment3 = "NORMAL - Only the text with no additional stylings";
	@Comment public static final String hudEntityNameFontRenderStyleComment4 = "DROP_SHADOW - Text with a bottom-right offset shadow";
	@Comment public static final String hudEntityNameFontRenderStyleComment5 = "GLOWING - Text with a large high-contrast outline to emphasize the text.";
	@Comment public static final String hudEntityNameFontRenderStyleComment6 = "OUTLINED - Text with a thin dark outline to emphasize the text. Selected by default.";
	@Entry(name = "HUD Entity Name Font Style")
	public static TESClientUtil.TextRenderType hudEntityNameFontStyle = TESClientUtil.TextRenderType.DROP_SHADOW;

	@Comment public static final String hudBossesEnabledComment = "Whether the TES HUD should render for boss-type enemies (they usually have their own boss bar)";
	@Entry(name = "HUD Bosses Enabled")
	public static boolean hudBossesEnabled = true;

	@Comment public static final String hudStatsComment = "Whether the TES HUD should render the entity's stats";
	@Entry(name = "HUD Entity Stats")
	public static boolean hudStats = true;

	@Comment public static final String hudStatsFontStyleComment = "What style TES font should render in for entity stats values in the HUD";
	@Comment public static final String hudStatsFontStyleComment2 = "Options:";
	@Comment public static final String hudStatsFontStyleComment3 = "NORMAL - Only the text with no additional stylings";
	@Comment public static final String hudStatsFontStyleComment4 = "DROP_SHADOW - Text with a bottom-right offset shadow";
	@Comment public static final String hudStatsFontStyleComment5 = "GLOWING - Text with a large high-contrast outline to emphasize the text.";
	@Comment public static final String hudStatsFontStyleComment6 = "OUTLINED - Text with a thin dark outline to emphasize the text. Selected by default.";
	@Entry(name = "HUD Stats Font Style")
	public static TESClientUtil.TextRenderType hudStatsFontStyle = TESClientUtil.TextRenderType.DROP_SHADOW;

	@Comment public static final String hudEntityIconsComment = "Whether the TES HUD should render the entity's alignment icons";
	@Entry(name = "HUD Entity Icons")
	public static boolean hudEntityIcons = true;

	@Comment public static final String hudPotionIconsComment = "Whether the TES HUD should render the entity's effect icons";
	@Entry(name = "HUD Entity Effects Icons")
	public static boolean hudPotionIcons = true;

	@Comment public static final String hudHorseStatsComment = "Whether the TES HUD should render horses' stats";
	@Entry(name = "HUD Horse Stats")
	public static boolean hudHorseStats = true;

	@Comment public static final String hudOpacityComment = "Set how opaque the TES HUD should be, overall. The lower the value, the more transparent the HUD will be";
	@Entry(name = "HUD Opacity", isSlider = true, min = 0f, max = 1f)
	public static float hudOpacity = 1f;

	@Comment public static final String hudBarFontBackingOpacityComment = "Set how opaque the background behind the text on TES bars, if a render type is set that renders text";
	@Entry(name = "TES Bar Font Backing Opacity", isSlider = true, min = 0f, max = 1f)
	public static float hudBarFontBackingOpacity = 0.5f;

	// In-world HUD //

	@Comment public static final String inWorldHudEnabledComment = "Whether TES should do in-world entity status bars";
	@Entry(name = "In-World HUD Enabled")
	public static boolean inWorldHudEnabled = true;

	@Comment public static final String inWorldHudForSelfComment = "Whether the TES In-World HUD should be enabled for the player or not";
	@Entry(name = "In-World HUD For Self")
	public static boolean inWorldHudForSelf = false;

	@Comment public static final String inWorldHUDActivationComment = "When the TES in-world status bars should render";
	@Comment public static final String inWorldHUDActivationComment2 = "Options:";
	@Comment public static final String inWorldHUDActivationComment3 = "ALWAYS - Any entity currently visible";
	@Comment public static final String inWorldHUDActivationComment4 = "NEARBY_ONLY - Only entities nearby";
	@Comment public static final String inWorldHUDActivationComment5 = "DAMAGED_ONLY - Only entities that have less than full health";
	@Comment public static final String inWorldHUDActivationComment6 = "DAMAGED_AND_NEARBY - Only entities that are nearby and have less than full health";
	@Comment public static final String inWorldHUDActivationComment7 = "LOOKING_AT - Only the currently targeted entity";
	@Comment public static final String inWorldHUDActivationComment8 = "LOOKING_AT_AND_DAMAGED - Only the currently targeted entity, if it has less than full health";
	@Comment public static final String inWorldHUDActivationComment9 = "LOOKING_AT_NEARBY_AND_DAMAGED - Only the currently targeted entity if it is nearby and has less than full health";
	@Comment public static final String inWorldHUDActivationComment10 = "NOT_LOOKING_AT - Only when the entity isn't the one being rendered for the on-screen HUD";
	@Comment public static final String inWorldHUDActivationComment11 = "NOT_LOOKING_AT_AND_DAMAGED - Only if not the currently targeted entity, if it has less than full health";
	@Comment public static final String inWorldHUDActivationComment12 = "NOT_LOOKING_AT_NEARBY_AND_DAMAGED - Only if not the currently targeted entity, it is nearby and has less than full health";
	@Entry(name = "In-World HUD Activation")
	public static TESHUDActivation inWorldHUDActivation = TESHUDActivation.DAMAGED_AND_NEARBY;

	@Comment public static final String inWorldHudOpacityComment = "How opaque the TES in-world entity HUD should be.";
	@Entry(name = "In-World HUD Opacity", isSlider = true, min = 0f, max = 1f)
	public static float inWorldHudOpacity = 1f;

	@Comment public static final String inWorldBarsRenderTypeComment = "Select the bar render type for the in-game TES entity status HUD";
	@Comment public static final String inWorldBarsRenderTypeComment2 = "Options:";
	@Comment public static final String inWorldBarsRenderTypeComment3 = "NUMERIC - Use numeric values only";
	@Comment public static final String inWorldBarsRenderTypeComment4 = "BAR - Use a health-bar style render";
	@Comment public static final String inWorldBarsRenderTypeComment5 = "COMBINED - Use a health-bar style render with numeric values overlaid";
	@Entry(name = "In-World HUD Bars Render Type")
	public static TESHud.BarRenderType inWorldBarsRenderType = TESHud.BarRenderType.BAR;

	@Comment public static final String inWorldHudHealthFontStyleComment = "What style TES font should render in for entity health in the in-world HUD (if applicable)";
	@Comment public static final String inWorldHudHealthFontStyleComment2 = "Options:";
	@Comment public static final String inWorldHudHealthFontStyleComment3 = "NORMAL - Only the text with no additional stylings";
	@Comment public static final String inWorldHudHealthFontStyleComment4 = "DROP_SHADOW - Text with a bottom-right offset shadow";
	@Comment public static final String inWorldHudHealthFontStyleComment5 = "GLOWING - Text with a large high-contrast outline to emphasize the text.";
	@Comment public static final String inWorldHudHealthFontStyleComment6 = "OUTLINED - Text with a thin dark outline to emphasize the text. Selected by default.";
	@Entry(name = "In-World HUD Health Font Style")
	public static TESClientUtil.TextRenderType inWorldHudHealthFontStyle = TESClientUtil.TextRenderType.NORMAL;

	@Comment public static final String inWorldBarsLengthComment = "Set how long the TES in-world entity status bars should be";
	@Entry(name = "In-World Bars Length", min = 10, max = Integer.MAX_VALUE)
	public static int inWorldBarsLength = 50;

	@Comment public static final String inWorldBarsSegmentsComment = "Whether the in-world entity status bars should be segmented";
	@Entry(name = "In-World Bars Segments")
	public static boolean inWorldBarsSegments = true;

	@Comment public static final String inWorldHudEntityNameComment = "Whether the in-world entity status HUD should render the entity's name";
	@Entry(name = "In-World HUD Entity Name")
	public static boolean inWorldHudEntityName = false;

	@Comment public static final String inWorldHudEntityNameFontStyleComment = "What style TES font should render in for entity names in the in-world HUD";
	@Comment public static final String inWorldHudEntityNameFontStyleComment2 = "Options:";
	@Comment public static final String inWorldHudEntityNameFontStyleComment3 = "NORMAL - Only the text with no additional stylings";
	@Comment public static final String inWorldHudEntityNameFontStyleComment4 = "DROP_SHADOW - Text with a bottom-right offset shadow";
	@Comment public static final String inWorldHudEntityNameFontStyleComment5 = "GLOWING - Text with a large high-contrast outline to emphasize the text.";
	@Comment public static final String inWorldHudEntityNameFontStyleComment6 = "OUTLINED - Text with a thin dark outline to emphasize the text. Selected by default.";
	@Entry(name = "In-World HUD Entity Name Font Style")
	public static TESClientUtil.TextRenderType inWorldHudEntityNameFontStyle = TESClientUtil.TextRenderType.DROP_SHADOW;

	@Comment public static final String inWorldHudStatsComment = "Whether the in-world entity status HUD should render the entity's stats values";
	@Entry(name = "In-World HUD Stats")
	public static boolean inWorldHudStats = false;

	@Comment public static final String inWorldHudStatsFontStyleComment = "What style TES font should render in for entity stats values in the in-world HUD";
	@Comment public static final String inWorldHudStatsFontStyleComment2 = "Options:";
	@Comment public static final String inWorldHudStatsFontStyleComment3 = "NORMAL - Only the text with no additional stylings";
	@Comment public static final String inWorldHudStatsFontStyleComment4 = "DROP_SHADOW - Text with a bottom-right offset shadow";
	@Comment public static final String inWorldHudStatsFontStyleComment5 = "GLOWING - Text with a large high-contrast outline to emphasize the text.";
	@Comment public static final String inWorldHudStatsFontStyleComment6 = "OUTLINED - Text with a thin dark outline to emphasize the text. Selected by default.";
	@Entry(name = "In-World HUD Stats Font Style")
	public static TESClientUtil.TextRenderType inWorldHudStatsFontStyle = TESClientUtil.TextRenderType.DROP_SHADOW;

	@Comment public static final String inWorldHudEntityIconsComment = "Whether the in-world entity status HUD should render the entity's alignment icons";
	@Entry(name = "In-World HUD Icons")
	public static boolean inWorldHudEntityIcons = false;

	@Comment public static final String inWorldHudPotionIconsComment = "Whether the in-world entity status HUD should render the entity's effects icons";
	@Entry(name = "In-World HUD Effects Icons")
	public static boolean inWorldHudPotionIcons = false;

	@Comment public static final String inWorldHudHorseStatsComment = "Whether the in-world TES entity status HUD should include horse stats";
	@Entry(name = "In-World HUD Horse Stats")
	public static boolean inWorldHudHorseStats = false;

	@Comment public static final String inWorldHudNameOverrideComment = "Whether the in-world TES entity status HUD should override vanilla name rendering";
	@Entry(name = "In-World HUD Name Override")
	public static boolean inWorldHudNameOverride = true;

	@Comment public static final String inWorldHudManualVerticalOffsetComment = "Set a manual vertical offset for the TES in-world HUD (in blocks) in the event of other mods doing overhead rendering";
	@Entry(name = "In-World HUD Manual Vertical Offset", min = Float.MIN_VALUE, max = Float.MAX_VALUE)
	public static float inWorldHudManualVerticalOffset = 0;

	// Particles //

	@Comment public static final String particlesEnabledComment = "Whether TES should do particles for various status changes such as damage dealt or health healed";
	@Entry(name = "Particles Enabled")
	public static boolean particlesEnabled = true;

	@Comment public static final String particleFontStyleComment = "What style TES particles' font should render in";
	@Comment public static final String particleFontStyleComment2 = "Options:";
	@Comment public static final String particleFontStyleComment3 = "NORMAL - Only the text with no additional stylings";
	@Comment public static final String particleFontStyleComment4 = "DROP_SHADOW - Text with a bottom-right offset shadow";
	@Comment public static final String particleFontStyleComment5 = "GLOWING - Text with a large high-contrast outline to emphasize the text.";
	@Comment public static final String particleFontStyleComment6 = "OUTLINED - Text with a thin dark outline to emphasize the text. Selected by default.";
	@Entry(name = "Particle Font Style")
	public static TESClientUtil.TextRenderType particleFontStyle = TESClientUtil.TextRenderType.OUTLINED;

	@Comment public static final String defaultParticleLifespanComment = "How long (in ticks) TES particles should display for";
	@Entry(name = "Default Particles Lifespan")
	public static int defaultParticleLifespan = 60;

	@Comment public static final String particleDecimalPointsComment = "How many decimals the numeric TES Particles should round to";
	@Comment public static final String particleDecimalPointsComment2 = "Or set to 0 to only use whole-numbers";
	@Entry(name = "Particle Decimal Points", isSlider = true, min = 0, max = 8)
	public static int particleDecimalPoints = 1;

	@Comment public static final String particleScaleComment = "Scale modifier for TES-Particles. The higher the value, the larger the particles";
	@Entry(name = "Particle Scale", isSlider = true, min = 0f, max = 10f)
	public static float particleScale = 1f;

	@Comment public static final String verbalHealthParticlesComment = "Whether TES should do verbal health-status particles (E.G. INSTAKILL) in certain situations";
	@Entry(name = "Verbal Health Particles")
	public static boolean verbalHealthParticles = true;

	@Comment public static final String damageParticleColourComment = "What colour the damage-type particles should be. Value can be a packed int, byte, or hex value. Format is ARGB";
	@Entry(name = "Damage-Particle Colour")
	public static int damageParticleColour = 0xFFFF0000;

	@Comment public static final String healParticleColourComment = "What colour the healing-type particles should be. Value can be a packed int, byte, or hex value. Format is ARGB";
	@Entry(name = "Heal-Particle Colour")
	public static int healParticleColour = 0xFF00FF00;

	@Comment public static final String teamBasedDamageParticleColoursComment = "Whether TES should change the colour of damage particles to the colour of the team that dealt the damage (if applicable)";
	@Entry(name = "Team Colour Damage Particles")
	public static boolean teamBasedDamageParticleColours = false;

	@Override
	public double getEntityTrackingDistance() {
		return entityTrackingDistance;
	}

	@Override
	public int getCacheCleanFrequency() {
		return cacheCleanFrequency;
	}

	@Override
	public boolean hudEnabled() {
		return hudEnabled;
	}

	@Override
	public TESHudPosition hudRenderPosition() {
		return hudRenderPosition;
	}

	@Override
	public int hudPositionLeftAdjust() {
		return hudLeftOffset;
	}

	@Override
	public int hudPositionTopAdjust() {
		return hudTopOffset;
	}

	@Override
	public double getHudTargetDistance() {
		return hudTargetDistance;
	}

	@Override
	public int hudTargetGracePeriod() {
		return hudTargetGracePeriod;
	}

	@Override
	public boolean hudEntityRender() {
		return hudEntityRender;
	}

	@Override
	public boolean hudPreventEntityOverflow() {
		return hudPreventEntityOverflow;
	}

	@Override
	public boolean hudEntityDamageOverlay() {
		return hudEntityDamageTint;
	}

	@Override
	public boolean hudEntityName() {
		return hudEntityName;
	}

	@Override
	public TESClientUtil.TextRenderType hudEntityNameFontStyle() {
		return hudEntityNameFontStyle;
	}

	@Override
	public boolean hudBossesEnabled() {
		return hudBossesEnabled;
	}

	@Override
	public boolean hudStats() {
		return hudStats;
	}

	@Override
	public TESClientUtil.TextRenderType hudStatsFontStyle() {
		return hudStatsFontStyle;
	}

	@Override
	public boolean hudEntityIcons() {
		return hudEntityIcons;
	}

	@Override
	public boolean hudPotionIcons() {
		return hudPotionIcons;
	}

	@Override
	public boolean hudHorseStats() {
		return hudHorseStats;
	}

	@Override
	public float hudOpacity() {
		return hudOpacity;
	}

	@Override
	public float hudBarFontBackingOpacity() {
		return hudBarFontBackingOpacity;
	}

	@Override
	public TESHud.BarRenderType hudHealthRenderType() {
		return hudBarRenderType;
	}

	@Override
	public TESClientUtil.TextRenderType hudHealthFontStyle() {
		return hudHealthFontStyle;
	}

	@Override
	public boolean hudHealthBarSegments() {
		return hudHealthBarSegments;
	}

	@Override
	public int hudHealthBarLength() {
		return hudHealthBarLength;
	}

	@Override
	public boolean inWorldBarsEnabled() {
		return inWorldHudEnabled;
	}

	@Override
	public boolean inWorldHudForSelf() {
		return inWorldHudForSelf;
	}

	@Override
	public TESHUDActivation inWorldHUDActivation() {
		return inWorldHUDActivation;
	}

	@Override
	public float inWorldHudOpacity() {
		return inWorldHudOpacity;
	}

	@Override
	public TESHud.BarRenderType inWorldBarsRenderType() {
		return inWorldBarsRenderType;
	}

	@Override
	public TESClientUtil.TextRenderType inWorldHudHealthFontStyle() {
		return inWorldHudHealthFontStyle;
	}

	@Override
	public int inWorldBarsLength() {
		return inWorldBarsLength;
	}

	@Override
	public boolean inWorldBarsSegments() {
		return inWorldBarsSegments;
	}

	@Override
	public boolean inWorldHudEntityName() {
		return inWorldHudEntityName;
	}

	@Override
	public TESClientUtil.TextRenderType inWorldHudEntityNameFontStyle() {
		return inWorldHudEntityNameFontStyle;
	}

	@Override
	public boolean inWorldHudStats() {
		return inWorldHudStats;
	}

	@Override
	public TESClientUtil.TextRenderType inWorldHudStatsFontStyle() {
		return inWorldHudStatsFontStyle;
	}

	@Override
	public boolean inWorldHudEntityIcons() {
		return inWorldHudEntityIcons;
	}

	@Override
	public boolean inWorldHudPotionIcons() {
		return inWorldHudPotionIcons;
	}

	@Override
	public boolean inWorldHudHorseStats() {
		return inWorldHudHorseStats;
	}

	@Override
	public boolean inWorldHudNameOverride() {
		return inWorldHudNameOverride;
	}

	@Override
	public float inWorldHudManualVerticalOffset() {
		return inWorldHudManualVerticalOffset;
	}

	@Override
	public boolean particlesEnabled() {
		return particlesEnabled;
	}

	@Override
	public TESClientUtil.TextRenderType particleFontStyle() {
		return particleFontStyle;
	}

	@Override
	public int defaultParticleLifespan() {
		return defaultParticleLifespan;
	}

	@Override
	public int particleDecimalPoints() {
		return particleDecimalPoints;
	}

	@Override
	public float getParticleScale() {
		return particleScale;
	}

	@Override
	public boolean verbalHealthParticles() {
		return verbalHealthParticles;
	}

	@Override
	public int getDamageParticleColour() {
		return damageParticleColour;
	}

	@Override
	public int getHealParticleColour() {
		return healParticleColour;
	}

	@Override
	public boolean teamBasedDamageParticleColours() {
		return teamBasedDamageParticleColours;
	}
}

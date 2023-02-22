package net.tslat.tes.config;

import eu.midnightdust.lib.config.MidnightConfig;
import net.tslat.tes.api.TESHUDActivation;
import net.tslat.tes.core.hud.TESHud;

public final class TESConfig extends MidnightConfig implements net.tslat.tes.api.TESConfig {
	@Comment
	private static final String entityTrackingDistanceComment = "How close (in blocks) entities should be before TES starts tracking them";
	@Entry(name = "Entity Tracking Distance", isSlider = true, min = 8d, max = 512d)
	private static double entityTrackingDistance = 64;

	@Comment private static final String cacheCleanFrequencyComment = "How frequently TES should clear out its tracking cache. Generally this should stay at default, but if you are noticing issues you can try adjusting it";
	@Entry(name = "Cache Clean Frequency", min = 20, max = Integer.MAX_VALUE)
	private static int cacheCleanFrequency = 400;

	@Comment private static final String hudEnabledComment = "Whether the TES HUD should be enabled or not";
	@Entry(name = "HUD Enabled")
	private static boolean hudEnabled = true;

	@Comment private static final String hudTargetDistanceComment = "How close (in blocks) the player has to be to render a HUD for an entity under the crosshairs";
	@Comment private static final String hudTargetDistanceComment2 = "Larger values may cost more performance";
	@Entry(name = "HUD Target Distance", isSlider = true, min = 4d, max = 256d)
	private static double hudTargetDistance = 64d;

	@Comment private static final String hudTargetGracePeriodComment = "How long (in ticks) after looking away from an entity before its HUD should stop rendering";
	@Entry(name = "HUD Target Grace Period", min = 0, max = Integer.MAX_VALUE)
	private static int hudTargetGracePeriod = 10;

	@Comment private static final String hudEntityRenderComment = "Whether the TES HUD should render the entity's image";
	@Entry(name = "HUD Entity Render")
	private static boolean hudEntityRender = true;

	@Comment private static final String hudHealthRenderTypeComment = "Select the health render type for the TES HUD";
	@Comment private static final String hudHealthRenderTypeComment2 = "Options:";
	@Comment private static final String hudHealthRenderTypeComment3 = "NUMERIC - Use numeric values for health only";
	@Comment private static final String hudHealthRenderTypeComment4 = "BAR - Use a health-bar style render";
	@Comment private static final String hudHealthRenderTypeComment5 = "COMBINED - Use a health-bar style render with numeric values overlaid";
	@Entry(name = "HUD Health Render Type")
	private static TESHud.BarRenderType hudBarRenderType = TESHud.BarRenderType.COMBINED;

	@Comment private static final String hudHealthBarSegmentsComment = "Set whether the TES HUD health bar should render bar-segments";
	@Entry(name = "HUD Health Bar Segments")
	private static boolean hudHealthBarSegments = true;

	@Comment private static final String hudHealthBarLengthComment = "Set how long the TES HUD health bar should be";
	@Entry(name = "HUD Health Bar Length", min = 10, max = Integer.MAX_VALUE)
	private static int hudHealthBarLength = 100;

	@Comment private static final String hudEntityDamageTintComment = "Set whether the TES HUD's entity icon should keep the red 'tint' entities get when the real entity takes damage or not";
	@Entry(name = "HUD Entity Damage Tint")
	private static boolean hudEntityDamageTint = false;

	@Comment private static final String hudEntityNameComment = "Whether the TES HUD should render the entity's name";
	@Entry(name = "HUD Entity Name")
	private static boolean hudEntityName = true;

	@Comment private static final String hudArmourComment = "Whether the TES HUD should render the entity's armour and toughness";
	@Entry(name = "HUD Entity Armour")
	private static boolean hudArmour = true;

	@Comment private static final String hudEntityIconsComment = "Whether the TES HUD should render the entity's alignment icons";
	@Entry(name = "HUD Entity Icons")
	private static boolean hudEntityIcons = true;

	@Comment private static final String hudPotionIconsComment = "Whether the TES HUD should render the entity's effect icons";
	@Entry(name = "HUD Entity Effects Icons")
	private static boolean hudPotionIcons = true;

	@Comment private static final String hudOpacityComment = "Set how opaque the TES HUD should be, overall. The lower the value, the more transparent the HUD will be";
	@Entry(name = "HUD Opacity", isSlider = true, min = 0f, max = 1f)
	private static float hudOpacity = 1f;

	// In-world HUD //

	@Comment private static final String inWorldHudEnabledComment = "Whether TES should do in-world entity status bars";
	@Entry(name = "In-World HUD Enabled")
	private static boolean inWorldHudEnabled = true;

	@Comment private static final String inWorldHUDActivationComment = "When the TES in-world status bars should render";
	@Comment private static final String inWorldHUDActivationComment2 = "Options:";
	@Comment private static final String inWorldHUDActivationComment3 = "ALWAYS - Any entity currently visible";
	@Comment private static final String inWorldHUDActivationComment4 = "NEARBY_ONLY - Only entities nearby";
	@Comment private static final String inWorldHUDActivationComment5 = "DAMAGED_ONLY - Only entities that have less than full health";
	@Comment private static final String inWorldHUDActivationComment6 = "DAMAGED_AND_NEARBY - Only entities that are nearby and have less than full health";
	@Comment private static final String inWorldHUDActivationComment7 = "LOOKING_AT - Only the currently targeted entity";
	@Comment private static final String inWorldHUDActivationComment8 = "LOOKING_AT_AND_DAMAGED - Only the currently targeted entity, if it has less than full health";
	@Comment private static final String inWorldHUDActivationComment9 = "LOOKING_AT_NEARBY_AND_DAMAGED - Only the currently targeted entity if it is nearby and has less than full health";
	@Comment private static final String inWorldHUDActivationComment10 = "NOT_LOOKING_AT - Only when the entity isn't the one being rendered for the on-screen HUD";
	@Comment private static final String inWorldHUDActivationComment11 = "NOT_LOOKING_AT_AND_DAMAGED - Only if not the currently targeted entity, if it has less than full health";
	@Comment private static final String inWorldHUDActivationComment12 = "NOT_LOOKING_AT_NEARBY_AND_DAMAGED - Only if not the currently targeted entity, it is nearby and has less than full health";
	@Entry(name = "In-World HUD Activation")
	private static TESHUDActivation inWorldHUDActivation = TESHUDActivation.DAMAGED_AND_NEARBY;

	@Comment private static final String inWorldHudOpacityComment = "How opaque the TES in-world entity HUD should be.";
	@Entry(name = "In-World HUD Opacity", isSlider = true, min = 0f, max = 1f)
	private static float inWorldHudOpacity = 1f;

	@Comment private static final String inWorldBarsRenderTypeComment = "Select the bar render type for the in-game TES entity status HUD";
	@Comment private static final String inWorldBarsRenderTypeComment2 = "Options:";
	@Comment private static final String inWorldBarsRenderTypeComment3 = "NUMERIC - Use numeric values only";
	@Comment private static final String inWorldBarsRenderTypeComment4 = "BAR - Use a health-bar style render";
	@Comment private static final String inWorldBarsRenderTypeComment5 = "COMBINED - Use a health-bar style render with numeric values overlaid";
	@Entry(name = "In-World HUD Bars Render Type")
	private static TESHud.BarRenderType inWorldBarsRenderType = TESHud.BarRenderType.BAR;

	@Comment private static final String inWorldBarsLengthComment = "Set how long the TES in-world entity status bars should be";
	@Entry(name = "In-World Bars Length", min = 10, max = Integer.MAX_VALUE)
	private static int inWorldBarsLength = 50;

	@Comment private static final String inWorldBarsSegmentsComment = "Whether the in-world entity status bars should be segmented";
	@Entry(name = "In-World Bars Segments")
	private static boolean inWorldBarsSegments = true;

	@Comment private static final String inWorldHudEntityNameComment = "Whether the in-world entity status HUD should render the entity's name";
	@Entry(name = "In-World HUD Entity Name")
	private static boolean inWorldHudEntityName = false;

	@Comment private static final String inWorldHudArmourComment = "Whether the in-world entity status HUD should render the entity's armour values";
	@Entry(name = "In-World HUD Armour")
	private static boolean inWorldHudArmour = false;

	@Comment private static final String inWorldHudEntityIconsComment = "Whether the in-world entity status HUD should render the entity's alignment icons";
	@Entry(name = "In-World HUD Icons")
	private static boolean inWorldHudEntityIcons = false;

	@Comment private static final String inWorldHudPotionIconsComment = "Whether the in-world entity status HUD should render the entity's effects icons";
	@Entry(name = "In-World HUD Effects Icons")
	private static boolean inWorldHudPotionIcons = false;

	// Particles //

	@Comment private static final String particlesEnabledComment = "Whether TES should do particles for various status changes such as damage dealt or health healed";
	@Entry(name = "Particles Enabled")
	private static boolean particlesEnabled = true;

	@Comment private static final String particleDecimalPointsComment = "How many decimals the numeric TES Particles should round to";
	@Comment private static final String particleDecimalPointsComment2 = "Or set to 0 to only use whole-numbers";
	@Entry(name = "Particle Decimal Points", isSlider = true, min = 0, max = 8)
	private static int particleDecimalPoints = 1;

	@Comment private static final String particleScaleComment = "Scale modifier for TES-Particles. The higher the value, the larger the particles";
	@Entry(name = "Particle Scale", isSlider = true, min = 0f, max = 10f)
	private static float particleScale = 1f;

	@Comment private static final String verbalHealthParticlesComment = "Whether TES should do verbal health-status particles (E.G. INSTAKILL) in certain situations";
	@Entry(name = "Verbal Health Particles")
	private static boolean verbalHealthParticles = true;

	@Comment private static final String damageParticleColourComment = "What colour the damage-type particles should be. Value can be a packed int, byte, or hex value. Format is ARGB";
	@Entry(name = "Damage-Particle Colour")
	private static int damageParticleColour = 0xFFFF0000;

	@Comment private static final String healParticleColourComment = "What colour the healing-type particles should be. Value can be a packed int, byte, or hex value. Format is ARGB";
	@Entry(name = "Heal-Particle Colour")
	private static int healParticleColour = 0xFF00FF00;

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
	public boolean hudEntityDamageOverlay() {
		return hudEntityDamageTint;
	}

	@Override
	public boolean hudEntityName() {
		return hudEntityName;
	}

	@Override
	public boolean hudArmour() {
		return hudArmour;
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
	public float hudOpacity() {
		return hudOpacity;
	}

	@Override
	public TESHud.BarRenderType hudHealthRenderType() {
		return hudBarRenderType;
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
	public boolean inWorldHudArmour() {
		return inWorldHudArmour;
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
	public boolean particlesEnabled() {
		return particlesEnabled;
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
}

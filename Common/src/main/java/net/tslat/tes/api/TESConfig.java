package net.tslat.tes.api;

import net.tslat.tes.api.util.TESClientUtil;
import net.tslat.tes.core.hud.TESHud;
import net.tslat.tes.core.hud.TESHudPosition;

/**
 * Common configuration interface for TES' configurable settings.
 * Use this interface where possible for config access as it is natively cross-platform compatible and ensures correct value retrieval
 */
public interface TESConfig {
	/**
	 * Gets the distance (in blocks) that entities must be within for TES to track them
	 */
	double getEntityTrackingDistance();

	/**
	 * Gets the tick-value for how frequently TES should clean up its {@link net.tslat.tes.core.state.TESEntityTracking#ENTITY_STATES state cache}
	 */
	int getCacheCleanFrequency();

	/**
	 * Whether the TES HUD should be rendered or not
	 */
	boolean hudEnabled();

	/**
	 * What position the TES HUD should render in
	 */
	TESHudPosition hudRenderPosition();

	/**
	 * Manually adjust the left-offset rendering position of the TES HUD
	 */
	int hudPositionLeftAdjust();

	/**
	 * Manually adjust the top-offset rendering position of the TES HUD
	 */
	int hudPositionTopAdjust();

	/**
	 * How far away a targeted entity can be before TES stops rendering a HUD for it
	 */
	double getHudTargetDistance();

	/**
	 * How long (in ticks) after looking away from an entity before its HUD should stop rendering
	 */
	int hudTargetGracePeriod();

	/**
	 * Whether the TES HUD should render the entity's image
	 */
	boolean hudEntityRender();

	/**
	 * Whether the TES HUD should cull any overflow for entities that don't scale properly to their rendering frame
	 */
	boolean hudPreventEntityOverflow();

	/**
	 * Whether the entity rendered in the TES HUD should keep the red overlay when the real entity takes damage
	 */
	boolean hudEntityDamageOverlay();

	/**
	 * Whether the TES HUD Should render the entity's name
	 */
	boolean hudEntityName();

	/**
	 * Whether the TES HUD should render the entity's mod ID under its name
	 */
	boolean hudEntityNamespace();

	/**
	 * What style TES font should render in for entity names in the HUD
	 */
	TESClientUtil.TextRenderType hudEntityNameFontStyle();

	/**
	 * Whether the TES HUD Should render for boss-type entities
	 */
	boolean hudBossesEnabled();

	/**
	 * Whether the TES HUD should render the entity's armour and toughness
	 */
	boolean hudArmour();

	/**
	 * What style TES font should render in for entity armour values in the HUD
	 */
	TESClientUtil.TextRenderType hudArmourFontStyle();

	/**
	 * Whether the TES HUD should render the entity's alignment icons
	 */
	boolean hudEntityIcons();

	/**
	 * Whether the TES HUD should render the entity's potion icons
	 */
	boolean hudPotionIcons();

	/**
	 * Whether the TES HUD should render horses' stats
	 */
	boolean hudHorseStats();

	/**
	 * Get the rendered opacity of the TES HUD.<br>
	 * This affects the entire HUD.<br>
	 * Value range is 0->1, with 1 being 100% opacity
	 */
	float hudOpacity();

	/**
	 * Set how opaque the background behind the text on TES bars, if a render type is set that renders text
	 */
	float hudBarFontBackingOpacity();

	/**
	 * Get the health render type for the TES HUD.<br>
	 * Available options are:
	 * <ul>
	 *     <li>NUMERIC - Use numeric values for health only</li>
	 *     <li>BAR - Use a health-bar style render</li>
	 *     <li>COMBINED - Use a health-bar style render with numeric values overlaid</li>
	 * </ul>
	 */
	TESHud.BarRenderType hudHealthRenderType();

	/**
	 * What style TES font should render in for entity health in the HUD (if applicable)
	 */
	TESClientUtil.TextRenderType hudHealthFontStyle();

	/**
	 * Whether the health bar in the TES HUD should render with health bar segments
	 */
	boolean hudHealthBarSegments();

	/**
	 * How long the TES HUD health bar should be (in pixels)
	 */
	int hudHealthBarLength();

	/**
	 * Whether the TES in-world entity status GUIs are enabled
	 */
	boolean inWorldBarsEnabled();

	/**
	 * Whether the TES HUD should be rendered for the player or not
	 */
	boolean inWorldHudForSelf();

	/**
	 * When the TES in-world entity status bars should render
	 */
	TESHUDActivation inWorldHUDActivation();

	/**
	 * How opaque the in-world TES entity status HUD should be
	 */
	float inWorldHudOpacity();

	/**
	 * Get the bar render type for the in-world TES entity status HUD.<br>
	 * Available options are:
	 * <ul>
	 *     <li>NUMERIC - Use numeric values only</li>
	 *     <li>BAR - Use a health-bar style render</li>
	 *     <li>COMBINED - Use a health-bar style render with numeric values overlaid</li>
	 * </ul>
	 */
	TESHud.BarRenderType inWorldBarsRenderType();

	/**
	 * What style TES font should render in for entity health in the in-world HUD (if applicable)
	 */
	TESClientUtil.TextRenderType inWorldHudHealthFontStyle();

	/**
	 * How long the TES in-world entity status health bars should be (in pixels)
	 */
	int inWorldBarsLength();

	/**
	 * Whether the in-world TES entity status HUD's bars should be segmented
	 */
	boolean inWorldBarsSegments();

	/**
	 * Whether the in-world TES entity status HUD should include the entity's name
	 */
	boolean inWorldHudEntityName();

	/**
	 * Whether the in-world entity status HUD should render the entity's mod ID under its name
	 */
	boolean inWorldHudEntityNamespace();

	/**
	 * Whether the in-world entity status HUD should render if the entity is a boss (they usually have their own boss bars)
	 */
	boolean inWorldHudBossesEnabled();

	/**
	 * What style TES font should render in for entity names in the in-world HUD
	 */
	TESClientUtil.TextRenderType inWorldHudEntityNameFontStyle();

	/**
	 * Whether the in-world TES entity status HUD should include the entity's armour values
	 */
	boolean inWorldHudArmour();

	/**
	 * What style TES font should render in for entity armour values in the in-world HUD
	 */
	TESClientUtil.TextRenderType inWorldHudArmourFontStyle();

	/**
	 * Whether the in-world TES entity status HUD should include the entity's alignment icons
	 */
	boolean inWorldHudEntityIcons();

	/**
	 * Whether the in-world TES entity status HUD should include the entity's effect icons
	 */
	boolean inWorldHudPotionIcons();

	/**
	 * Whether the in-world TES entity status HUD should include horse stats
	 */
	boolean inWorldHudHorseStats();

	/**
	 * Whether the in-world TES entity status HUD should override vanilla name rendering
	 */
	boolean inWorldHudNameOverride();

	/**
	 * A manual vertical offset for the TES in-world HUD (in blocks)
	 */
	float inWorldHudManualVerticalOffset();

	/**
	 * Whether TES should do particles for various status changes such as damage dealt or health healed
	 */
	boolean particlesEnabled();

	/**
	 * What style TES particles' font should render in
	 */
	TESClientUtil.TextRenderType particleFontStyle();

	/**
	 * How long (in ticks) particles should survive for by default
	 */
	int defaultParticleLifespan();

	/**
	 * How many decimals the numeric TES Particles should round to
	 */
	int particleDecimalPoints();

	/**
	 * Get the scale modifier for TES Particle rendering.<br>
	 * A value of 2 is double the standard rendering size.
	 */
	float getParticleScale();

	/**
	 * Gets whether the verbal health particles are enabled
	 */
	boolean verbalHealthParticles();

	/**
	 * Get the packed-int colour value of the damage-type TES particle.<br>
	 * Format is ARGB
	 */
	int getDamageParticleColour();

	/**
	 * Get the packed-int colour value of the healing-type TES particle<br>
	 * Format is ARGB
	 */
	int getHealParticleColour();

	/**
	 * Whether TES should colour its damage particles based on the team that dealt the damage
	 */
	boolean teamBasedDamageParticleColours();

	/**
	 * Whether TES currently has mob effect syncing active
	 */
	default boolean isSyncingEffects() {
		return hudPotionIcons() || inWorldHudPotionIcons();
	}
}
package net.tslat.tes.api;

import net.minecraft.resources.ResourceLocation;

/**
 * "Entity type" enum for TES to differentiate entities for handling
 */
public interface TESEntityType {
	TESEntityType PASSIVE = new Impl(TESConstants.id("bar/passive_background"), TESConstants.id("bar/passive_fill"));
	TESEntityType NEUTRAL = new Impl(TESConstants.id("bar/neutral_background"), TESConstants.id("bar/neutral_fill"));
	TESEntityType HOSTILE = new Impl(TESConstants.id("bar/hostile_background"), TESConstants.id("bar/hostile_fill"));
	TESEntityType BOSS = new Impl(TESConstants.id("bar/boss_background"), TESConstants.id("bar/boss_fill"));
	TESEntityType PLAYER = new Impl(TESConstants.id("bar/player_background"), TESConstants.id("bar/player_fill"));

	/**
	 * @return The texture location of the background (empty) texture for this bar type
	 */
	ResourceLocation backgroundTexture();

	/**
	 * @return The texture location of the foreground/progress (filled) texture for this bar type
	 */
	ResourceLocation overlayTexture();

	record Impl(ResourceLocation backgroundTexture, ResourceLocation overlayTexture) implements TESEntityType {}
}

package net.tslat.tslatentitystatus.api.common.constant;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.tslat.tslatentitystatus.api.TESConstants;
import net.tslat.tslatentitystatus.core.platform.TESPlatform;
import org.jetbrains.annotations.ApiStatus;

import java.util.IdentityHashMap;
import java.util.Map;

// TODO switch to dynamic bar colours
// TODO Clear Relations cache on world exit/load
/// TES enum to differentiate entities by their relationship to the player
public interface TESEntityRelation {
	/// @see TESPlatform#getTESEntityRelation(LivingEntity)
	Map<Class<? extends LivingEntity>, TESEntityRelation> RELATIONS = new IdentityHashMap<>(30);

	TESEntityRelation PASSIVE = new Impl(TESConstants.id("bar/passive_background"), TESConstants.id("bar/passive_fill"));
	TESEntityRelation NEUTRAL = new Impl(TESConstants.id("bar/neutral_background"), TESConstants.id("bar/neutral_fill"));
	TESEntityRelation HOSTILE = new Impl(TESConstants.id("bar/hostile_background"), TESConstants.id("bar/hostile_fill"));
	TESEntityRelation BOSS = new Impl(TESConstants.id("bar/boss_background"), TESConstants.id("bar/boss_fill"));
	TESEntityRelation PLAYER = new Impl(TESConstants.id("bar/player_background"), TESConstants.id("bar/player_fill"));

	/// @return The texture location of the background (empty) texture for this bar type
	Identifier backgroundTexture();

	/// @return The texture location of the foreground/progress (filled) texture for this bar type
	Identifier overlayTexture();
	
	/// Quick internal class for holding built-in implementations of [TESEntityRelation]
	@ApiStatus.Internal
	record Impl(Identifier backgroundTexture, Identifier overlayTexture) implements TESEntityRelation {}
}

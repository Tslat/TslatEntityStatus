package net.tslat.tslatentitystatus.api.common.util;

// TODO Clear mobs cache on world exit/load

import it.unimi.dsi.fastutil.objects.Reference2BooleanMap;
import it.unimi.dsi.fastutil.objects.Reference2BooleanOpenHashMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.breeze.Breeze;
import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;
import net.minecraft.world.entity.monster.spider.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.tslat.tslatentitystatus.api.TESConstants;
import net.tslat.tslatentitystatus.api.common.constant.TESEntityRelation;

/// Common-code static helper class for various `TES`-related functionalities
public final class TESUtil {
	private static final Reference2BooleanMap<Class<? extends LivingEntity>> MELEE_MOBS = new Reference2BooleanOpenHashMap<>();
	private static final Reference2BooleanMap<Class<? extends LivingEntity>> RANGED_MOBS = new Reference2BooleanOpenHashMap<>();

	/// Round the provided number to the nearest decimal place, trimming trailing zeroes as needed
	///
	/// @param value The value to round
	/// @param decimals The number of decimals to round the value to at minimum
	/// @return The rounded value, as a String
	public static String roundToDecimal(double value, int decimals) {
		float val = Math.round(value * (float)Math.pow(10, decimals)) / (float)Math.pow(10, decimals);

		if (((int)val) == val)
			return String.valueOf((int)val);

		return String.valueOf(val);
	}

	/// @return The provided entity's health percentage, represented as a fraction of its max health
	public static float getHealthPercent(LivingEntity entity) {
		return getHealth(entity) / getMaxHealth(entity);
	}

	/// @return The provided entity's current health
	public static float getHealth(LivingEntity entity) {
		return entity.getHealth();
	}

	/// @return The provided entity's max health, if it has max health
	public static float getMaxHealth(LivingEntity entity) {
		if (entity.getAttributes().hasAttribute(Attributes.MAX_HEALTH))
			return (float)entity.getAttributeValue(Attributes.MAX_HEALTH);

		return 0;
	}

	/// @return The provided entity's armour value, if it has armour
	public static int getArmour(LivingEntity entity) {
		if (entity.getAttributes().hasAttribute(Attributes.ARMOR))
			return Mth.floor(entity.getAttributeValue(Attributes.ARMOR));

		return 0;
	}

	/// @return The provided entity's [Attributes#ARMOR_TOUGHNESS] value, if it has armour
	public static float getArmourToughness(LivingEntity entity) {
		if (entity.getAttributes().hasAttribute(Attributes.ARMOR_TOUGHNESS))
			return (float)entity.getAttributeValue(Attributes.ARMOR_TOUGHNESS);

		return 0;
	}

	/// @return The provided entity's [Attributes#ATTACK_DAMAGE] value, if it has any
	public static float getMeleeDamage(LivingEntity entity) {
		if (entity.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE))
			return (float)entity.getAttributeValue(Attributes.ATTACK_DAMAGE);

		return 0;
	}

	/// @return `true` if the provided Entity is immune to fire
	public static boolean isFireImmune(Entity entity) {
		return entity.getType().fireImmune();
	}

	/// @return `true` if the provided entity is a melee-attacker
	public static boolean isMeleeMob(LivingEntity entity) {
		if (entity instanceof AbstractSkeleton skeleton)
			return skeleton.getMainHandItem().has(DataComponents.WEAPON);

		return MELEE_MOBS.computeIfAbsent(entity.getClass(), _ -> {
			if (!entity.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE))
				return false;

			if (entity.getAttributeBaseValue(Attributes.ATTACK_DAMAGE) != 2 && !(entity instanceof Breeze))
				return true;

			if (entity instanceof Phantom || entity instanceof EnderDragon || entity instanceof Spider || entity instanceof Slime ||
			    entity instanceof Endermite || entity instanceof Silverfish)
				return true;

			return false;
		});
	}

	/// @return `true` if the provided Entity is a ranged-attacker
	public static boolean isRangedMob(LivingEntity entity) {
		if (entity instanceof AbstractSkeleton skeleton)
			return skeleton.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof BowItem;

		return RANGED_MOBS.computeIfAbsent(entity.getClass(), _ -> {
			if (entity instanceof RangedAttackMob)
				return true;

			if (entity instanceof Blaze || entity instanceof Shulker || entity instanceof Ghast || entity instanceof Guardian ||
				entity instanceof Breeze)
				return true;

			return false;
		});
	}

	/// @return If the entity is functionally visible to the player for logical purposes
	public static boolean isVisibleToPlayer(Entity entity, Player player) {
		return !entity.isInvisibleTo(player) || (TESConstants.PLATFORM.getDamageableEntity(entity) instanceof LivingEntity livingEntity && livingEntity.isCurrentlyGlowing());
	}

	public static boolean shouldTESHandleEntity(Entity entity, Player player) {
		return entity instanceof LivingEntity && !entity.is(TESConstants.NO_TES_HANDLING) && isVisibleToPlayer(entity, player);
	}

	/// Get the relationship type of the provided entity to the player, from the perspective of `TES`
	///
	/// This allows for handling different entities in different ways, particularly for render formatting
	public static TESEntityRelation getTESEntityRelation(LivingEntity entity) {
		return TESConstants.PLATFORM.getTESEntityRelation(entity);
	}
}

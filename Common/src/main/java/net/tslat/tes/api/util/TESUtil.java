package net.tslat.tes.api.util;

import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.SwordItem;
import net.tslat.tes.api.TESAPI;
import net.tslat.tes.api.TESEntityType;

import javax.annotation.Nullable;

/**
 * Helper class for TES.<br>
 * Some methods are handled by subclasses for loader-specific implementations.<br>
 * <br>
 * Loader-specific methods can be accessed through {@link TESAPI#getConfig()}
 */
public interface TESUtil {
	Object2BooleanOpenHashMap<Class<? extends LivingEntity>> meleeMobs = new Object2BooleanOpenHashMap<>();
	Object2BooleanOpenHashMap<Class<? extends LivingEntity>> rangedMobs = new Object2BooleanOpenHashMap<>();

	/**
	 * Round the provided number to the nearest decimal place, trimming trailing zeroes as needed
	 * @param value The value to round
	 * @param decimals The number of decimals to round the value to at minimum
	 * @return The rounded value, as a String
	 */
	static String roundToDecimal(double value, int decimals) {
		float val = Math.round(value * (float)Math.pow(10, decimals)) / (float)Math.pow(10, decimals);

		if (((int)val) == val)
			return String.valueOf((int)val);

		return String.valueOf(val);
	}

	/**
	 * Get the provided entity's health percentage, represented as a fraction of its max health
	 */
	static float getHealthPercent(LivingEntity entity) {
		return entity.getHealth() / entity.getMaxHealth();
	}

	/**
	 * Get the provided entity's armour value, if it has armour
	 */
	static int getArmour(LivingEntity entity) {
		if (entity.getAttributes().hasAttribute(Attributes.ARMOR))
			return (int)Math.floor(entity.getAttributeValue(Attributes.ARMOR));

		return 0;
	}

	/**
	 * Get the provided entity's {@link Attributes#ARMOR_TOUGHNESS armour toughness} value, if it has armour
	 */
	static float getArmourToughness(LivingEntity entity) {
		if (entity.getAttributes().hasAttribute(Attributes.ARMOR_TOUGHNESS))
			return (float)entity.getAttributeValue(Attributes.ARMOR_TOUGHNESS);

		return 0;
	}

	/**
	 * Get whether the provided Entity is immune to fire or not
	 */
	static boolean isFireImmune(Entity entity) {
		return entity.getType().fireImmune();
	}

	/**
	 * Get whether the provided entity is a melee-attacker
	 */
	static boolean isMeleeMob(LivingEntity entity) {
		return isMeleeMobHardcoded(entity);
	}

	/**
	 * Get whether the provided Entity is a ranged-attacker
	 */
	static boolean isRangedMob(LivingEntity entity) {
		return isRangedMobHardcoded(entity);
	}

	static boolean isVisibleToPlayer(@Nullable LivingEntity entity, Player player) {
		return entity != null && (!entity.isInvisibleTo(player) || entity.isCurrentlyGlowing());
	}

	/**
	 * Get a LivingEntity instance from the provided Entity instance, if possible.<br>
	 * Includes handling for part-entities and eliminates invalid entities
	 */
	@Nullable
	LivingEntity getLivingEntityIfPossible(@Nullable Entity entity);

	/**
	 * Get the {@link TESEntityType} of the provided entity
	 */
	TESEntityType getEntityType(LivingEntity entity);

	void clearDynamicCaches();

	// Because vanilla isn't even remotely consistent
	private static boolean isMeleeMobHardcoded(LivingEntity entity) {
		return meleeMobs.computeIfAbsent(entity.getClass(), clazz -> {
			if (!entity.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE))
				return false;

			if (entity.getAttributeBaseValue(Attributes.ATTACK_DAMAGE) != 2)
				return true;

			if (entity instanceof AbstractSkeleton skeleton)
				return skeleton.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof SwordItem;

			if (entity instanceof Phantom || entity instanceof EnderDragon || entity instanceof Spider || entity instanceof Slime ||
			entity instanceof Endermite || entity instanceof Silverfish)
				return true;

			return false;
		});
	}

	// Because vanilla isn't even remotely consistent
	private static boolean isRangedMobHardcoded(LivingEntity entity) {
		return rangedMobs.computeIfAbsent(entity.getClass(), clazz -> {
			if (entity instanceof RangedAttackMob)
				return true;

			if (entity instanceof Blaze || entity instanceof Shulker || entity instanceof Ghast || entity instanceof Guardian)
				return true;

			if (entity instanceof AbstractSkeleton skeleton)
				return skeleton.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof BowItem;

			return false;
		});
	}
}

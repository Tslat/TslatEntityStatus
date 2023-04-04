package net.tslat.tes.api.util;

import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.SwordItem;
import net.minecraft.util.Hand;
import net.minecraftforge.entity.PartEntity;
import net.tslat.tes.api.TESAPI;
import net.tslat.tes.api.TESEntityType;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Helper class for TES.<br>
 * Some methods are handled by subclasses for loader-specific implementations.<br>
 * <br>
 * Loader-specific methods can be accessed through {@link TESAPI#getConfig()}
 */
public class TESUtil {
	private static final Object2BooleanOpenHashMap<Class<? extends LivingEntity>> meleeMobs = new Object2BooleanOpenHashMap<>();
	private static final Object2BooleanOpenHashMap<Class<? extends LivingEntity>> rangedMobs = new Object2BooleanOpenHashMap<>();

	private static final Map<Class<? extends LivingEntity>, TESEntityType> ENTITY_TYPE_MAP = new Object2ObjectOpenHashMap<>();

	/**
	 * Round the provided number to the nearest decimal place, trimming trailing zeroes as needed
	 * @param value The value to round
	 * @param decimals The number of decimals to round the value to at minimum
	 * @return The rounded value, as a String
	 */
	public static String roundToDecimal(double value, int decimals) {
		float val = Math.round(value * (float)Math.pow(10, decimals)) / (float)Math.pow(10, decimals);

		if (((int)val) == val)
			return String.valueOf((int)val);

		return String.valueOf(val);
	}

	/**
	 * Get the provided entity's health percentage, represented as a fraction of its max health
	 */
	public static float getHealthPercent(LivingEntity entity) {
		return entity.getHealth() / entity.getMaxHealth();
	}

	/**
	 * Get the provided entity's armour value, if it has armour
	 */
	public static int getArmour(LivingEntity entity) {
		if (entity.getAttributes().hasAttribute(Attributes.ARMOR))
			return (int)Math.floor(entity.getAttributeValue(Attributes.ARMOR));

		return 0;
	}

	/**
	 * Get the provided entity's {@link Attributes#ARMOR_TOUGHNESS armour toughness} value, if it has armour
	 */
	public static float getArmourToughness(LivingEntity entity) {
		if (entity.getAttributes().hasAttribute(Attributes.ARMOR_TOUGHNESS))
			return (float)entity.getAttributeValue(Attributes.ARMOR_TOUGHNESS);

		return 0;
	}

	/**
	 * Get whether the provided Entity is immune to fire or not
	 */
	public static boolean isFireImmune(Entity entity) {
		return entity.getType().fireImmune();
	}

	/**
	 * Get whether the provided entity is a melee-attacker
	 */
	public static boolean isMeleeMob(LivingEntity entity) {
		return isMeleeMobHardcoded(entity);
	}

	/**
	 * Get whether the provided Entity is a ranged-attacker
	 */
	public static boolean isRangedMob(LivingEntity entity) {
		return isRangedMobHardcoded(entity);
	}

	public static boolean isVisibleToPlayer(@Nullable LivingEntity entity, PlayerEntity player) {
		return entity != null && (!entity.isInvisibleTo(player) || entity.isGlowing());
	}

	/**
	 * Get a LivingEntity instance from the provided Entity instance, if possible.<br>
	 * Includes handling for part-entities and eliminates invalid entities
	 */
	@Nullable
	public static LivingEntity getLivingEntityIfPossible(@Nullable Entity entity) {
		if (entity instanceof LivingEntity) {
			LivingEntity livingEntity = (LivingEntity)entity;

			if (livingEntity instanceof ArmorStandEntity)
				return null;

			return livingEntity;
		}

		if (entity instanceof PartEntity<?> && ((PartEntity)entity).getParent() instanceof LivingEntity)
			return (LivingEntity)((PartEntity)entity).getParent();

		return null;
	}

	/**
	 * Get the {@link TESEntityType} of the provided entity
	 */
	public static TESEntityType getEntityType(LivingEntity entity) {
		return ENTITY_TYPE_MAP.computeIfAbsent(entity.getClass(), clazz -> {
			if (entity.getType() == EntityType.PLAYER)
				return TESEntityType.PLAYER;

			if (!entity.canChangeDimensions())
				return TESEntityType.BOSS;

			if (entity instanceof IAngerable)
				return TESEntityType.NEUTRAL;

			if (entity instanceof MonsterEntity)
				return TESEntityType.HOSTILE;

			return TESEntityType.PASSIVE;
		});
	}

	// Because vanilla isn't even remotely consistent
	public static boolean isMeleeMobHardcoded(LivingEntity entity) {
		return meleeMobs.computeIfAbsent(entity.getClass(), clazz -> {
			if (!entity.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE))
				return false;

			if (entity.getAttributeBaseValue(Attributes.ATTACK_DAMAGE) != 2)
				return true;

			if (entity instanceof AbstractSkeletonEntity)
				return entity.getItemInHand(Hand.MAIN_HAND).getItem() instanceof SwordItem;

			if (entity instanceof PhantomEntity || entity instanceof EnderDragonEntity || entity instanceof SpiderEntity || entity instanceof SlimeEntity ||
					entity instanceof EndermiteEntity || entity instanceof SilverfishEntity)
				return true;

			return false;
		});
	}

	// Because vanilla isn't even remotely consistent
	public static boolean isRangedMobHardcoded(LivingEntity entity) {
		return rangedMobs.computeIfAbsent(entity.getClass(), clazz -> {
			if (entity instanceof IRangedAttackMob)
				return true;

			if (entity instanceof BlazeEntity || entity instanceof ShulkerEntity || entity instanceof GhastEntity || entity instanceof GuardianEntity)
				return true;

			if (entity instanceof AbstractSkeletonEntity)
				return entity.getItemInHand(Hand.MAIN_HAND).getItem() instanceof BowItem;

			return false;
		});
	}
}

package net.tslat.tes.util;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraftforge.common.Tags;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.registries.ForgeRegistries;
import net.tslat.tes.api.TESEntityType;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class TESUtil implements net.tslat.tes.api.util.TESUtil {
	private final Map<Class<? extends LivingEntity>, TESEntityType> entityTypeMap = new Object2ObjectOpenHashMap<>();

	@Nullable
	@Override
	public LivingEntity getLivingEntityIfPossible(@Nullable Entity entity) {
		if (entity instanceof LivingEntity livingEntity) {
			if (livingEntity instanceof ArmorStand)
				return null;

			return livingEntity;
		}

		if (entity instanceof PartEntity<?> partEntity && partEntity.getParent() instanceof LivingEntity parent)
			return parent;

		return null;
	}

	@Override
	public TESEntityType getEntityType(LivingEntity entity) {
		return entityTypeMap.computeIfAbsent(entity.getClass(), clazz -> {
			if (entity.getType() == EntityType.PLAYER)
				return TESEntityType.PLAYER;

			if (ForgeRegistries.ENTITY_TYPES.tags().getTag(Tags.EntityTypes.BOSSES).contains(entity.getType()))
				return TESEntityType.BOSS;

			if (entity instanceof Enemy)
				return TESEntityType.HOSTILE;

			if (entity instanceof NeutralMob)
				return TESEntityType.NEUTRAL;

			return TESEntityType.PASSIVE;
		});
	}
}

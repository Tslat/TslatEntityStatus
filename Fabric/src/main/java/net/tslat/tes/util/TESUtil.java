package net.tslat.tes.util;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalEntityTypeTags;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.Enemy;
import net.tslat.tes.api.object.TESEntityType;
import net.tslat.tes.api.util.TESClientUtil;
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

		return null;
	}

	@Override
	public TESEntityType getEntityType(LivingEntity entity) {
		if (entity.getType() != EntityType.PLAYER && entity.isAlliedTo(TESClientUtil.getClientPlayer()))
			return TESEntityType.PASSIVE;

		return entityTypeMap.computeIfAbsent(entity.getClass(), clazz -> {
			if (entity.getType() == EntityType.PLAYER)
				return TESEntityType.PLAYER;

			if (isBossEntity(entity))
				return TESEntityType.BOSS;

			if (entity instanceof Enemy)
				return TESEntityType.HOSTILE;

			if (entity instanceof NeutralMob)
				return TESEntityType.NEUTRAL;

			return TESEntityType.PASSIVE;
		});
	}

	@Override
	public boolean isBossEntity(LivingEntity entity) {
		return BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(entity.getType()).is(ConventionalEntityTypeTags.BOSSES);
	}

	@Override
	public final void clearDynamicCaches() {
		entityTypeMap.entrySet().removeIf(classTESEntityTypeEntry -> classTESEntityTypeEntry.getValue() == TESEntityType.BOSS);
	}
}

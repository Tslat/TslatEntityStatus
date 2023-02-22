package net.tslat.tes.util;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalEntityTypeTags;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.Enemy;
import net.tslat.tes.api.TESEntityType;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.entity.multipart.api.EntityPart;

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

		if (entity instanceof EntityPart<?> partEntity && partEntity.getOwner() instanceof LivingEntity parent)
			return parent;

		return null;
	}

	@Override
	public TESEntityType getEntityType(LivingEntity entity) {
		return entityTypeMap.computeIfAbsent(entity.getClass(), clazz -> {
			if (entity.getType() == EntityType.PLAYER)
				return TESEntityType.PLAYER;

			if (BuiltInRegistries.ENTITY_TYPE.getTag(ConventionalEntityTypeTags.BOSSES).orElseThrow().contains(Holder.direct(entity.getType())))
				return TESEntityType.BOSS;

			if (entity instanceof Enemy)
				return TESEntityType.HOSTILE;

			if (entity instanceof NeutralMob)
				return TESEntityType.NEUTRAL;

			return TESEntityType.PASSIVE;
		});
	}
}

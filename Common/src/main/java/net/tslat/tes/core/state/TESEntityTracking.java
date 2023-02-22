package net.tslat.tes.core.state;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.tslat.tes.api.TESAPI;

import javax.annotation.Nullable;
import java.util.List;

public final class TESEntityTracking {
	private static final Int2ObjectOpenHashMap<EntityState> ENTITY_STATES = new Int2ObjectOpenHashMap<>(50);
	private static int LAST_GLOBAL_RENDER_COUNT = 10;
	private static List<LivingEntity> ENTITIES_TO_RENDER;

	public static void accountForEntity(LivingEntity entity) {
		ENTITY_STATES.compute(entity.getId(), (key, value) -> {
			double trackingDist = TESAPI.getConfig().getEntityTrackingDistance();

			if (entity.distanceToSqr(Minecraft.getInstance().cameraEntity) > trackingDist * trackingDist)
				return null;

			return value == null ? new EntityState(entity) : value;
		});
	}

	public static void tick() {
		ENTITY_STATES.values().forEach(EntityState::tick);

		if (Minecraft.getInstance().level.getGameTime() % TESAPI.getConfig().getCacheCleanFrequency() == 0)
			ENTITY_STATES.values().removeIf(state -> !state.isValid());
	}

	public static void addEntityToRender(LivingEntity entity) {
		if (ENTITIES_TO_RENDER == null)
			ENTITIES_TO_RENDER = new ObjectArrayList<>(LAST_GLOBAL_RENDER_COUNT);

		ENTITIES_TO_RENDER.add(entity);
	}

	@Nullable
	public static EntityState getStateForEntity(LivingEntity entity) {
		return getStateForEntityId(entity.getId());
	}

	@Nullable
	public static EntityState getStateForEntityId(int id) {
		return ENTITY_STATES.get(id);
	}

	public static List<LivingEntity> getEntitiesToRender() {
		if (ENTITIES_TO_RENDER == null) {
			LAST_GLOBAL_RENDER_COUNT = 10;

			return List.of();
		}

		List<LivingEntity> entities = ENTITIES_TO_RENDER;

		LAST_GLOBAL_RENDER_COUNT = ENTITIES_TO_RENDER.size();
		ENTITIES_TO_RENDER = null;

		return entities;
	}
}

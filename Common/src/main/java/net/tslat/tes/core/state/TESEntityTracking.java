package net.tslat.tes.core.state;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.tslat.tes.api.TESAPI;
import net.tslat.tes.core.particle.TESParticleManager;

import org.jetbrains.annotations.Nullable;
import java.util.List;

public final class TESEntityTracking {
	private static final Int2ObjectOpenHashMap<EntityState> ENTITY_STATES = new Int2ObjectOpenHashMap<>(50);
	private static List<LivingEntity> ENTITIES_TO_RENDER = new ObjectArrayList<>();
	private static IntSet RENDERED_NAMES = new IntOpenHashSet();

	public static void accountForEntity(LivingEntity entity) {
		ENTITY_STATES.compute(entity.getId(), (key, value) -> {
			double trackingDist = TESAPI.getConfig().getEntityTrackingDistance();

			if (entity.distanceToSqr((Minecraft.getInstance().cameraEntity == null ? Minecraft.getInstance().player : Minecraft.getInstance().cameraEntity)) > trackingDist * trackingDist)
				return null;

			return value == null ? new EntityState(entity) : value;
		});
	}

	public static void tick() {
		TESParticleManager.clearClaims();
		ENTITY_STATES.values().forEach(EntityState::tick);

		if (Minecraft.getInstance().level.getGameTime() % TESAPI.getConfig().getCacheCleanFrequency() == 0)
			ENTITY_STATES.values().removeIf(state -> !state.isValid());

	}

	public static void addEntityToRender(LivingEntity entity) {
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
		List<LivingEntity> entities = ENTITIES_TO_RENDER;
		ENTITIES_TO_RENDER = new ObjectArrayList<>(ENTITIES_TO_RENDER.size());

		RENDERED_NAMES.clear();

		return entities;
	}

	public static boolean wasNameRendered(int entityId) {
		return RENDERED_NAMES.contains(entityId);
	}

	public static void markNameRendered(LivingEntity entity) {
		RENDERED_NAMES.add(entity.getId());
	}
}

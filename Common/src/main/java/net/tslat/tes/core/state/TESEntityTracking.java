package net.tslat.tes.core.state;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.tslat.tes.api.TESAPI;
import net.tslat.tes.api.TESConstants;
import net.tslat.tes.core.particle.TESParticleManager;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class TESEntityTracking {
	private static final Int2ObjectOpenHashMap<EntityState> ENTITY_STATES = new Int2ObjectOpenHashMap<>(50);
	private static final IntSet RENDERED_NAMES = new IntOpenHashSet();
	private static List<LivingEntity> ENTITIES_TO_RENDER = new ObjectArrayList<>();

	public static void accountForEntity(LivingEntity entity) {
		ENTITY_STATES.compute(entity.getId(), (key, value) -> {
			if (entity.getSelfAndPassengers().anyMatch(passenger -> passenger == Minecraft.getInstance().player) && !TESAPI.getConfig().inWorldHudForSelf())
				return null;

			double trackingDist = TESAPI.getConfig().getEntityTrackingDistance();

			if (entity.distanceToSqr((Minecraft.getInstance().cameraEntity == null ? Minecraft.getInstance().player : Minecraft.getInstance().cameraEntity)) > trackingDist * trackingDist)
				return null;

			if (entity.getType().is(TESConstants.NO_TES_HANDLING))
				return null;

			return value == null ? new EntityState(entity) : value;
		});
	}

	public static void tick() {
		Minecraft mc = Minecraft.getInstance();

		if (!mc.isPaused() && mc.level != null && !mc.level.tickRateManager().isFrozen()) {
			TESParticleManager.clearClaims();

			for (ObjectIterator<EntityState> iterator = ENTITY_STATES.values().iterator(); iterator.hasNext();) {
				EntityState state = iterator.next();

				if (!state.isValid()) {
					iterator.remove();

					continue;
				}

				state.tick();
			}
		}
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

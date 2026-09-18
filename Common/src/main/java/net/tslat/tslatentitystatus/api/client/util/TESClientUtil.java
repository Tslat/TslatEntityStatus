package net.tslat.tslatentitystatus.api.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.tslat.tslatentitystatus.api.TESConfig;
import net.tslat.tslatentitystatus.api.TESConstants;
import net.tslat.tslatentitystatus.core.common.TESEntityStateHolder;
import net.tslat.tslatentitystatus.core.client.hud.TESHud;
import net.tslat.tslatentitystatus.core.state.TESEntityState;
import org.jspecify.annotations.Nullable;

/// Various helper methods for client-side functions
public final class TESClientUtil {
	/// Get the current config for `TES`
	public static TESConfig getConfig() {
		return TESConstants.getConfig();
	}

	/// Get `TES`' current HUD target entity if it has one
	public static @Nullable LivingEntity getCurrentHUDTarget() {
		return TESHud.getTargetEntity();
	}

	/// Get `TES`' current status data for a given entity if it has one
	public static @Nullable TESEntityState getTESEntityState(Entity entity) {
		return entity instanceof TESEntityStateHolder holder ? holder.tslatentitystatus$getEntityState() : null;
	}

	/// Get `TES`' current status data for a given entity if it has one
	public static @Nullable TESEntityState getTESEntityState(int entityId) {
		final Level level = TESClientUtil.getClientLevel();
		final Entity entity = level != null ? level.getEntity(entityId) : null;

		return entity != null ? getTESEntityState(entity) : null;
	}

	/// Get the client level, or null if the client is not currently in-world
	public static @Nullable Level getClientLevel() {
		return Minecraft.getInstance().level;
	}
	
	/// Get the client player, or null if the client is not yet initialized
	public static @Nullable Player getClientPlayer() {
		return Minecraft.getInstance().player;
	}

	/// Get the client's camera entity (usually the player themselves), or null if the client is not yet initialized
	public static @Nullable Entity getClientCamera() {
		return Minecraft.getInstance().getCameraEntity();
	}

	/// Get the client's camera position
	///
	/// This is typically more accurate than getting the camera entity's position
	public static Vec3 getCameraPosition() {
		return Minecraft.getInstance().gameRenderer.getMainCamera().position();
	}

    /// Get the closest position to the camera for the provided entity
    ///
    /// This allows for more accurate detection, particularly on larger entities
    public static Vec3 getClosestEntityPosition(Entity entity) {
		final Player player = getClientPlayer();

		if (player == null)
			return entity.position();

        final Vec3 cameraPos = getCameraPosition();
        final Vec3 angle = player.getLookAngle();

        return entity.getBoundingBox().clip(cameraPos.subtract(angle.scale(100)), cameraPos.add(angle.scale(100)))
                .orElseGet(() -> new Vec3(
						entity.getX() - Mth.cos(angle.x) * entity.getBbWidth() * 0.5f,
                        Mth.clamp(cameraPos.y, entity.getY(), entity.getY(1)),
                        entity.getZ() - Mth.sin(angle.z) * entity.getBbWidth() * 0.5f));
    }

	/// Safely get a [RandomSource] instance for consistently performing randomised `TES` functionality
	public static RandomSource getRandom() {
		final Level level = getClientLevel();

		return level != null ? level.getRandom() : RandomSource.createThreadLocalInstance();
	}

	public void doThing() {

	}
}

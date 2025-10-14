package net.tslat.tes.api.util;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * Various helper methods for client-side functions
 */
public final class TESClientUtil {
	/**
	 * Get the client player, or null if the client is not yet initialized
	 */
	@Nullable
	public static Player getClientPlayer() {
		return Minecraft.getInstance().player;
	}

	/**
	 * Get the client's camera entity (usually the player themselves), or null if the client is not yet initialized.
	 */
	@Nullable
	public static Entity getClientCamera() {
		return Minecraft.getInstance().getCameraEntity();
	}

	/**
	 * Get the client's camera position.
     * <p>
     * This is typically more accurate than getting the camera entity's position.
	 */
	public static Vec3 getCameraPosition() {
		return Minecraft.getInstance().gameRenderer.getMainCamera().position();
	}

    /**
     * Get the closest position to the camera for the provided entity
     * <p>
     * This allows for more accurate detection, particularly on larger entities
     */
    public static Vec3 getClosestEntityPosition(Entity entity) {
        final Vec3 cameraPos = getCameraPosition();
        final Vec3 angle = getClientPlayer().getLookAngle();

        return entity.getBoundingBox().clip(cameraPos, cameraPos.add(angle.scale(500)))
                .orElseGet(() -> new Vec3(Mth.cos((float)angle.x) * entity.getBbWidth() * 0.5f, Mth.clamp(cameraPos.y, entity.getY(), entity.getY(1)), Mth.sin((float)angle.z) * entity.getBbWidth() * 0.5f));
    }
}

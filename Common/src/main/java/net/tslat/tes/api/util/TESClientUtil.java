package net.tslat.tes.api.util;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
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
		return Minecraft.getInstance().cameraEntity;
	}
}

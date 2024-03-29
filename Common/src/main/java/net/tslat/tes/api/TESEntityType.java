package net.tslat.tes.api;

import net.minecraft.resources.ResourceLocation;

/**
 * "Entity type" enum for TES to differentiate entities for handling
 */
public interface TESEntityType {
	TESEntityType PASSIVE = new Impl(new ResourceLocation("boss_bar/green_background"), new ResourceLocation("boss_bar/green_progress"));
	TESEntityType NEUTRAL = new Impl(new ResourceLocation("boss_bar/yellow_background"), new ResourceLocation("boss_bar/yellow_progress"));
	TESEntityType HOSTILE = new Impl(new ResourceLocation("boss_bar/red_background"), new ResourceLocation("boss_bar/red_progress"));
	TESEntityType BOSS = new Impl(new ResourceLocation("boss_bar/purple_background"), new ResourceLocation("boss_bar/purple_progress"));
	TESEntityType PLAYER = new Impl(new ResourceLocation("boss_bar/white_background"), new ResourceLocation("boss_bar/white_progress"));

	/**
	 * @return The texture location of the background (empty) texture for this bar type
	 */
	ResourceLocation getBackgroundTexture();

	/**
	 * @return The texture location of the foreground/progress (filled) texture for this bar type
	 */
	ResourceLocation getOverlayTexture();

	class Impl implements TESEntityType {
		private final ResourceLocation backgroundTexture;
		private final ResourceLocation overlayTexture;

		public Impl(ResourceLocation backgroundTexture, ResourceLocation overlayTexture) {
			this.backgroundTexture = backgroundTexture;
			this.overlayTexture = overlayTexture;
		}

		@Override
		public ResourceLocation getBackgroundTexture() {
			return this.backgroundTexture;
		}

		@Override
		public ResourceLocation getOverlayTexture() {
			return this.overlayTexture;
		}
	}
}

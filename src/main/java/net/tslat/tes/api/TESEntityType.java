package net.tslat.tes.api;

/**
 * "Entity type" enum for TES to differentiate entities for handling
 */
public enum TESEntityType {
	PASSIVE(30),
	NEUTRAL(40),
	HOSTILE(20),
	BOSS(50),
	PLAYER(60);

	private final int textureYPos;

	TESEntityType(int textureYPos) {
		this.textureYPos = textureYPos;
	}

	/**
	 * Get the UV height index of the bar used for this entity type in minecraft:textures/gui/bars.png"
	 */
	public int getTextureYPos() {
		return this.textureYPos;
	}
}

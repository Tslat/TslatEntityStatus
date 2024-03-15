package net.tslat.tes.api;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.tslat.tes.api.util.TESUtil;
import net.tslat.tes.core.networking.TESNetworking;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.ServiceLoader;

/**
 * Common class to store all of the globally & statically accessible data points for TES
 */
public final class TESConstants {
	public static final String VERSION = "1.4.2";
	public static final String MOD_ID = "tslatentitystatus";
	public static final String MOD_NAME = "TES";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
	@Nullable
	public static TESConfig CONFIG = null;

	public static TESUtil UTILS = ServiceLoader.load(TESUtil.class).findFirst().get();
	public static TESNetworking NETWORKING = ServiceLoader.load(TESNetworking.class).findFirst().get();

	public static boolean IS_SERVER_SIDE = true;

	public static final TagKey<EntityType<?>> NO_TES_HANDLING = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(MOD_ID, "no_tes_handling"));

	public static void setConfig(TESConfig config) {
		CONFIG = config;
	}

	public static void setIsClient() {
		IS_SERVER_SIDE = false;
	}
}

package net.tslat.tslatentitystatus.api;

import com.google.common.base.Suppliers;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.tslat.tslatentitystatus.core.networking.TESNetworking;
import net.tslat.tslatentitystatus.core.platform.TESPlatform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.util.ServiceLoader;
import java.util.function.Supplier;

/// Common class to store all the globally and statically accessible data points for TES
public final class TESConstants {
	public static final String MOD_ID = "tslatentitystatus";
	public static final Supplier<Logger> LOGGER = Suppliers.memoize(() -> LogManager.getLogger(MOD_ID));

	private static final Identifier BASE_ID = Identifier.fromNamespaceAndPath(MOD_ID, "");
    public static final Identifier HUD_LAYER_ID = id("main_hud");

	private static @Nullable TESConfig CONFIG = null;

	public static final TESPlatform PLATFORM = ServiceLoader.load(TESPlatform.class).findFirst().orElseThrow();
	public static final TESNetworking NETWORKING = ServiceLoader.load(TESNetworking.class).findFirst().orElseThrow();

	/// The [TagKey] definition of the [EntityType] tag TES uses to blacklist entities from its handling
	public static final TagKey<EntityType<?>> NO_TES_HANDLING = TagKey.create(Registries.ENTITY_TYPE, id("no_tes_handling"));

	@ApiStatus.Internal
	public static void setConfig(TESConfig config) {
		CONFIG = config;
	}
	
	/// Get the TES config instance
	///
	/// **NOTE:** This can only be accessed on the client side
	public static TESConfig getConfig() {
		if (CONFIG == null)
			throw new IllegalStateException("Attempted to access the TES config on the server side!");
		
		return CONFIG;
	}

	/// Create an [Identifier] under the `TES` namespace
	///
	/// This is a more efficient method than creating a fully new instance every time, as it only parses half the id
	public static Identifier id(String path) {
		return BASE_ID.withPath(path);
	}
}

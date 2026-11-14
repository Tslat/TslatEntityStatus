package net.tslat.tslatentitystatus;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalEntityTypeTags;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.tslat.tslatentitystatus.api.client.util.TESClientUtil;
import net.tslat.tslatentitystatus.api.common.constant.TESEntityRelation;
import net.tslat.tslatentitystatus.core.platform.TESPlatform;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.util.Map;

public class TESFabricPlatform implements TESPlatform {
	private static final Map<EntityType<?>, TESEntityRelation> ENTITY_RELATIONS = new Reference2ObjectOpenHashMap<>();
	private static boolean HAS_SERVER_MOD = false;

	@Override
	public boolean isDevelopmentEnvironment() {
		return FabricLoaderImpl.INSTANCE.isDevelopmentEnvironment();
	}

	@Override
	public boolean isPhysicalClient() {
		return FabricLoaderImpl.INSTANCE.getEnvironmentType() == EnvType.CLIENT;
	}

	@Override
	public TESEntityRelation getTESEntityRelation(LivingEntity entity) {
		if (entity.getType() != EntityTypes.PLAYER && entity.isAlliedTo(TESClientUtil.getClientPlayer()))
			return TESEntityRelation.PASSIVE;

		if (entity.is(ConventionalEntityTypeTags.BOSSES))
			return TESEntityRelation.BOSS;

		return ENTITY_RELATIONS.computeIfAbsent(entity.getType(), _ -> switch(entity) {
			case Player pl when pl.getType() == EntityTypes.PLAYER -> TESEntityRelation.PLAYER;
			case Enemy _ -> TESEntityRelation.HOSTILE;
			case NeutralMob _ -> TESEntityRelation.NEUTRAL;
			default -> TESEntityRelation.PASSIVE;
		});
	}

	@ApiStatus.Internal
	@Override
	public void setSyncingEffects(boolean syncingEffects) {
		HAS_SERVER_MOD = syncingEffects;
	}

	@Override
	public boolean hasServerMod() {
		return HAS_SERVER_MOD;
	}

	/// @return An array of part entity instances for the given entity, or `null` if not a multipart entity
	@Override
	public Entity @Nullable [] getPartEntities(LivingEntity entity) {
		return null;
	}
}

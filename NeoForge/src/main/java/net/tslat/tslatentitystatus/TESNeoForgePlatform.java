package net.tslat.tslatentitystatus;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.entity.PartEntity;
import net.tslat.tslatentitystatus.api.client.util.TESClientUtil;
import net.tslat.tslatentitystatus.api.common.constant.TESEntityRelation;
import net.tslat.tslatentitystatus.core.platform.TESPlatform;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.util.Map;

public class TESNeoForgePlatform implements TESPlatform {
	private static final Map<EntityType<?>, TESEntityRelation> ENTITY_RELATIONS = new Reference2ObjectOpenHashMap<>();
	private static boolean HAS_SERVER_MOD = false;

	@Override
	public boolean isDevelopmentEnvironment() {
		return !FMLLoader.getCurrent().isProduction();
	}

	@Override
	public boolean isPhysicalClient() {
		return FMLLoader.getCurrent().getDist() == Dist.CLIENT;
	}

	@Override
	public @Nullable LivingEntity getDamageableEntity(@Nullable Entity entity) {
		if (entity instanceof LivingEntity livingEntity) {
			if (livingEntity instanceof ArmorStand)
				return null;

			return livingEntity;
		}

		if (entity instanceof PartEntity<?> partEntity)
			return getDamageableEntity(partEntity.getParent());

		return null;
	}

	@Override
	public TESEntityRelation getTESEntityRelation(LivingEntity entity) {
		if (entity.getType() != EntityTypes.PLAYER && entity.isAlliedTo(TESClientUtil.getClientPlayer()))
			return TESEntityRelation.PASSIVE;

		if (entity.is(Tags.EntityTypes.BOATS))
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
}

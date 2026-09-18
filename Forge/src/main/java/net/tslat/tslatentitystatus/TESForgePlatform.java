package net.tslat.tslatentitystatus;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.Tags;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.fml.loading.FMLLoader;
import net.tslat.tslatentitystatus.api.client.util.TESClientUtil;
import net.tslat.tslatentitystatus.api.common.constant.TESEntityRelation;
import net.tslat.tslatentitystatus.core.platform.TESPlatform;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.util.Map;

public class TESForgePlatform implements TESPlatform {
	private static final Map<EntityType<?>, TESEntityRelation> ENTITY_RELATIONS = new Reference2ObjectOpenHashMap<>();
	private static boolean HAS_SERVER_MOD = false;

	@Override
	public boolean isDevelopmentEnvironment() {
		return !FMLLoader.isProduction();
	}

	@Override
	public boolean isPhysicalClient() {
		return FMLLoader.getDist() == Dist.CLIENT;
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
		if (entity.getType() != EntityType.PLAYER && entity.isAlliedTo(TESClientUtil.getClientPlayer()))
			return TESEntityRelation.PASSIVE;

		if (entity.is(Tags.EntityTypes.BOATS))
			return TESEntityRelation.BOSS;

		return ENTITY_RELATIONS.computeIfAbsent(entity.getType(), _ -> switch(entity) {
            case Player pl when pl.getType() == EntityType.PLAYER -> TESEntityRelation.PLAYER;
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

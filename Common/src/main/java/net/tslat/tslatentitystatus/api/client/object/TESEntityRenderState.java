package net.tslat.tslatentitystatus.api.client.object;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.equine.AbstractChestedHorse;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.phys.Vec3;
import net.tslat.tslatentitystatus.api.TESConstants;
import net.tslat.tslatentitystatus.api.client.util.TESClientUtil;
import net.tslat.tslatentitystatus.api.common.constant.TESEntityRelation;
import net.tslat.tslatentitystatus.api.common.util.TESUtil;
import net.tslat.tslatentitystatus.core.client.hud.TESHud;
import net.tslat.tslatentitystatus.core.state.TESEntityState;

import java.util.List;
import java.util.Map;

/// [EntityRenderState] container for [TESEntityState] data for TES rendering
///
/// Cannot be extended; use [#extraData] for adding third-party data needed for custom TESHud elements
public final class TESEntityRenderState {
	public final int entityId;
	public final EntityType<? extends LivingEntity> entityType;

	public final Vec3 entityRenderOffset;

	public final float health;
	public final float prevHealth;
	public final float maxHealth;
	public final float prevTransitionHealth;
	public final long prevTransitionTime;

	public final float partialTick;

	public final TESEntityRelation entityRelation;

	public final boolean isPlayerOrVehicle;

	public final boolean hasCustomName;
	public final Component displayName;

	public final int armour;
	public final float armorToughness;
	public final float meleeDamage;
	public final boolean isHorse;
	public final boolean hasContainer;
	public final double moveSpeed;
	public final double jumpStrength;

	public final List<Holder<MobEffect>> effects;

	public final boolean isFireImmune;
	public final boolean isMeleeMob;
	public final boolean isRangedMob;
	public final boolean isAquatic;
	public final boolean isIllager;
	public final boolean isArthropod;
	public final boolean isUndead;

	public final Supplier<Map<String, Object>> extraData = Suppliers.memoize(Object2ObjectOpenHashMap::new);

	private TESEntityRenderState(LivingEntity entity, TESEntityState tesState, Vec3 entityRenderOffset, float partialTick) {
		this.entityId = entity.getId();
        //noinspection unchecked
        this.entityType = (EntityType<? extends LivingEntity>)entity.getType();
		this.entityRenderOffset = entityRenderOffset;
		this.health = tesState.getHealth();
		this.prevHealth = tesState.getLastHealth();
		this.maxHealth = entity.getMaxHealth();
		this.prevTransitionHealth = tesState.getLastTransitionHealth();
		this.prevTransitionTime = tesState.getLastTransitionTime();
		this.partialTick = partialTick;
		this.entityRelation = TESConstants.PLATFORM.getTESEntityRelation(entity);
		this.isPlayerOrVehicle = entity.getPassengersAndSelf().anyMatch(e -> e == TESClientUtil.getClientPlayer());
		this.hasCustomName = entity.hasCustomName();
		this.displayName = entity.getDisplayName();
		this.armour = TESUtil.getArmour(entity);
		this.armorToughness = TESUtil.getArmourToughness(entity);
		this.meleeDamage = TESUtil.getMeleeDamage(entity);
		this.isHorse = entity instanceof AbstractHorse;
		this.hasContainer = entity instanceof AbstractChestedHorse chestedHorse && chestedHorse.hasChest();
		this.moveSpeed = getAttributeValue(entity, Attributes.MOVEMENT_SPEED);
		this.jumpStrength = getAttributeValue(entity, Attributes.JUMP_STRENGTH);
		this.effects = List.copyOf(tesState.getEffects());
		this.isFireImmune = TESUtil.isFireImmune(entity);
		this.isMeleeMob = TESUtil.isMeleeMob(entity);
		this.isRangedMob = TESUtil.isRangedMob(entity);
		this.isAquatic = entity.is(EntityTypeTags.AQUATIC);
		this.isIllager = entity.is(EntityTypeTags.ILLAGER);
		this.isArthropod = entity.is(EntityTypeTags.ARTHROPOD);
		this.isUndead = entity.is(EntityTypeTags.UNDEAD);
	}

	/// Create a new [TESEntityRenderState] instance for the current render pass for the given entity
	public static TESEntityRenderState create(LivingEntity entity, TESEntityState tesState, Vec3 entityRenderOffset, float partialTick) {
        final TESEntityRenderState renderState = new TESEntityRenderState(entity, tesState, entityRenderOffset, partialTick);

		TESHud.collectThirdPartyRenderData(renderState, entity, tesState, partialTick);

		return renderState;
	}

	private static double getAttributeValue(LivingEntity entity, Holder<Attribute> attribute) {
		return entity.getAttributes().hasAttribute(attribute) ? entity.getAttributeValue(attribute) : 0;
	}
}

package net.tslat.tes.core.hud;

import com.mojang.blaze3d.Blaze3D;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.Util;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.tslat.tes.api.TESAPI;
import net.tslat.tes.api.TESConfig;
import net.tslat.tes.api.TESConstants;
import net.tslat.tes.api.TESTextures;
import net.tslat.tes.api.object.TESHudElement;
import net.tslat.tes.api.object.TESHudRenderContext;
import net.tslat.tes.api.util.TESClientUtil;
import net.tslat.tes.api.util.TESRenderUtil;
import net.tslat.tes.api.util.TESUtil;
import net.tslat.tes.core.hud.element.BuiltinHudElements;
import net.tslat.tes.core.hud.element.TESHudEntityIcon;
import net.tslat.tes.core.state.EntityState;
import net.tslat.tes.core.state.TESEntityTracking;
import org.joml.Matrix3x2fStack;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Manager class for the TES HUD.
 * <p>
 * Handles rendering and updating the HUD itself, as well as taking registrations of new {@link TESHudElement TES HUD Elements}
 */
public class TESHud {
	private static final Map<String, TESHudElement> ELEMENTS = Util.make(Collections.synchronizedMap(new Object2ObjectArrayMap<>()), map -> {
			map.put("EntityName", BuiltinHudElements::renderEntityName);
			map.put("HealthBar", BuiltinHudElements::renderEntityHealth);
			map.put("Stats", BuiltinHudElements::renderEntityStats);
			map.put("Icons", BuiltinHudElements::renderEntityIcons);
			map.put("Effects", BuiltinHudElements::renderEntityEffects);
			map.put("HorseStats", BuiltinHudElements::renderHorseStats);
	});
	protected static final List<TESHudEntityIcon> ENTITY_ICONS = Util.make(new CopyOnWriteArrayList<>(), list -> {
		list.addAll(List.of(
				TESHudEntityIcon.makeGeneric(TESTextures.PROPERTY_FIRE_IMMUNE, TESUtil::isFireImmune),
				TESHudEntityIcon.makeGeneric(TESTextures.PROPERTY_MELEE, TESUtil::isMeleeMob),
				TESHudEntityIcon.makeGeneric(TESTextures.PROPERTY_RANGED, TESUtil::isRangedMob),
				TESHudEntityIcon.makeGeneric(TESTextures.ENTITY_TYPE_AQUATIC, entity -> entity.getType().is(EntityTypeTags.AQUATIC)),
				TESHudEntityIcon.makeGeneric(TESTextures.ENTITY_TYPE_ILLAGER, entity -> entity.getType().is(EntityTypeTags.ILLAGER)),
				TESHudEntityIcon.makeGeneric(TESTextures.ENTITY_TYPE_ARTHROPOD, entity -> entity.getType().is(EntityTypeTags.ARTHROPOD)),
				TESHudEntityIcon.makeGeneric(TESTextures.ENTITY_TYPE_UNDEAD, entity -> entity.getType().is(EntityTypeTags.UNDEAD))));
	});
	private static TESHudElement[] INVERSE_ELEMENTS = buildInverseElementArray(ELEMENTS.values());
	private static LivingEntity TARGET_ENTITY = null;
	private static long TARGET_EXPIRY_TIME = -1L;

	/**
	 * Set the current target entity for HUD rendering
	 */
	public static void setTargetEntity(LivingEntity entity) {
		TARGET_ENTITY = entity;
		TARGET_EXPIRY_TIME = Mth.floor(Blaze3D.getTime() * 20) + 1 + TESAPI.getConfig().hudTargetGracePeriod();
	}

	/**
	 * Get the current target entity for HUD rendering
	 */
	public static LivingEntity getTargetEntity() {
		return TARGET_ENTITY;
	}

	/**
	 * Add a {@link TESHudElement} to the manager
	 */
	public static void addHudElement(String name, TESHudElement element) {
		synchronized (ELEMENTS) {
			ELEMENTS.put(name, element);
			INVERSE_ELEMENTS = buildInverseElementArray(ELEMENTS.values());
		}
	}

	/**
	 * Remove an existing {@link TESHudElement element} from the TES HUD
	 * @param name The name of the element to remove
	 * @return Whether an element with the given name was present or not
	 */
	public static boolean removeHudElement(String name) {
		AtomicBoolean removed = new AtomicBoolean(false);

		synchronized (ELEMENTS) {
			removed.set(ELEMENTS.remove(name) != null);
			INVERSE_ELEMENTS = buildInverseElementArray(ELEMENTS.values());
		}

		return removed.get();
	}

	/**
	 * Add a {@link TESHudEntityIcon} to the TES HUD
	 *
	 * @param icon The icon instance to add
	 */
	public static void addHudEntityIcon(TESHudEntityIcon icon) {
		ENTITY_ICONS.add(icon);
	}

	public static void renderForHud(GuiGraphics guiGraphics, Minecraft mc, DeltaTracker deltaTracker) {
		if (TARGET_ENTITY == null)
			return;

		if (TARGET_ENTITY.isRemoved() || TARGET_ENTITY.level() != mc.level || Mth.floor(Blaze3D.getTime() * 20) > TARGET_EXPIRY_TIME) {
			TARGET_ENTITY = null;

			return;
		}

		TESConfig config = TESAPI.getConfig();

		if (!config.hudEnabled() || Minecraft.getInstance().options.hideGui || (!config.hudBossesEnabled() && TESConstants.UTILS.isBossEntity(TARGET_ENTITY)))
			return;

		float hudOpacity = config.hudOpacity();
		Matrix3x2fStack poseStack = guiGraphics.pose();

		poseStack.pushMatrix();
		Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.ITEMS_3D);
		config.hudRenderPosition().adjustRenderForHudPosition(guiGraphics);

		if (TESAPI.getConfig().hudEntityRender()) {
			TESRenderUtil.renderEntityIcon(guiGraphics, mc, deltaTracker, TARGET_ENTITY, hudOpacity, true);

			poseStack.translate(40, 0);
		}

		poseStack.translate(0, 2);

		Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.ITEMS_3D);

		TESHudRenderContext renderContext = TESHudRenderContext.guiContext(guiGraphics);

		for (TESHudElement element : ELEMENTS.values()) {
			int offset = element.render(renderContext, mc, deltaTracker, TARGET_ENTITY, hudOpacity);

			if (offset > 0)
				poseStack.translate(0, 2 + offset);
		}

		poseStack.popMatrix();
	}

	public static void renderInWorld(PoseStack poseStack, LivingEntity entity, DeltaTracker deltaTracker) {
		EntityState entityState = TESEntityTracking.getStateForEntity(entity);

		if (entityState == null || !entityState.isValid())
			return;

		entityState.markActive();

		Minecraft mc = Minecraft.getInstance();
		TESConfig config = TESAPI.getConfig();

		if (!config.inWorldBarsEnabled() ||
				(entity.getSelfAndPassengers().anyMatch(passenger -> passenger == mc.player) && !config.inWorldHudForSelf()) ||
				!config.inWorldHUDActivation().test(entityState) ||
				(!config.inWorldHudBossesEnabled() && TESConstants.UTILS.isBossEntity(TARGET_ENTITY)))
			return;

		float partialTick = deltaTracker.getGameTimeDeltaPartialTick(!entity.level().tickRateManager().isEntityFrozen(entity));
		float hudOpacity = config.inWorldHudOpacity();
		EntityRenderer<? super LivingEntity, EntityRenderState> renderer = (EntityRenderer)mc.getEntityRenderDispatcher().getRenderer(entity);
		Vec3 position = entity.getPosition(partialTick)
				.subtract(mc.gameRenderer.getMainCamera().getPosition())
				.add(renderer.getRenderOffset(renderer.createRenderState(entity, partialTick)));

		Vec3 nameTagOffset = entity.getAttachments().getNullable(EntityAttachment.NAME_TAG, 0, entity.getYRot(partialTick));

		if (nameTagOffset == null)
			nameTagOffset = new Vec3(0, entity.getBbHeight(), 0);

		nameTagOffset = nameTagOffset.add(0, 0.5f, 0);

		poseStack.pushPose();
		poseStack.translate(position.x, position.y, position.z);
		poseStack.translate(nameTagOffset);
		poseStack.translate(0, config.inWorldHudManualVerticalOffset(), 0);

		TESRenderUtil.positionFacingCamera(poseStack);
		poseStack.scale(0.02f, 0.02f, 0.02f);

		TESHudRenderContext renderContext = TESHudRenderContext.inWorldContext(poseStack, mc.renderBuffers().bufferSource(), mc.getEntityRenderDispatcher().getRenderer(entity).getPackedLightCoords(entity, partialTick));

		for (TESHudElement element : INVERSE_ELEMENTS) {
			int offset = element.render(renderContext, mc, deltaTracker, entity, hudOpacity);

			if (offset > 0)
				poseStack.translate(0, -(2 + offset), 0);
		}

		poseStack.popPose();
	}

	public static void pickNewEntity(float partialTick) {
		Minecraft mc = Minecraft.getInstance();

		if (mc.crosshairPickEntity != null) {
			LivingEntity target = TESConstants.UTILS.getLivingEntityIfPossible(mc.crosshairPickEntity);

			if (target != null && TESUtil.shouldTESHandleEntity(target, TESClientUtil.getClientPlayer()))
				TESHud.setTargetEntity(target);
		}
		else {
			double targetingRange = TESAPI.getConfig().hudTargetDistance();
			Entity cameraEntity = mc.getCameraEntity();
			Vec3 cameraPos = cameraEntity.getEyePosition(partialTick);
			Vec3 cameraView = cameraEntity.getViewVector(partialTick);
			Vec3 rayEnd = cameraPos.add(cameraView.multiply(targetingRange, targetingRange, targetingRange));
			AABB hitBounds = cameraEntity.getBoundingBox().expandTowards(cameraView.scale(targetingRange)).inflate(1, 1, 1);
			EntityHitResult hitResult = ProjectileUtil.getEntityHitResult(cameraEntity, cameraPos, rayEnd, hitBounds, entity -> !entity.isSpectator() && entity.isPickable(), targetingRange * targetingRange);

			if (hitResult == null)
				return;

			LivingEntity target = TESConstants.UTILS.getLivingEntityIfPossible(hitResult.getEntity());

			if (target == null || !TESUtil.shouldTESHandleEntity(target, TESClientUtil.getClientPlayer()))
				return;

			double entityHitClipDistanceSqr = hitResult.getLocation().distanceToSqr(cameraPos);
			targetingRange = Math.sqrt(entityHitClipDistanceSqr);
			rayEnd = cameraPos.add(cameraView.multiply(targetingRange, targetingRange, targetingRange));

			HitResult blockHitResult = cameraEntity.level().clip(new ClipContext(cameraPos, rayEnd, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, cameraEntity));

			if (blockHitResult == null || blockHitResult.getType() == HitResult.Type.MISS || blockHitResult.getLocation().distanceToSqr(cameraPos) > entityHitClipDistanceSqr)
				TESHud.setTargetEntity(target);
		}
	}

	public static List<TESHudEntityIcon> getEntityIcons() {
		return ENTITY_ICONS;
	}

	private static TESHudElement[] buildInverseElementArray(Collection<TESHudElement> elements) {
		TESHudElement[] array = new TESHudElement[elements.size()];

		int i = elements.size() - 1;

		for (TESHudElement element : elements) {
			array[i--] = element;
		}

		return array;
	}

	public enum BarRenderType {
		NUMERIC,
		BAR,
		BAR_ICONS,
		COMBINED;
	}
}

package net.tslat.tslatentitystatus.core.client.hud;

import com.mojang.blaze3d.Blaze3D;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.tslat.tslatentitystatus.api.TESConfig;
import net.tslat.tslatentitystatus.api.TESConstants;
import net.tslat.tslatentitystatus.api.client.constant.TESTextures;
import net.tslat.tslatentitystatus.api.client.object.TESEntityRenderState;
import net.tslat.tslatentitystatus.api.client.object.TESHudElement;
import net.tslat.tslatentitystatus.api.client.object.TESHudEntityIcon;
import net.tslat.tslatentitystatus.api.client.object.TESHudRenderContext;
import net.tslat.tslatentitystatus.api.client.util.TESClientUtil;
import net.tslat.tslatentitystatus.api.client.util.TESRenderUtil;
import net.tslat.tslatentitystatus.api.common.constant.TESEntityRelation;
import net.tslat.tslatentitystatus.api.common.util.TESUtil;
import net.tslat.tslatentitystatus.core.state.TESEntityState;
import org.joml.Matrix3x2fStack;
import org.jspecify.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/// Manager class for the TES HUD
///
/// Handles rendering and updating the HUD itself, as well as taking registrations of new [`TES HUD Elements`][TESHudElement]
public class TESHud {
	private static final Map<String, TESHudElement> ELEMENTS = Util.make(Collections.synchronizedMap(new Object2ObjectArrayMap<>()), map -> {
			map.put("EntityName", BuiltinHudElements::submitEntityName);
			map.put("HealthBar", BuiltinHudElements::submitEntityHealth);
			map.put("Stats", BuiltinHudElements::submitEntityStats);
			map.put("Icons", BuiltinHudElements::submitEntityIcons);
			map.put("Effects", BuiltinHudElements::submitEntityEffects);
			map.put("HorseStats", BuiltinHudElements::submitHorseStats);
	});
	protected static final List<TESHudEntityIcon> ENTITY_ICONS = Util.make(new CopyOnWriteArrayList<>(), list -> {
		list.addAll(List.of(
				TESHudEntityIcon.makeGeneric(TESTextures.PROPERTY_FIRE_IMMUNE, state -> state.isFireImmune),
				TESHudEntityIcon.makeGeneric(TESTextures.PROPERTY_MELEE, state -> state.isMeleeMob),
				TESHudEntityIcon.makeGeneric(TESTextures.PROPERTY_RANGED, state -> state.isRangedMob),
				TESHudEntityIcon.makeGeneric(TESTextures.ENTITY_TYPE_AQUATIC, state -> state.isAquatic),
				TESHudEntityIcon.makeGeneric(TESTextures.ENTITY_TYPE_ILLAGER, state -> state.isIllager),
				TESHudEntityIcon.makeGeneric(TESTextures.ENTITY_TYPE_ARTHROPOD, state -> state.isArthropod),
				TESHudEntityIcon.makeGeneric(TESTextures.ENTITY_TYPE_UNDEAD, state -> state.isUndead)));
	});
	private static TESHudElement[] INVERSE_ELEMENTS = buildInverseElementArray(ELEMENTS.values());
	private static WeakReference<@Nullable LivingEntity> TARGET_ENTITY = new WeakReference<>(null);
	private static long TARGET_EXPIRY_TIME = -1L;

	/// Set the current target entity for HUD rendering
	public static void setTargetEntity(@Nullable LivingEntity entity) {
		TARGET_ENTITY = new WeakReference<>(entity);
		TARGET_EXPIRY_TIME = entity == null ? -1 : Mth.floor(Blaze3D.getTime() * 20) + 1 + TESConstants.getConfig().hudTargetGracePeriod();
	}

	/// Get the current target entity for HUD rendering
	@Nullable
	public static LivingEntity getTargetEntity() {
        final LivingEntity target = TARGET_ENTITY.get();

        if (target != null) {
            if (target.isRemoved() ||
                target.level() != Minecraft.getInstance().level ||
                Mth.floor(Blaze3D.getTime() * 20) > TARGET_EXPIRY_TIME) {
                TARGET_ENTITY = new WeakReference<>(null);
				TARGET_EXPIRY_TIME = -1;

                return null;
            }
        }


		return target;
	}

	/// Add a [TESHudElement] to the manager
	public static void addHudElement(String name, TESHudElement element) {
		synchronized (ELEMENTS) {
			ELEMENTS.put(name, element);
			INVERSE_ELEMENTS = buildInverseElementArray(ELEMENTS.values());
		}
	}

	/// Remove an existing [`element`][TESHudElement] from the TES HUD
	/// @param name The name of the element to remove
	/// @return Whether an element with the given name was present or not
	public static boolean removeHudElement(String name) {
		AtomicBoolean removed = new AtomicBoolean(false);

		synchronized (ELEMENTS) {
			removed.set(ELEMENTS.remove(name) != null);
			INVERSE_ELEMENTS = buildInverseElementArray(ELEMENTS.values());
		}

		return removed.get();
	}

	/// Add a [TESHudEntityIcon] to the TES HUD
	///
	/// @param icon The icon instance to add
	public static void addHudEntityIcon(TESHudEntityIcon icon) {
		ENTITY_ICONS.add(icon);
	}

	/// Gather all additional third-party render data required by registered [TESHudElement]s
	public static void collectThirdPartyRenderData(TESEntityRenderState tesRenderState, LivingEntity entity, TESEntityState tesState, float partialTick) {
		for (TESHudElement hudElement : INVERSE_ELEMENTS) {
			hudElement.addRenderData(tesRenderState, entity, tesState, partialTick);
		}
	}

	/// Submit the GUI HUD render tasks for the current render pass
	public static void submitHudRenderTasks(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker) {
        final LivingEntity target = getTargetEntity();
		final Minecraft mc = Minecraft.getInstance();

        if (target == null)
            return;

		final TESConfig config = TESConstants.getConfig();
		final TESEntityState tesEntityState = target.tslatentitystatus$getEntityState();

		if (!config.hudEnabled() || Minecraft.getInstance().gui.hud.isHidden() || tesEntityState == null)
			return;

		if (!config.hudBossesEnabled() && TESConstants.PLATFORM.getTESEntityRelation(target) == TESEntityRelation.BOSS)
			return;

		final TickRateManager tickRateManager = target.level().tickRateManager();
		final TESEntityRenderState tesRenderState = TESEntityRenderState.create(target, tesEntityState, Vec3.ZERO, deltaTracker.getGameTimeDeltaPartialTick(!tickRateManager.isEntityFrozen(target)));
		final float hudOpacity = config.hudOpacity();
		final Matrix3x2fStack poseStack = guiGraphics.pose();
        final TESHudRenderContext renderContext = TESHudRenderContext.guiContext(guiGraphics, deltaTracker.getGameTimeDeltaTicks());

		poseStack.pushMatrix();
		Minecraft.getInstance().gameRenderer.lighting().setupFor(Lighting.Entry.ITEMS_3D);
		config.hudRenderPosition().adjustRenderForHudPosition(guiGraphics);

		if (TESConstants.getConfig().hudEntityRender()) {
			TESRenderUtil.renderEntityIcon(renderContext.args().left().get(), mc, target, hudOpacity, true);

			poseStack.translate(40, 0);
		}

		poseStack.translate(0, 2);

		Minecraft.getInstance().gameRenderer.lighting().setupFor(Lighting.Entry.ITEMS_3D);



		for (TESHudElement element : ELEMENTS.values()) {
			int offset = element.submit(renderContext, tesRenderState, hudOpacity);

			if (offset > 0)
				poseStack.translate(0, 2 + offset);
		}

		poseStack.popMatrix();
	}

	/// Submit the in-world render tasks for the given TES entity
	public static void submitWorldRenderTasks(TESEntityRenderState tesRenderState, LivingEntityRenderState renderState, PoseStack poseStack, SubmitNodeCollector renderTasks,
											  CameraRenderState cameraRenderState) {
		Minecraft mc = Minecraft.getInstance();
		TESConfig config = TESConstants.getConfig();

		if (!config.inWorldBarsEnabled() ||
		    (tesRenderState.isPlayerOrVehicle && !config.inWorldHudForSelf()) ||
		    !config.inWorldHUDActivation().shouldBeActiveFor(renderState, tesRenderState) ||
		    (!config.inWorldHudBossesEnabled() && tesRenderState.entityRelation == TESEntityRelation.BOSS))
			return;

		float hudOpacity = config.inWorldHudOpacity();
		Vec3 position = new Vec3(renderState.x, renderState.y, renderState.z)
				.subtract(mc.gameRenderer.mainCamera().position())
				.add(tesRenderState.entityRenderOffset);

		Vec3 nameTagOffset = renderState.nameTagAttachment;

		if (nameTagOffset == null)
			nameTagOffset = new Vec3(0, renderState.boundingBoxHeight, 0);

		nameTagOffset = nameTagOffset.add(0, 0.5f, 0);

		poseStack.pushPose();
		poseStack.translate(position.x, position.y, position.z);
		poseStack.translate(nameTagOffset);
		poseStack.translate(0, config.inWorldHudManualVerticalOffset(), 0);

		TESRenderUtil.positionFacingCamera(poseStack);
		poseStack.scale(0.02f, 0.02f, 0.02f);

		TESHudRenderContext renderContext = TESHudRenderContext.inWorldContext(poseStack, renderTasks, cameraRenderState, tesRenderState.partialTick, renderState.lightCoords);

		for (TESHudElement element : INVERSE_ELEMENTS) {
			int offset = element.submit(renderContext, tesRenderState, hudOpacity);

			if (offset > 0)
				poseStack.translate(0, -(2 + offset), 0);
		}

		poseStack.popPose();
	}

	public static void pickNewEntity(float partialTick, @Nullable Entity crosshairTarget) {
		final Player player = TESClientUtil.getClientPlayer();

		if (player == null) {
			TESHud.setTargetEntity(null);

			return;
		}

		if (crosshairTarget != null) {
			LivingEntity target = TESConstants.PLATFORM.getDamageableEntity(crosshairTarget);

			if (target != null && TESUtil.shouldTESHandleEntity(target, TESClientUtil.getClientPlayer()) && target.tslatentitystatus$getEntityState() != null)
				TESHud.setTargetEntity(target);
		}
		else {
			Entity cameraEntity = TESClientUtil.getClientCamera();

			if (cameraEntity == null) {
				TESHud.setTargetEntity(null);

				return;
			}

			double targetingRange = TESConstants.getConfig().hudTargetDistance();
			Vec3 cameraPos = TESClientUtil.getCameraPosition();
			Vec3 cameraView = cameraEntity.getViewVector(partialTick);
			Vec3 rayEnd = cameraPos.add(cameraView.multiply(targetingRange, targetingRange, targetingRange));
			AABB hitBounds = cameraEntity.getBoundingBox().expandTowards(cameraView.scale(targetingRange)).inflate(1, 1, 1);
			EntityHitResult hitResult = ProjectileUtil.getEntityHitResult(cameraEntity, cameraPos, rayEnd, hitBounds, entity -> !entity.isSpectator() && entity.isPickable(), targetingRange * targetingRange);

			if (hitResult == null)
				return;

			LivingEntity target = TESConstants.PLATFORM.getDamageableEntity(hitResult.getEntity());

			if (target == null || !TESUtil.shouldTESHandleEntity(target, TESClientUtil.getClientPlayer()))
				return;

			double entityHitClipDistanceSqr = hitResult.getLocation().distanceToSqr(cameraPos);
			targetingRange = Math.sqrt(entityHitClipDistanceSqr);
			rayEnd = cameraPos.add(cameraView.multiply(targetingRange, targetingRange, targetingRange));
			HitResult blockHitResult = cameraEntity.level().clip(new ClipContext(cameraPos, rayEnd, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, cameraEntity));

			if ((blockHitResult.getType() == HitResult.Type.MISS || blockHitResult.getLocation().distanceToSqr(cameraPos) > entityHitClipDistanceSqr) && target.tslatentitystatus$getEntityState() != null)
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

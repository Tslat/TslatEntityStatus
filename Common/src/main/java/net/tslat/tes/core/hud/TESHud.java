package net.tslat.tes.core.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.tslat.tes.api.TESAPI;
import net.tslat.tes.api.TESConstants;
import net.tslat.tes.api.TESEntityType;
import net.tslat.tes.api.TESHudElement;
import net.tslat.tes.api.util.TESClientUtil;
import net.tslat.tes.api.util.TESUtil;
import net.tslat.tes.core.hud.element.BuiltinHudElements;
import net.tslat.tes.core.state.EntityState;
import net.tslat.tes.core.state.TESEntityTracking;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Manager class for the TES HUD.<br>
 * Handles rendering and updating the HUD itself, as well as taking registrations of new {@link TESHudElement TES HUD Elements}
 */
public class TESHud {
	private static final Map<String, TESHudElement> ELEMENTS = Util.make(Collections.synchronizedMap(new Object2ObjectArrayMap<>()), map -> {
			map.put("EntityName", BuiltinHudElements::renderEntityName);
			map.put("HealthBar", BuiltinHudElements::renderEntityHealth);
			map.put("Armour", BuiltinHudElements::renderEntityArmour);
			map.put("Icons", BuiltinHudElements::renderEntityIcons);
			map.put("Effects", BuiltinHudElements::renderEntityEffects);
	});
	private static TESHudElement[] INVERSE_ELEMENTS = buildInverseElementArray(ELEMENTS.values());
	private static LivingEntity TARGET_ENTITY = null;
	private static long TARGET_EXPIRY_TIME = -1L;

	/**
	 * Set the current target entity for HUD rendering
	 */
	public static void setTargetEntity(LivingEntity entity) {
		TARGET_ENTITY = entity;
		TARGET_EXPIRY_TIME = Minecraft.getInstance().level.getGameTime() + 1 + TESAPI.getConfig().hudTargetGracePeriod();
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

	public static void renderForHud(PoseStack poseStack, Minecraft mc, float partialTick) {
		if (TARGET_ENTITY == null)
			return;

		if (!TARGET_ENTITY.isAlive() || TARGET_ENTITY.level != mc.level || mc.level.getGameTime() > TARGET_EXPIRY_TIME) {
			TARGET_ENTITY = null;

			return;
		}

		if (!TESAPI.getConfig().hudEnabled() || (!TESAPI.getConfig().hudBossesEnabled() && TESConstants.UTILS.getEntityType(TARGET_ENTITY) == TESEntityType.BOSS))
			return;

		float hudOpacity = TESAPI.getConfig().hudOpacity();

		poseStack.pushPose();
		RenderSystem.enableBlend();
		RenderSystem.setShaderColor(1, 1, 1, hudOpacity);

		if (TESAPI.getConfig().hudEntityRender()) {
			TESClientUtil.renderEntityIcon(poseStack, mc, partialTick, TARGET_ENTITY, hudOpacity, true);

			poseStack.translate(40, 0, 0);
		}

		poseStack.translate(0, 2, 0);

		for (TESHudElement element : ELEMENTS.values()) {
			int offset = element.render(poseStack, mc, partialTick, TARGET_ENTITY, hudOpacity, false);

			if (offset > 0)
				poseStack.translate(0, 2 + offset, 0);
		}

		RenderSystem.setShaderColor(1, 1, 1, 1);
		poseStack.popPose();
	}

	public static void renderInWorld(PoseStack poseStack, LivingEntity entity, float partialTick) {
		if (!TESAPI.getConfig().inWorldBarsEnabled() || entity.isDeadOrDying() || (entity == Minecraft.getInstance().player && !TESAPI.getConfig().inWorldHudForSelf()))
			return;

		EntityState entityState = TESEntityTracking.getStateForEntity(entity);

		if (entityState == null || !TESAPI.getConfig().inWorldHUDActivation().test(entityState))
			return;

		float hudOpacity = TESAPI.getConfig().inWorldHudOpacity();
		Minecraft mc = Minecraft.getInstance();
		Vec3 position = entity.getPosition(partialTick)
				.subtract(mc.gameRenderer.getMainCamera().getPosition())
				.add(mc.getEntityRenderDispatcher().getRenderer(entity).getRenderOffset(entity, partialTick));

		poseStack.pushPose();
		poseStack.translate(position.x, position.y, position.z);
		poseStack.translate(0, entity.getBbHeight() + 0.5f, 0);
		poseStack.translate(0, TESConstants.CONFIG.inWorldHudManualVerticalOffset(), 0);

		TESClientUtil.positionFacingCamera(poseStack);
		poseStack.mulPose(Vector3f.ZP.rotationDegrees(180));
		poseStack.scale(0.02f, 0.02f, 0.02f);
		RenderSystem.enableBlend();
		RenderSystem.enableDepthTest();
		RenderSystem.setShaderColor(1, 1, 1, hudOpacity);

		for (TESHudElement element : INVERSE_ELEMENTS) {
			int offset = element.render(poseStack, mc, partialTick, entity, hudOpacity, true);

			if (offset > 0)
				poseStack.translate(0, -(2 + offset), 0);
		}

		RenderSystem.setShaderColor(1, 1, 1, 1);
		poseStack.popPose();
	}

	public static void pickNewEntity(float partialTick) {
		Minecraft mc = Minecraft.getInstance();

		if (mc.crosshairPickEntity != null) {
			LivingEntity target = TESConstants.UTILS.getLivingEntityIfPossible(mc.crosshairPickEntity);

			if (TESUtil.isVisibleToPlayer(target, TESClientUtil.getClientPlayer()))
				TESHud.setTargetEntity(target);
		}
		else {
			double targetingRange = TESAPI.getConfig().getHudTargetDistance();
			Entity cameraEntity = mc.getCameraEntity();
			Vec3 cameraPos = cameraEntity.getEyePosition(partialTick);
			Vec3 cameraView = cameraEntity.getViewVector(partialTick);
			Vec3 rayEnd = cameraPos.add(cameraView.multiply(targetingRange, targetingRange, targetingRange));
			AABB hitBounds = cameraEntity.getBoundingBox().expandTowards(cameraView.scale(targetingRange)).inflate(1, 1, 1);
			EntityHitResult hitResult = ProjectileUtil.getEntityHitResult(cameraEntity, cameraPos, rayEnd, hitBounds, entity -> !entity.isSpectator() && entity.isPickable(), targetingRange * targetingRange);

			if (hitResult == null)
				return;

			LivingEntity target = TESConstants.UTILS.getLivingEntityIfPossible(hitResult.getEntity());

			if (!TESUtil.isVisibleToPlayer(target, TESClientUtil.getClientPlayer()))
				return;

			double entityHitClipDistanceSqr = hitResult.getLocation().distanceToSqr(cameraPos);
			targetingRange = Math.sqrt(entityHitClipDistanceSqr);
			rayEnd = cameraPos.add(cameraView.multiply(targetingRange, targetingRange, targetingRange));

			HitResult blockHitResult = cameraEntity.level.clip(new ClipContext(cameraPos, rayEnd, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, cameraEntity));

			if (blockHitResult == null || blockHitResult.getType() == HitResult.Type.MISS || blockHitResult.getLocation().distanceToSqr(cameraPos) > entityHitClipDistanceSqr)
				TESHud.setTargetEntity(target);
		}
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
		COMBINED;
	}
}

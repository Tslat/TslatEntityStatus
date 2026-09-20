package net.tslat.tslatentitystatus.core.state;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.tslat.tslatentitystatus.api.TESConfig;
import net.tslat.tslatentitystatus.api.TESConstants;
import net.tslat.tslatentitystatus.api.client.object.TESParticle;
import net.tslat.tslatentitystatus.api.client.util.TESClientUtil;
import net.tslat.tslatentitystatus.core.particle.TESParticleManager;
import net.tslat.tslatentitystatus.core.particle.type.ComponentParticle;
import net.tslat.tslatentitystatus.core.particle.type.DamageParticle;
import net.tslat.tslatentitystatus.core.particle.type.HealParticle;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Set;

/// Holder class for entity state data
public class TESEntityState {
	protected Set<Holder<MobEffect>> effects = Collections.emptySet();

	protected float currentHealth;
	protected float lastHealth;
	protected float lastTransitionHealth;
	protected long lastTransitionTime;
	protected @Nullable WeakReference<DamageSource> lastDamageSource;

	public TESEntityState(LivingEntity entity) {
		this.currentHealth = entity.getHealth();
		this.lastHealth = this.currentHealth;

		if (TESConstants.getConfig().isSyncingEffects() && TESConstants.PLATFORM.hasServerMod() && entity.level().isClientSide())
			TESConstants.NETWORKING.requestEffectsSync(entity.getId());
	}

	public float getHealth() {
		return this.currentHealth;
	}

	public float getLastHealth() {
		return this.lastHealth;
	}

	public float getLastTransitionHealth() {
		return this.lastTransitionHealth;
	}

	public long getLastTransitionTime() {
		return this.lastTransitionTime;
	}

	public Set<Holder<MobEffect>> getEffects() {
		return this.effects;
	}

	/// Tick this state's updates and handling
	public void tick(LivingEntity entity) {
		if (entity.is(TESConstants.NO_TES_HANDLING))
			return;

		this.currentHealth = Math.min(entity.getHealth(), entity.getMaxHealth());

		if (this.currentHealth != this.lastHealth && entity.tickCount > 2) {
			handleHealthChange(entity);

			DamageSource lastDamage = entity.getLastDamageSource();
			this.lastDamageSource = lastDamage == null ? null : new WeakReference<>(lastDamage);
		}

		this.lastHealth = currentHealth;

		if (entity.level().getGameTime() - this.lastTransitionTime > 20) {
			if (this.lastTransitionHealth > this.currentHealth) {
				this.lastTransitionHealth -= entity.getMaxHealth() / 30f;
			}
			else {
				this.lastTransitionTime = 0;
				this.lastTransitionHealth = this.currentHealth;
			}
		}
	}

	/// Handle this state's health status and delta for the current tick
	protected void handleHealthChange(LivingEntity entity) {
		final TESConfig config = TESConstants.getConfig();

		if (!config.particlesEnabled())
			return;

		final DamageSource damageSource = entity.getLastDamageSource();
		float healthDelta = this.currentHealth - this.lastHealth;

		if (healthDelta != 0)
			healthDelta = TESParticleManager.handleParticleClaims(entity, healthDelta, TESParticleManager::addParticle,
																  damageSource != null && this.lastDamageSource != null && !this.lastDamageSource.refersTo(damageSource));

		if (healthDelta == 0)
			return;

		TESParticleManager.addParticle(getHealthParticle(entity, entity.getEyePosition().toVector3f(), healthDelta, damageSource, config));
	}

	/// Get the health-change-related particle to spawn for the current tick
	protected TESParticle<?> getHealthParticle(LivingEntity entity, Vector3f position, float healthDelta, @Nullable DamageSource relevantDamageSource, TESConfig config) {
		if (healthDelta < 0) {
			this.lastTransitionTime = entity.level().getGameTime();
			int colour = config.getDamageParticleColour();

			if (this.lastTransitionHealth == 0)
				this.lastTransitionHealth = this.lastHealth;

			if (relevantDamageSource != null && config.teamBasedDamageParticleColours()) {
				if (relevantDamageSource.getEntity() instanceof LivingEntity attacker) {
					int teamColour = attacker.getTeamColor();

					if (teamColour != 0xFFFFFF)
						colour = teamColour;
				}
			}

			if (config.verbalHealthParticles() && this.currentHealth <= 0 && this.lastHealth >= entity.getMaxHealth()) {
				return new ComponentParticle(this, position, TESParticle.Animation.POP_OFF, Component.translatable("config.tes.particle.verbal.instakill")
						.setStyle(Style.EMPTY.withColor(colour)));
			}
			else {
				return new DamageParticle(this, position, -healthDelta).withColour(colour);
			}
		}
		else {
			if (config.verbalHealthParticles() && this.currentHealth >= entity.getMaxHealth() && this.lastHealth <= entity.getMaxHealth() * 0.05f) {
				return new ComponentParticle(this, position, TESParticle.Animation.RISE, Component.translatable("config.tes.particle.verbal.fullHeal")
						.setStyle(Style.EMPTY.withColor(config.getHealParticleColour())));
			}
			else {
				return new HealParticle(this, position, healthDelta);
			}
		}
	}

	/// Whether this entity's TES in-world HUD should be rendered for this entity
	public boolean shouldRenderInWorldHud(Entity entity) {
		if (entity.is(TESConstants.NO_TES_HANDLING))
			return false;

		final TESConfig config = TESConstants.getConfig();
		final Player pl = TESClientUtil.getClientPlayer();
		
		if (!config.inWorldHudForSelf() && (entity == pl || entity.getPassengers().contains(pl)))
			return false;
		
		return TESClientUtil.getClosestEntityPosition(entity).distanceToSqr(TESClientUtil.getCameraPosition()) <= Mth.square(config.getEntityTrackingDistance());
	}

	/// Add and/or remove potion effects from this state's tracking
	public void modifyEffects(Set<Holder<MobEffect>> idsToAdd, Set<Holder<MobEffect>> idsToRemove) {
		if ((Object)this.effects == Collections.emptySet()) {
			this.effects = new ObjectOpenHashSet<>(idsToAdd);
		}
		else {
			this.effects.addAll(idsToAdd);
		}

		if (!this.effects.isEmpty())
			this.effects.removeAll(idsToRemove);
	}
}

package net.tslat.tes.core.state;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.tslat.tes.api.TESAPI;
import net.tslat.tes.api.TESConstants;
import net.tslat.tes.api.object.TESParticle;
import net.tslat.tes.core.particle.TESParticleManager;
import net.tslat.tes.core.particle.type.ComponentParticle;
import net.tslat.tes.core.particle.type.DamageParticle;
import net.tslat.tes.core.particle.type.HealParticle;
import org.joml.Vector3f;

import java.lang.ref.WeakReference;
import java.util.Optional;
import java.util.Set;

/**
 * Holder class for entity state data.<br>
 * Each rendered entity is assigned one the first time it is rendered.
 */
public class EntityState {
	protected final WeakReference<LivingEntity> entity;

	protected Set<Holder<MobEffect>> effects;

	protected float currentHealth;
	protected float lastHealth;
	protected boolean wasPreHurt;
	protected float lastTransitionHealth;
	protected long lastTransitionTime;
	protected WeakReference<DamageSource> lastDamageSource;
	protected int lastRenderTick;

	public EntityState(LivingEntity entity) {
		this.entity = new WeakReference<>(entity);
		this.currentHealth = entity.getHealth();
		this.lastHealth = this.currentHealth;
		this.wasPreHurt = this.currentHealth < entity.getMaxHealth();
		this.lastRenderTick = entity.tickCount;

		if (TESConstants.CONFIG.isSyncingEffects() && entity.level().isClientSide())
			TESConstants.NETWORKING.requestEffectsSync(entity.getId());
	}

	public Optional<LivingEntity> getEntity() {
		return Optional.ofNullable(this.entity.get());
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

	public float getLastTransitionTime() {
		return this.lastTransitionTime;
	}

	public Set<Holder<MobEffect>> getEffects() {
		return this.effects == null ? Set.of() : this.effects;
	}

	public void modifyEffects(Set<Holder<MobEffect>> ids, Set<Holder<MobEffect>> idsToRemove) {
		if (this.effects == null) {
			this.effects = new ObjectOpenHashSet<>(ids);
		}
		else {
			this.effects.addAll(ids);
		}

		this.effects.removeAll(idsToRemove);
	}

	public void markActive() {
		this.lastRenderTick = getEntity().map(entity -> entity.tickCount).orElse(0);
	}
	
	public boolean isValid() {
        return getEntity()
                .map(entity ->
                             !entity.isRemoved() &&
                             entity.level() == Minecraft.getInstance().level &&
                             (this.lastRenderTick < 0 || this.lastRenderTick >= entity.tickCount - 200))
                .orElse(false);
	}

	public void tick() {
		LivingEntity entity = getEntity().orElse(null);

		if (entity == null)
			return;

		this.currentHealth = Math.min(entity.getHealth(), entity.getMaxHealth());

		if (this.currentHealth != this.lastHealth && entity.tickCount > 2) {
			handleHealthChange();

			this.lastDamageSource = new WeakReference<>(entity.getLastDamageSource());
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

	protected void handleHealthChange() {
        final LivingEntity entity = getEntity().orElse(null);

		if (entity != null && TESAPI.getConfig().particlesEnabled()) {
			TESParticle<?> particle;
			float healthDelta = this.currentHealth - this.lastHealth;
			boolean damageSourceAccurate = entity.getLastDamageSource() != null && this.lastDamageSource != null && this.lastDamageSource.get() != entity.getLastDamageSource();

			if (healthDelta != 0)
				healthDelta = TESParticleManager.handleParticleClaims(this, healthDelta, TESParticleManager::addParticle, damageSourceAccurate);

			if (healthDelta == 0)
				return;

			Vector3f particlePos = new Vector3f((float)entity.getX(), (float)entity.getEyeY(), (float)entity.getZ());

			if (healthDelta < 0) {
				this.lastTransitionTime = entity.level().getGameTime();
				int colour = TESAPI.getConfig().getDamageParticleColour();

				if (this.lastTransitionHealth == 0)
					this.lastTransitionHealth = this.lastHealth;

				if (damageSourceAccurate && TESAPI.getConfig().teamBasedDamageParticleColours()) {
					if (entity.getLastDamageSource().getEntity() instanceof LivingEntity attacker) {
						int teamColour = attacker.getTeamColor();

						if (teamColour != 0xFFFFFF)
							colour = teamColour;
					}
				}

				if (TESAPI.getConfig().verbalHealthParticles() && this.currentHealth <= 0 && !this.wasPreHurt && this.lastHealth >= entity.getMaxHealth()) {
					particle = new ComponentParticle(this, particlePos, TESParticle.Animation.POP_OFF, Component.translatable("config.tes.particle.verbal.instakill").setStyle(Style.EMPTY.withColor(colour)));
				}
				else {
					particle = new DamageParticle(this, particlePos, -healthDelta).withColour(colour);
				}
			}
			else {
				if (TESAPI.getConfig().verbalHealthParticles() && this.currentHealth >= entity.getMaxHealth() && this.lastHealth <= entity.getMaxHealth() * 0.05f) {
					particle = new ComponentParticle(this, particlePos, TESParticle.Animation.RISE, Component.translatable("config.tes.particle.verbal.fullHeal").setStyle(Style.EMPTY.withColor(TESAPI.getConfig().getHealParticleColour())));
				}
				else {
					particle = new HealParticle(this, particlePos, healthDelta);
				}
			}

			this.wasPreHurt = false;
			TESParticleManager.addParticle(particle);
		}
	}
}

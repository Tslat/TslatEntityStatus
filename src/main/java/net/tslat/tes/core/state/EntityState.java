package net.tslat.tes.core.state;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;
import net.tslat.tes.api.TESAPI;
import net.tslat.tes.api.TESConstants;
import net.tslat.tes.api.TESParticle;
import net.tslat.tes.core.particle.TESParticleManager;
import net.tslat.tes.core.particle.type.ComponentParticle;
import net.tslat.tes.core.particle.type.DamageParticle;
import net.tslat.tes.core.particle.type.HealParticle;

import java.util.Collections;
import java.util.Set;

/**
 * Holder class for entity state data.<br>
 * Each rendered entity is assigned one the first time it is rendered.
 */
public class EntityState {
	protected final LivingEntity entity;

	protected Set<ResourceLocation> effects;

	protected float currentHealth;
	protected float lastHealth;
	protected float lastTransitionHealth;
	protected long lastTransitionTime;

	public EntityState(LivingEntity entity) {
		this.entity = entity;
		this.currentHealth = entity.getHealth();
		this.lastHealth = this.currentHealth;

		TESConstants.NETWORKING.requestEffectsSync(this.entity.getId());
	}

	public LivingEntity getEntity() {
		return this.entity;
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

	public Set<ResourceLocation> getEffects() {
		return this.effects == null ? Collections.emptySet() : this.effects;
	}

	public void modifyEffects(Set<ResourceLocation> ids, Set<ResourceLocation> idsToRemove) {
		if (this.effects == null) {
			this.effects = new ObjectOpenHashSet<>(ids);
		}
		else {
			this.effects.addAll(ids);
		}

		this.effects.removeAll(idsToRemove);
	}
	
	public boolean isValid() {
		return this.entity != null && this.entity.isAlive() && this.entity.level == Minecraft.getInstance().level;
	}

	public void tick() {
		this.currentHealth = Math.min(this.entity.getHealth(), this.entity.getMaxHealth());

		if (this.currentHealth != this.lastHealth && this.entity.tickCount > 2)
			handleHealthChange();

		this.lastHealth = currentHealth;

		if (this.entity.level.getGameTime() - this.lastTransitionTime > 20) {
			if (this.lastTransitionHealth > this.currentHealth) {
				this.lastTransitionHealth -= this.entity.getMaxHealth() / 30f;
			}
			else {
				this.lastTransitionTime = 0;
				this.lastTransitionHealth = this.currentHealth;
			}
		}
	}

	protected void handleHealthChange() {
		if (TESAPI.getConfig().particlesEnabled()) {
			TESParticle<?> particle;
			float healthDelta = this.currentHealth - this.lastHealth;

			if (healthDelta != 0)
				healthDelta = TESParticleManager.handleParticleClaims(this, healthDelta, TESParticleManager::addParticle);

			if (healthDelta == 0)
				return;

			Vector3f particlePos = new Vector3f((float)this.entity.getX(), (float)this.entity.getEyeY() + 0.5f, (float)this.entity.getZ());

			if (healthDelta < 0) {
				this.lastTransitionTime = this.entity.level.getGameTime();

				if (this.lastTransitionHealth == 0)
					this.lastTransitionHealth = this.lastHealth;

				if (TESAPI.getConfig().verbalHealthParticles() && this.currentHealth <= 0 && this.lastHealth >= this.entity.getMaxHealth()) {
					particle = new ComponentParticle(this, particlePos, TESParticle.Animation.POP_OFF, new TranslationTextComponent("config.tes.particle.verbal.instakill").setStyle(Style.EMPTY.withColor(Color.fromRgb(TESAPI.getConfig().getDamageParticleColour()))));
				}
				else {
					particle = new DamageParticle(this, particlePos, healthDelta * -1);
				}
			}
			else {
				if (TESAPI.getConfig().verbalHealthParticles() && this.currentHealth >= this.entity.getMaxHealth() && this.lastHealth <= this.entity.getMaxHealth() * 0.05f) {
					particle = new ComponentParticle(this, particlePos, TESParticle.Animation.RISE, new TranslationTextComponent("config.tes.particle.verbal.fullHeal").setStyle(Style.EMPTY.withColor(Color.fromRgb(TESAPI.getConfig().getHealParticleColour()))));
				}
				else {
					particle = new HealParticle(this, particlePos, healthDelta);
				}
			}

			TESParticleManager.addParticle(particle);
		}
	}
}

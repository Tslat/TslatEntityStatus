package net.tslat.tes.core.state;

import com.mojang.math.Vector3f;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.tslat.tes.api.TESAPI;
import net.tslat.tes.api.TESConstants;
import net.tslat.tes.api.TESParticle;
import net.tslat.tes.api.util.TESClientUtil;
import net.tslat.tes.core.particle.TESParticleManager;
import net.tslat.tes.core.particle.type.DamageParticle;
import net.tslat.tes.core.particle.type.HealParticle;
import net.tslat.tes.core.particle.type.TextParticle;

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
		return this.effects == null ? Set.of() : this.effects;
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

		if (this.currentHealth != this.lastHealth)
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
			Vector3f particlePos = new Vector3f((float)this.entity.getX(), (float)this.entity.getEyeY(), (float)this.entity.getZ());
			TextParticle particle;

			if (this.currentHealth < this.lastHealth) {
				this.lastTransitionTime = this.entity.level.getGameTime();

				if (this.lastTransitionHealth == 0)
					this.lastTransitionHealth = this.lastHealth;

				if (TESAPI.getConfig().verbalHealthParticles() && this.currentHealth <= 0 && this.lastHealth >= this.entity.getMaxHealth()) {
					particle = new TextParticle(this, particlePos, TESParticle.Animation.POP_OFF, TESClientUtil.translateKey("config.tes.particle.verbal.instakill"));

					particle.setColour(TESAPI.getConfig().getDamageParticleColour());
				}
				else {
					particle = new DamageParticle(this, particlePos, this.lastHealth - this.currentHealth);
				}
			}
			else {
				if (TESAPI.getConfig().verbalHealthParticles() && this.currentHealth >= this.entity.getMaxHealth() && this.lastHealth <= this.entity.getMaxHealth() * 0.05f) {
					particle = new TextParticle(this, particlePos, TESParticle.Animation.RISE, TESClientUtil.translateKey("config.tes.particle.verbal.fullHeal"));

					particle.setColour(TESAPI.getConfig().getHealParticleColour());
				}
				else {
					particle = new HealParticle(this, particlePos, this.currentHealth - this.lastHealth);
				}
			}

			TESParticleManager.addParticle(particle);
		}
	}
}

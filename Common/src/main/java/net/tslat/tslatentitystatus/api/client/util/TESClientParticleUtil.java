package net.tslat.tslatentitystatus.api.client.util;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.tslat.tslatentitystatus.api.TESConstants;
import net.tslat.tslatentitystatus.api.client.object.TESParticle;
import net.tslat.tslatentitystatus.core.particle.TESParticleManager;
import net.tslat.tslatentitystatus.core.particle.type.ComponentParticle;
import net.tslat.tslatentitystatus.core.particle.type.NumericParticle;
import net.tslat.tslatentitystatus.core.state.TESEntityState;

/// Helper class for handling client-side general particle functionality to separate it out from common code
public final class TESClientParticleUtil {
    /// Add a [TESParticle] to the [TESParticleManager]
    ///
    /// This method should only be called <u><b>client-side</b></u>
    ///
    /// The particle itself is responsible for rendering and its own validity/lifespan
    ///
    /// @param particle The particle to add
    public static void addTESParticle(TESParticle<?> particle) {
        if (!TESConstants.PLATFORM.isPhysicalClient()) {
            if (TESConstants.PLATFORM.isDevelopmentEnvironment())
                throw new IllegalStateException("Attempted to add a TES particle on the server side!");

            TESConstants.LOGGER.get().error("A mod attempted to add a TES particle on the server side, skipping!");

            return;
        }

        TESParticleManager.addParticle(particle);
    }

    /// Add a [TESParticle] for the given position
    ///
    /// @param position The position the particle should appear at
    /// @param contents The contents of the particle. If using a numeric value, use one of the double-based methods
    public static void addTESParticle(Vec3 position, Component contents) {
        addTESParticle(new ComponentParticle(null, position.toVector3f(), contents));
    }

    /// Add a [NumericParticle] for the given position
    ///
    /// @param position The position the particle should appear at
    /// @param value    The value of the particle
    /// @param colour   The text colour of the particle
    public static void addTESParticle(Vec3 position, double value, int colour) {
        addTESParticle(new NumericParticle(null, position.toVector3f(), value).withColour(colour));
    }

    /// Add a [TESParticle] for the given entity
    ///
    /// @param targetEntity The entity the particle should appear on
    /// @param contents     The contents of the particle. If using a numeric value, use one of the double-based methods
    public static void addTESParticle(LivingEntity targetEntity, Component contents) {
        addTESParticle(targetEntity, targetEntity.getEyePosition(), contents);
    }

    /// Add a [TESParticle] for the given entity at a specific position
    ///
    /// @param targetEntity The entity the particle should appear on
    /// @param position     The position in the world to spawn the particle at
    /// @param contents     The contents of the particle. If using a numeric value, use one of the double-based methods
    public static void addTESParticle(LivingEntity targetEntity, Vec3 position, Component contents) {
        final TESEntityState entityState = targetEntity.tslatentitystatus$getEntityState();

        if (entityState != null)
            addTESParticle(new ComponentParticle(entityState, position.toVector3f(), contents));
    }

    /// Add a [TESParticle] for the given entity
    ///
    /// @param targetEntity The entity the particle should appear on
    /// @param value        The value of the particle
    /// @param colour       The text colour of the particle
    public static void addTESParticle(LivingEntity targetEntity, double value, int colour) {
        addTESParticle(targetEntity, targetEntity.getEyePosition(), value, colour);
    }

    /// Add a [TESParticle] for the given entity at a specific position
    ///
    /// @param targetEntity The entity the particle should appear on
    /// @param position     The position in the world to spawn the particle at
    /// @param value        The value of the particle
    /// @param colour       The text colour of the particle
    public static void addTESParticle(LivingEntity targetEntity, Vec3 position, double value, int colour) {
        final TESEntityState entityState = targetEntity.tslatentitystatus$getEntityState();

        if (entityState != null)
            addTESParticle(new NumericParticle(entityState, position.toVector3f(), value).withColour(colour));
    }

    private TESClientParticleUtil() {}
}

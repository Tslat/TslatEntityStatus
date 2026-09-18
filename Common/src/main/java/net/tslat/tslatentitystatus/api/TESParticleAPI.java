package net.tslat.tslatentitystatus.api;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.tslat.tslatentitystatus.api.client.object.TESParticle;
import net.tslat.tslatentitystatus.api.client.util.TESClientParticleUtil;
import net.tslat.tslatentitystatus.api.client.util.TESClientUtil;
import net.tslat.tslatentitystatus.api.common.util.TESUtil;
import net.tslat.tslatentitystatus.core.particle.TESParticleClaimant;
import net.tslat.tslatentitystatus.core.particle.TESParticleManager;
import net.tslat.tslatentitystatus.core.particle.type.NumericParticle;
import net.tslat.tslatentitystatus.core.platform.TESPlatform;

import java.util.Optional;

/// Public-facing API for `TES` particle-related functionality<br/>
/// Typically used for adding `TES` particle instances to be rendered
///
/// @see TESConstants
/// @see TESRegistrationAPI
/// @see TESPlatform
/// @see TESUtil
/// @see TESClientUtil
public final class TESParticleAPI {
	/// Submit a particle claim to the particle manager for the next/upcoming tick
	///
	/// If the target entity has a health change next tick, your claimant will be called with the relevant info
	///
	/// @param id 				The id of the [TESParticleClaimant] responsible for handling the claim
	/// @param targetEntity 	The entity the particle claim is for
	/// @param additionalData 	Optional additional data passed back to the claimant at the time of the claim
	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static void submitParticleClaim(Identifier id, LivingEntity targetEntity, Optional<CompoundTag> additionalData) {
		if (!targetEntity.level().isClientSide()) {
			TESConstants.NETWORKING.sendParticleClaim(id, targetEntity, additionalData.orElse(null));
		}
		else {
			TESParticleManager.addParticleClaim(targetEntity.getId(), id, additionalData.orElse(null));
		}
	}
	
	/// Add a [TESParticle] for the given position
	///
	/// @param level 	The level the particle is in
	/// @param position The position the particle should appear at
	/// @param contents The contents of the particle. If using a numeric value, use one of the double-based methods
	public static void addTESParticle(Level level, Vec3 position, Component contents) {
		if (level instanceof ServerLevel serverLevel) {
			TESConstants.NETWORKING.sendParticle(serverLevel, position, contents);
		}
		else {
			TESClientParticleUtil.addTESParticle(position, contents);
		}
	}
	
	/// Add a [numeric TESParticle][NumericParticle] for the given position
	///
	/// @param level 	The level the particle is in
	/// @param position The position the particle should appear at
	/// @param value 	The value of the particle
	/// @param colour 	The text colour of the particle
	public static void addTESParticle(Level level, Vec3 position, double value, int colour) {
		if (level instanceof ServerLevel serverLevel) {
			TESConstants.NETWORKING.sendParticle(serverLevel, position, value, colour);
		}
		else {
			TESClientParticleUtil.addTESParticle(position, value, colour);
		}
	}
	
	/// Add a [TESParticle] for the given entity
	///
	/// @param targetEntity The entity the particle should appear on
	/// @param contents 	The contents of the particle. If using a numeric value, use one of the double-based methods
	public static void addTESParticle(LivingEntity targetEntity, Component contents) {
		if (!targetEntity.level().isClientSide()) {
			TESConstants.NETWORKING.sendParticle(targetEntity, contents);
		}
		else {
			TESClientParticleUtil.addTESParticle(targetEntity, contents);
		}
	}
	
	/// Add a [TESParticle] for the given entity
	///
	/// @param targetEntity The entity the particle should appear on
	/// @param value 		The value of the particle
	/// @param colour 		The text colour of the particle
	public static void addTESParticle(LivingEntity targetEntity, double value, int colour) {
		if (!targetEntity.level().isClientSide()) {
			TESConstants.NETWORKING.sendParticle(targetEntity, value, colour);
		}
		else {
			TESClientParticleUtil.addTESParticle(targetEntity, value, colour);
		}
	}

	private TESParticleAPI() {}
}

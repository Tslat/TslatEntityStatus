package net.tslat.tslatentitystatus.api;

import net.minecraft.resources.Identifier;
import net.minecraft.world.damagesource.DamageSource;
import net.tslat.tslatentitystatus.api.client.object.TESHudElement;
import net.tslat.tslatentitystatus.api.client.object.TESParticle;
import net.tslat.tslatentitystatus.api.client.util.TESClientUtil;
import net.tslat.tslatentitystatus.api.common.util.TESUtil;
import net.tslat.tslatentitystatus.core.client.hud.TESHud;
import net.tslat.tslatentitystatus.core.particle.TESParticleClaimant;
import net.tslat.tslatentitystatus.core.particle.TESParticleManager;
import net.tslat.tslatentitystatus.core.particle.TESParticleSourceHandler;
import net.tslat.tslatentitystatus.core.platform.TESPlatform;

/// Public-facing API for `TES` registration-related functionality
///
/// @see TESConstants
/// @see TESParticleAPI
/// @see TESPlatform
/// @see TESUtil
/// @see TESClientUtil
public final class TESRegistrationAPI {
	/// Add a [HUD element][TESHudElement] to the [TES HUD manager][TESHud]
	///
	/// The element will be called to render at each render frame, with its position pre-adjusted
	///
	/// Generally, HUD elements should be added at mod construct
	///
	/// @param id The id of the element to add
	/// @param element The element instance to add to the HUD
	public static void addTESHudElement(Identifier id, TESHudElement element) {
		TESHud.addHudElement(id.toString(), element);
	}
	
	/// Remove an existing [HUD Element][TESHudElement] from the [TES HUD manager][TESHud], if present
	///
	/// @param id The id of the element to remove
	/// @return true if the element was present
	public static boolean removeTESHudElement(Identifier id) {
		return TESHud.removeHudElement(id.toString());
	}
	
	/// Register a [TESParticleClaimant] with TES for handling custom claims
	///
	/// This allows for overriding damage particles dynamically or doing other similar things
	///
	/// @param id The id of the claimant to register
	/// @param claimant The claimant instance
	public static void registerParticleClaimant(Identifier id, TESParticleClaimant claimant) {
		TESParticleManager.registerParticleClaimant(id, claimant);
	}
	
	/// Register a [TESParticleSourceHandler][TESParticleSourceHandler] with TES for custom handling of damage-based [TESParticles][TESParticle] predicated by their [DamageSource]
	///
	/// This can be used to more reliably special-handle damage particles for specific `DamageSources` (such as freezing damage, fire damage, etc.)
	public static void registerParticleSourceHandler(TESParticleSourceHandler handler) {
		TESParticleManager.registerParticleSourceHandler(handler);
	}
	
	private TESRegistrationAPI() {}
}

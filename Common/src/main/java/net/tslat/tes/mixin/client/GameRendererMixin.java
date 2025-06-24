package net.tslat.tes.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.tslat.tes.core.hud.TESHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
	@Inject(method = "pick(F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;pop()V"))
	private void tes$pickHudEntity(float partialTick, CallbackInfo callback) {
		TESHud.pickNewEntity(partialTick);
	}

	@WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;render(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V"))
	private void tes$renderHud(Gui gui, GuiGraphics guiGraphics, DeltaTracker deltaTracker, Operation<Void> original) {
		original.call(gui, guiGraphics, deltaTracker);
		TESHud.renderForHud(guiGraphics, Minecraft.getInstance(), deltaTracker);
	}
}

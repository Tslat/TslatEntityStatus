package net.tslat.tslatentitystatus.mixin.client;

import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.tslat.tslatentitystatus.api.client.object.renderhelper.CustomRenderSubmitsExtension;
import org.spongepowered.asm.mixin.Mixin;

/// Quack
@Mixin(OrderedSubmitNodeCollector.class)
public interface OrderedSubmitNodeCollectorMixin extends CustomRenderSubmitsExtension {
}

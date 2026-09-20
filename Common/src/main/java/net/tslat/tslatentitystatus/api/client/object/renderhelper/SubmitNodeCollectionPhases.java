package net.tslat.tslatentitystatus.api.client.object.renderhelper;

import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.feature.phase.FeatureRenderPhase;
import net.minecraft.client.renderer.feature.phase.TranslucentFeatureRenderPhase;

/// Duck-interface for extraction custom [FeatureRenderPhase]s from a [SubmitNodeCollection] instance
public interface SubmitNodeCollectionPhases {
    default TranslucentFeatureRenderPhase tslatentitystatus$getCustomTexts() {
        throw new IllegalStateException("SubmitNodeCollectionPhases isn't quacking like a duck!");
    }
}

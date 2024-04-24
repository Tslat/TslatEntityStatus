package net.tslat.tes.core.networking.packet;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.tslat.tes.api.TESConstants;
import net.tslat.tes.core.state.EntityState;
import net.tslat.tes.core.state.TESEntityTracking;

import java.util.Set;
import java.util.function.Consumer;

public record SyncEffectsPacket(int entityId, Set<Holder<MobEffect>> idsToAdd, Set<Holder<MobEffect>> idsToRemove) implements MultiloaderPacket {
	public static final CustomPacketPayload.Type<SyncEffectsPacket> TYPE = new Type<>(new ResourceLocation(TESConstants.MOD_ID, "sync_effects"));
	public static final StreamCodec<RegistryFriendlyByteBuf, SyncEffectsPacket> CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT,
			SyncEffectsPacket::entityId,
			ByteBufCodecs.collection(ObjectOpenHashSet::new, ByteBufCodecs.holderRegistry(Registries.MOB_EFFECT)),
			SyncEffectsPacket::idsToAdd,
			ByteBufCodecs.collection(ObjectOpenHashSet::new, ByteBufCodecs.holderRegistry(Registries.MOB_EFFECT)),
			SyncEffectsPacket::idsToRemove,
			SyncEffectsPacket::new);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	@Override
	public void receiveMessage(Player sender, Consumer<Runnable> workQueue) {
		workQueue.accept(() -> {
			EntityState state = TESEntityTracking.getStateForEntityId(this.entityId);

			if (state != null)
				state.modifyEffects(this.idsToAdd, this.idsToRemove);
		});
	}
}

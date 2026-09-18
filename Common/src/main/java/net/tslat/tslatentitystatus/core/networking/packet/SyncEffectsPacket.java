package net.tslat.tslatentitystatus.core.networking.packet;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.tslat.tslatentitystatus.api.TESConstants;
import net.tslat.tslatentitystatus.api.client.util.TESClientUtil;
import net.tslat.tslatentitystatus.core.networking.packet.base.MultiloaderPacket;
import net.tslat.tslatentitystatus.core.state.TESEntityState;

import java.util.Set;
import java.util.function.Consumer;

/// Clientbound `TES` particle to sync the current [MobEffect] information to the client for a specific [Entity]
public record SyncEffectsPacket(int entityId, Set<Holder<MobEffect>> idsToAdd, Set<Holder<MobEffect>> idsToRemove) implements MultiloaderPacket {
	public static final CustomPacketPayload.Type<SyncEffectsPacket> TYPE = new Type<>(TESConstants.id("sync_effects"));
	public static final StreamCodec<RegistryFriendlyByteBuf, SyncEffectsPacket> CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, SyncEffectsPacket::entityId,
			ByteBufCodecs.collection(SyncEffectsPacket::optionalSet, ByteBufCodecs.holderRegistry(Registries.MOB_EFFECT)), SyncEffectsPacket::idsToAdd,
			ByteBufCodecs.collection(SyncEffectsPacket::optionalSet, ByteBufCodecs.holderRegistry(Registries.MOB_EFFECT)), SyncEffectsPacket::idsToRemove,
			SyncEffectsPacket::new);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	@Override
	public void receiveMessage(Player sender, Consumer<Runnable> workQueue) {
		workQueue.accept(() -> {
			final TESEntityState state = TESClientUtil.getTESEntityState(this.entityId);

			if (state != null)
				state.modifyEffects(this.idsToAdd, this.idsToRemove);
		});
	}

	private static Set<Holder<MobEffect>> optionalSet(int size) {
		return size == 0 ? Set.of() : new ObjectOpenHashSet<>(size);
	}
}

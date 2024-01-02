package net.tslat.tes.core.networking.packet;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.tslat.tes.api.TESConstants;
import net.tslat.tes.core.state.EntityState;
import net.tslat.tes.core.state.TESEntityTracking;

import java.util.Set;
import java.util.function.Consumer;

public record SyncEffectsPacket(int entityId, Set<ResourceLocation> idsToAdd, Set<ResourceLocation> idsToRemove) implements MultiloaderPacket {
	public static final ResourceLocation ID = new ResourceLocation(TESConstants.MOD_ID, "sync_effects");

	@Override
	public ResourceLocation id() {
		return ID;
	}

	@Override
	public void write(final FriendlyByteBuf buf) {
		buf.writeVarInt(this.entityId);
		buf.writeCollection(this.idsToAdd, FriendlyByteBuf::writeResourceLocation);
		buf.writeCollection(this.idsToRemove, FriendlyByteBuf::writeResourceLocation);
	}

	public static SyncEffectsPacket decode(final FriendlyByteBuf buf) {
		return new SyncEffectsPacket(
				buf.readVarInt(),
				buf.readCollection(ObjectOpenHashSet::new, FriendlyByteBuf::readResourceLocation),
				buf.readCollection(ObjectOpenHashSet::new, FriendlyByteBuf::readResourceLocation));
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

package net.tslat.tes.networking;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.tslat.tes.api.TESAPI;
import net.tslat.tes.core.state.EntityState;
import net.tslat.tes.core.state.TESEntityTracking;

import java.util.Set;

public class SyncEffectsPacket {
	private final int entityId;
	private final Set<ResourceLocation> idsToAdd;
	private final Set<ResourceLocation> idsToRemove;

	public SyncEffectsPacket(int entityId, Set<ResourceLocation> idsToAdd, Set<ResourceLocation> idsToRemove) {
		this.entityId = entityId;
		this.idsToAdd = idsToAdd;
		this.idsToRemove = idsToRemove;
	}

	public void encode(final FriendlyByteBuf buf) {
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

	public void handleMessage(CustomPayloadEvent.Context context) {
		if (TESAPI.getConfig().isSyncingEffects()) {
			EntityState state = TESEntityTracking.getStateForEntityId(this.entityId);

			if (state != null)
				state.modifyEffects(this.idsToAdd, this.idsToRemove);
		}
	}
}

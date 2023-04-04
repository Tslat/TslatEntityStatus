package net.tslat.tes.networking;

import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import net.tslat.tes.core.state.EntityState;
import net.tslat.tes.core.state.TESEntityTracking;

import java.util.Collection;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class SyncEffectsPacket {
	private final int entityId;
	private final Set<ResourceLocation> idsToAdd;
	private final Set<ResourceLocation> idsToRemove;

	public SyncEffectsPacket(int entityId, Set<ResourceLocation> idsToAdd, Set<ResourceLocation> idsToRemove) {
		this.entityId = entityId;
		this.idsToAdd = idsToAdd;
		this.idsToRemove = idsToRemove;
	}

	public void encode(final PacketBuffer buf) {
		buf.writeVarInt(this.entityId);
		writeCollection(buf, this.idsToAdd, PacketBuffer::writeResourceLocation);
		writeCollection(buf, this.idsToRemove, PacketBuffer::writeResourceLocation);
	}

	public static SyncEffectsPacket decode(final PacketBuffer buf) {
		return new SyncEffectsPacket(
				buf.readVarInt(),
				readCollection(buf, ObjectOpenHashSet::new, PacketBuffer::readResourceLocation),
				readCollection(buf, ObjectOpenHashSet::new, PacketBuffer::readResourceLocation));
	}

	public void handleMessage(Supplier<NetworkEvent.Context> context) {
		context.get().enqueueWork(() -> {
			EntityState state = TESEntityTracking.getStateForEntityId(this.entityId);

			if (state != null)
				state.modifyEffects(this.idsToAdd, this.idsToRemove);
		});

		context.get().setPacketHandled(true);
	}

	private static <E, T extends Collection<E>> T readCollection(PacketBuffer buffer, Int2ObjectFunction<T> collection, Function<PacketBuffer, E> elementFunction) {
		int size = buffer.readVarInt();
		T coll = collection.apply(size);

		for (int i = 0; i < size; i++) {
			coll.add(elementFunction.apply(buffer));
		}

		return coll;
	}

	private static <E, T extends Collection<E>> void writeCollection(PacketBuffer buffer, T collection, BiConsumer<PacketBuffer, E> elementFunction) {
		buffer.writeVarInt(collection.size());

		for (E element : collection) {
			elementFunction.accept(buffer, element);
		}
	}
}

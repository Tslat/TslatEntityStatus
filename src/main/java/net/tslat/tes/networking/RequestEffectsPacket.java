package net.tslat.tes.networking;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.tslat.tes.api.TESConstants;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Supplier;

public class RequestEffectsPacket {
	private final int entityId;

	public RequestEffectsPacket(final int entityId) {
		this.entityId = entityId;
	}

	public void encode(final PacketBuffer buf) {
		buf.writeVarInt(this.entityId);
	}

	public static RequestEffectsPacket decode(final PacketBuffer buf) {
		return new RequestEffectsPacket(buf.readVarInt());
	}

	public void handleMessage(Supplier<NetworkEvent.Context> context) {
		context.get().enqueueWork(() -> {
			Entity entity = context.get().getSender().getLevel().getEntity(this.entityId);

			if (entity instanceof LivingEntity) {
				Collection<EffectInstance> effects = ((LivingEntity)entity).getActiveEffects();
				Set<ResourceLocation> ids = new ObjectOpenHashSet<>(effects.size());

				for (EffectInstance instance : effects) {
					if (instance.isVisible() || instance.showIcon())
						ids.add(ForgeRegistries.POTIONS.getKey(instance.getEffect()));
				}

				TESConstants.NETWORKING.sendEffectsSync(context.get().getSender(), this.entityId, ids, Collections.emptySet());
			}
		});

		context.get().setPacketHandled(true);
	}
}

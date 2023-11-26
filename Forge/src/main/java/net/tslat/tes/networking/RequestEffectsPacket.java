package net.tslat.tes.networking;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.tslat.tes.api.TESConstants;

import java.util.Collection;
import java.util.Set;
import java.util.function.Supplier;

public class RequestEffectsPacket {
	private final int entityId;

	public RequestEffectsPacket(final int entityId) {
		this.entityId = entityId;
	}

	public void encode(final FriendlyByteBuf buf) {
		buf.writeVarInt(this.entityId);
	}

	public static RequestEffectsPacket decode(final FriendlyByteBuf buf) {
		return new RequestEffectsPacket(buf.readVarInt());
	}

	public void handleMessage(Supplier<NetworkEvent.Context> context) {
		context.get().enqueueWork(() -> {
			final ServerPlayer pl = context.get().getSender();

			if (pl == null)
				return;

			final Entity entity = pl.level().getEntity(this.entityId);

			if (entity instanceof LivingEntity livingEntity) {
				Collection<MobEffectInstance> effects = livingEntity.getActiveEffects();
				Set<ResourceLocation> ids = new ObjectOpenHashSet<>(effects.size());

				for (MobEffectInstance instance : effects) {
					if (instance.isVisible() || instance.showIcon())
						ids.add(ForgeRegistries.MOB_EFFECTS.getKey(instance.getEffect()));
				}

				TESConstants.NETWORKING.sendEffectsSync(pl, this.entityId, ids, Set.of());
			}
		});
		context.get().setPacketHandled(true);
	}
}

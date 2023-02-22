package net.tslat.tes.networking;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;
import net.tslat.tes.api.TESConstants;

import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;

public class RequestEffectsPacket {
	public static final ResourceLocation ID = new ResourceLocation(TESConstants.MOD_ID, "request_effects");
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

	public void handleMessage(Consumer<Runnable> queue) {
		queue.accept(() -> {
			Entity entity = context.get().getSender().getLevel().getEntity(this.entityId);

			if (entity instanceof LivingEntity livingEntity) {
				Collection<MobEffectInstance> effects = livingEntity.getActiveEffects();
				Set<ResourceLocation> ids = new ObjectOpenHashSet<>(effects.size());

				for (MobEffectInstance instance : effects) {
					if (instance.isVisible() || instance.showIcon())
						ids.add(ForgeRegistries.MOB_EFFECTS.getKey(instance.getEffect()));
				}

				TESConstants.NETWORKING.sendEffectsSync(context.get().getSender(), this.entityId, ids, Set.of());
			}
		});
	}
}

package net.tslat.tes.core.networking.packet;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.tslat.tes.api.TESAPI;
import net.tslat.tes.api.TESConstants;
import net.tslat.tes.core.particle.TESParticleManager;
import net.tslat.tes.core.particle.type.ComponentParticle;
import net.tslat.tes.core.state.EntityState;
import org.joml.Vector3f;

import java.util.function.Consumer;

public record NewComponentParticlePacket(int entityId, Component contents, Vector3f position) implements MultiloaderPacket {
	public static final CustomPacketPayload.Type<NewComponentParticlePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(TESConstants.MOD_ID, "new_component_particle"));
	public static final StreamCodec<RegistryFriendlyByteBuf, NewComponentParticlePacket> CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT,
			NewComponentParticlePacket::entityId,
			ComponentSerialization.STREAM_CODEC,
			NewComponentParticlePacket::contents,
			ByteBufCodecs.VECTOR3F,
			NewComponentParticlePacket::position,
			NewComponentParticlePacket::new);

	public NewComponentParticlePacket(final LivingEntity entity, final Component contents) {
		this(entity.getId(), contents, new Vector3f((float)entity.getX(), (float)entity.getEyeY(), (float)entity.getZ()));
	}

	public NewComponentParticlePacket(final Vector3f position, final Component contents) {
		this(-1, contents, position);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	@Override
	public void receiveMessage(Player sender, Consumer<Runnable> workQueue) {
		workQueue.accept(() -> {
			if (this.entityId == -1) {
				TESParticleManager.addParticle(new ComponentParticle(null, this.position, this.contents));
			}
			else {
				EntityState entityState = TESAPI.getTESDataForEntity(this.entityId);

				if (entityState != null)
					TESParticleManager.addParticle(new ComponentParticle(entityState, this.position, this.contents));
			}
		});
	}
}

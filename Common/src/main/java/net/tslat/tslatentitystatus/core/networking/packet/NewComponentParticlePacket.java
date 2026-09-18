package net.tslat.tslatentitystatus.core.networking.packet;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.tslat.tslatentitystatus.api.TESConstants;
import net.tslat.tslatentitystatus.api.client.util.TESClientParticleUtil;
import net.tslat.tslatentitystatus.api.client.util.TESClientUtil;
import net.tslat.tslatentitystatus.core.networking.packet.base.MultiloaderPacket;
import net.tslat.tslatentitystatus.core.particle.type.ComponentParticle;
import net.tslat.tslatentitystatus.core.state.TESEntityState;

import java.util.function.Consumer;

/// `TES` packet for adding a new component `TESParticle`
public record NewComponentParticlePacket(int entityId, Component contents, Vec3 position) implements MultiloaderPacket {
	public static final CustomPacketPayload.Type<NewComponentParticlePacket> TYPE = new Type<>(TESConstants.id("new_component_particle"));
	public static final StreamCodec<RegistryFriendlyByteBuf, NewComponentParticlePacket> CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, NewComponentParticlePacket::entityId,
			ComponentSerialization.STREAM_CODEC, NewComponentParticlePacket::contents,
			VEC3_CODEC, NewComponentParticlePacket::position,
			NewComponentParticlePacket::new);

	public NewComponentParticlePacket(final LivingEntity entity, final Component contents) {
		this(entity.getId(), contents, entity.getEyePosition());
	}

	public NewComponentParticlePacket(final Vec3 position, final Component contents) {
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
				TESClientParticleUtil.addTESParticle(new ComponentParticle(null, this.position.toVector3f(), this.contents));
			}
			else {
				final TESEntityState state = TESClientUtil.getTESEntityState(this.entityId);
				
				if (state != null)
					TESClientParticleUtil.addTESParticle(new ComponentParticle(state, this.position.toVector3f(), this.contents));
			}
		});
	}
}

package net.tslat.tes.core.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
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
	public static final ResourceLocation ID = new ResourceLocation(TESConstants.MOD_ID, "new_component_particle");

	public NewComponentParticlePacket(final LivingEntity entity, final Component contents) {
		this(entity.getId(), contents, new Vector3f((float)entity.getX(), (float)entity.getEyeY(), (float)entity.getZ()));
	}

	public NewComponentParticlePacket(final Vector3f position, final Component contents) {
		this(-1, contents, position);
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	@Override
	public void write(final FriendlyByteBuf buf) {
		buf.writeVector3f(this.position);
		buf.writeComponent(this.contents);
		buf.writeVarInt(this.entityId);
	}

	public static NewComponentParticlePacket decode(final FriendlyByteBuf buf) {
		Vector3f position = buf.readVector3f();
		Component contents = buf.readComponent();
		int entityId = buf.readVarInt();

		return entityId == -1 ? new NewComponentParticlePacket(position, contents) : new NewComponentParticlePacket(entityId, contents, position);
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

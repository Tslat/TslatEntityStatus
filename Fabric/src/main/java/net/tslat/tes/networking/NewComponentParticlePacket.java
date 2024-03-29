package net.tslat.tes.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.tslat.tes.api.TESAPI;
import net.tslat.tes.api.TESConstants;
import net.tslat.tes.core.particle.TESParticleManager;
import net.tslat.tes.core.particle.type.ComponentParticle;
import net.tslat.tes.core.state.EntityState;
import org.joml.Vector3f;

import java.util.function.Consumer;

public class NewComponentParticlePacket {
	public static final ResourceLocation ID = new ResourceLocation(TESConstants.MOD_ID, "new_component_particle");

	private final int entityId;
	private final Component contents;
	private final Vector3f position;

	private NewComponentParticlePacket(final int entityId, final Component contents, final Vector3f position) {
		this.entityId = entityId;
		this.contents = contents;
		this.position = position;
	}

	public NewComponentParticlePacket(final LivingEntity entity, final Component contents) {
		this.entityId = entity.getId();
		this.contents = contents;
		this.position = new Vector3f((float)entity.getX(), (float)entity.getEyeY(), (float)entity.getZ());
	}

	public NewComponentParticlePacket(final Vector3f position, final Component contents) {
		this.entityId = -1;
		this.contents = contents;
		this.position = position;
	}

	public void encode(final FriendlyByteBuf buf) {
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

	public void handleMessage(Consumer<Runnable> queue) {
		queue.accept(() -> {
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

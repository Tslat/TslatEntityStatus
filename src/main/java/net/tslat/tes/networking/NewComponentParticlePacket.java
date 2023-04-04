package net.tslat.tes.networking;

import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.network.NetworkEvent;
import net.tslat.tes.api.TESAPI;
import net.tslat.tes.core.particle.TESParticleManager;
import net.tslat.tes.core.particle.type.ComponentParticle;
import net.tslat.tes.core.state.EntityState;

import java.util.function.Supplier;

public class NewComponentParticlePacket {
	private final int entityId;
	private final ITextComponent contents;
	private final Vector3f position;

	private NewComponentParticlePacket(final int entityId, final ITextComponent contents, final Vector3f position) {
		this.entityId = entityId;
		this.contents = contents;
		this.position = position;
	}

	public NewComponentParticlePacket(final LivingEntity entity, final ITextComponent contents) {
		this.entityId = entity.getId();
		this.contents = contents;
		this.position = new Vector3f((float)entity.getX(), (float)entity.getEyeY(), (float)entity.getZ());
	}

	public NewComponentParticlePacket(final Vector3f position, final ITextComponent contents) {
		this.entityId = -1;
		this.contents = contents;
		this.position = position;
	}

	public void encode(final PacketBuffer buf) {
		buf.writeFloat(this.position.x());
		buf.writeFloat(this.position.y());
		buf.writeFloat(this.position.z());
		buf.writeComponent(this.contents);
		buf.writeVarInt(this.entityId);
	}

	public static NewComponentParticlePacket decode(final PacketBuffer buf) {
		Vector3f position = new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat());
		ITextComponent contents = buf.readComponent();
		int entityId = buf.readVarInt();

		return entityId == -1 ? new NewComponentParticlePacket(position, contents) : new NewComponentParticlePacket(entityId, contents, position);
	}

	public void handleMessage(Supplier<NetworkEvent.Context> context) {
		context.get().enqueueWork(() -> {
			if (this.entityId == -1) {
				TESParticleManager.addParticle(new ComponentParticle(null, this.position, this.contents));
			}
			else {
				EntityState entityState = TESAPI.getTESDataForEntity(this.entityId);

				if (entityState != null)
					TESParticleManager.addParticle(new ComponentParticle(entityState, this.position, this.contents));
			}
		});
		context.get().setPacketHandled(true);
	}
}
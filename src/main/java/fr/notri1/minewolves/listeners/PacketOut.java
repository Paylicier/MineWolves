// https://github.com/Minestom/Minestom/issues/2170
// I spent 3h on this :(

package fr.notri1.minewolves.listeners;

import net.minestom.server.entity.Player;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.event.player.PlayerPacketOutEvent;
import net.minestom.server.network.packet.client.play.ClientPlayerPositionAndRotationPacket;
import net.minestom.server.network.packet.client.play.ClientPlayerRotationPacket;
import net.minestom.server.network.packet.server.play.EntityHeadLookPacket;
import net.minestom.server.network.packet.server.play.EntityRotationPacket;
import org.jspecify.annotations.NonNull;

public class PacketOut implements EventListener<PlayerPacketOutEvent> {

    @Override
    public @NonNull Class<PlayerPacketOutEvent> eventType() {
        return PlayerPacketOutEvent.class;
    }

    @Override
    public @NonNull Result run(PlayerPacketOutEvent event) {
        Player player = event.getPlayer();

        if (player.getVehicle() == null) return Result.SUCCESS;

        if(event.getPacket() instanceof EntityRotationPacket headLookPacket) {
            if(headLookPacket.entityId() == player.getEntityId()) {
                event.setCancelled(true);
            }
        }

        return Result.SUCCESS;
    }
}

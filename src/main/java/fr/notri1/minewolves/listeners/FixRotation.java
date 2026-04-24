// https://github.com/Minestom/Minestom/issues/2170
// I spent 3h on this :(

package fr.notri1.minewolves.listeners;

import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.network.packet.client.play.ClientPlayerPositionAndRotationPacket;
import net.minestom.server.network.packet.client.play.ClientPlayerRotationPacket;
import net.minestom.server.network.packet.server.play.EntityHeadLookPacket;
import net.minestom.server.network.packet.server.play.EntityRotationPacket;
import org.jspecify.annotations.NonNull;

public class FixRotation implements EventListener<PlayerPacketEvent> {

    @Override
    public @NonNull Class<PlayerPacketEvent> eventType() {
        return PlayerPacketEvent.class;
    }

    @Override
    public @NonNull Result run(PlayerPacketEvent event) {
        Player player = event.getPlayer();

        if (player.getVehicle() == null) return Result.SUCCESS;

        var packet = event.getPacket();
        float yaw = 0;
        float pitch = 0;
        boolean isRotation = false;

        if (packet instanceof ClientPlayerRotationPacket rotationPacket) {
            yaw = rotationPacket.yaw();
            pitch = rotationPacket.pitch();
            isRotation = true;
        } else if (packet instanceof ClientPlayerPositionAndRotationPacket posRotPacket) {
            yaw = posRotPacket.position().yaw();
            pitch = posRotPacket.position().pitch();
            isRotation = true;
        }

        if (isRotation) {
            EntityHeadLookPacket headLookPacket = new EntityHeadLookPacket(player.getEntityId(), yaw);
            player.sendPacketToViewers(headLookPacket);

            EntityRotationPacket rotationPacket = new EntityRotationPacket(
                    player.getEntityId(),
                    yaw,
                    pitch,
                    player.isOnGround()
            );
            player.sendPacketToViewers(rotationPacket);
        }
        return Result.SUCCESS;
    }
}

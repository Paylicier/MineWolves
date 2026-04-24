package fr.notri1.minewolves.listeners;

import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.network.packet.client.play.ClientPlayerRotationPacket;
import net.minestom.server.network.packet.client.play.ClientPlayerPositionAndRotationPacket;
import net.minestom.server.network.packet.client.play.ClientTickEndPacket;
import net.minestom.server.network.packet.server.play.EntityPositionAndRotationPacket;
import net.minestom.server.network.packet.server.play.EntityRotationPacket;
import net.minestom.server.network.packet.server.play.PlayerRotationPacket;

public class Packet implements EventListener<PlayerPacketEvent> {

    @Override
    public Class<PlayerPacketEvent> eventType() {
        return PlayerPacketEvent.class;
    }

    @Override
    public Result run(PlayerPacketEvent event) {
        Player player = event.getPlayer();
        if(event.getPacket() instanceof ClientTickEndPacket) return Result.SUCCESS;
        if (player.getVehicle() != null) {
            System.out.println("Packet received: " + event.getPacket().getClass().getSimpleName());
            if(event.getPacket() instanceof ClientPlayerRotationPacket pkt) {
//                player.sendActionBar(Component.text(pkt.yaw() + " " + pkt.pitch()));
//                player.sendPacketToViewers(new EntityRotationPacket(player.getEntityId(), pkt.yaw(), pkt.pitch(), pkt.onGround()));
//                player.sendPacketToViewers(new EntityRotationPacket(player.getEntityId(), pkt.yaw(), pkt.pitch(), pkt.onGround()));
            }
            if(event.getPacket() instanceof ClientPlayerPositionAndRotationPacket pkt) {
//                player.setView(pkt.position().yaw(), pkt.position().pitch());
//                player.sendPacketToViewers(new EntityRotationPacket(player.getEntityId(), pkt.position().yaw(), pkt.position().pitch(), pkt.onGround()));
//                player.sendPacketToViewers(new EntityPositionAndRotationPacket(player.getEntityId(), (short) pkt.position().x(), (short) pkt.position().y(), (short) pkt.position().z(), pkt.position().yaw(), pkt.position().pitch(), pkt.onGround()));
            }
        }
        return Result.SUCCESS;
    }
}

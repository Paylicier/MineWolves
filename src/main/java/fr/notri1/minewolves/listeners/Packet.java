package fr.notri1.minewolves.listeners;

import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerPacketEvent;

public class Packet implements EventListener<PlayerPacketEvent> {

    @Override
    public Class<PlayerPacketEvent> eventType() {
        return PlayerPacketEvent.class;
    }

    @Override
    public Result run(PlayerPacketEvent event) {
        if (!event.getPacket().getClass().getSimpleName().equals("ClientTickEndPacket")) {
            System.out.println("Packet received: " + event.getPacket().getClass().getSimpleName());
        }
        return Result.SUCCESS;
    }
}

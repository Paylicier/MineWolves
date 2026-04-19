package fr.notri1.minewolves.listeners;

import net.minestom.server.MinecraftServer;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.GlobalEventHandler;

public final class Listeners {
    public static void init() {
        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        EventNode<Event> entityNode = EventNode.type("listeners", EventFilter.ALL);

        entityNode
                .addListener(new AsyncPlayerConfiguration())
                .addListener(new PlayerBlockBreak())
                .addListener(new PlayerBlockPlace())
                .addListener(new PlayerFirstSpawn())
                .addListener(new PlayerScroll())
                .addListener(new ServerListPing());

        globalEventHandler.addChild(entityNode);
    }
}
package fr.notri1.minewolves.listeners;

import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.server.ServerListPingEvent;
import net.minestom.server.ping.Status;

import static fr.notri1.minewolves.MineWolves.config;
import static fr.notri1.minewolves.MineWolves.mineWolvesManager;

public class ServerListPing implements EventListener<ServerListPingEvent> {
    @Override
    public Class<ServerListPingEvent> eventType() {
        return ServerListPingEvent.class;
    }

    @Override
    public Result run(ServerListPingEvent event) {
        event.setStatus(Status.builder()
                .description(Component.text("MineWolves - A Minecraft Werewolf Game"))
                .playerInfo(Status.PlayerInfo.builder()
                        .onlinePlayers(MinecraftServer.getConnectionManager().getOnlinePlayerCount())
                        .maxPlayers(config.getGame().getMaxPlayers())
                        .sample(Component.text("Current phase : " + mineWolvesManager.status.name()))
                        .build())
                .versionInfo(new Status.VersionInfo(MinecraftServer.VERSION_NAME, MinecraftServer.PROTOCOL_VERSION))
                .build()
        );

        return Result.SUCCESS;
    }
}

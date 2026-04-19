package fr.notri1.minewolves.listeners;

import fr.notri1.minewolves.MineWolves;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerSpawnEvent;
import org.jetbrains.annotations.NotNull;

import static fr.notri1.minewolves.MineWolves.config;
import static fr.notri1.minewolves.MineWolves.instanceContainer;

public class PlayerFirstSpawn implements EventListener<PlayerSpawnEvent> {
    @Override
    public @NotNull Class<PlayerSpawnEvent> eventType() {
        return PlayerSpawnEvent.class;
    }

    @Override
    public Result run(PlayerSpawnEvent event) {
        System.out.println("Player " + event.getPlayer().getUsername() + " spawned: " + event.isFirstSpawn());
        if (!event.isFirstSpawn()) return null;

        final Player player = event.getPlayer();

        // [+] PLAYER [1/20]
        instanceContainer.sendMessage(Component.text("[+] ").color(NamedTextColor.GREEN).append(Component.text(player.getUsername()).color(NamedTextColor.WHITE)).append(Component.text(" [" + instanceContainer.getPlayers().size() + "/" + config.getGame().getMaxPlayers() + "]").color(NamedTextColor.GRAY)));

        MineWolves.mineWolvesManager.checkStart();

        return null;
    }
}
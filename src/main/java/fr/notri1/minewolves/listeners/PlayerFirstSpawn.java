package fr.notri1.minewolves.listeners;

import fr.notri1.minewolves.MineWolves;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerSpawnEvent;
import org.jetbrains.annotations.NotNull;

import static fr.notri1.minewolves.MineWolves.instanceContainer;

public class PlayerFirstSpawn implements EventListener<PlayerSpawnEvent> {
    @Override
    public @NotNull Class<PlayerSpawnEvent> eventType() {
        return PlayerSpawnEvent.class;
    }

    @Override
    public Result run(PlayerSpawnEvent event) {
        if (!event.isFirstSpawn()) return null;

        final Player player = event.getPlayer();

        instanceContainer.sendMessage(Component.translatable("multiplayer.player.joined", player.getName()).color(NamedTextColor.GREEN).append(Component.text(" [" + instanceContainer.getPlayers().size() + "/" + "69" + "]").color(NamedTextColor.GRAY)));

        if (instanceContainer.getPlayers().size() >= 2) { //todo: use numbers from config + make it so the game can start between min and max, idk how with the cooldown and things
            MineWolves.mineWolvesManager.start();
        }

        return null;
    }
}
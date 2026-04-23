package fr.notri1.minewolves.listeners;

import fr.notri1.minewolves.MineWolves;
import fr.notri1.minewolves.Status;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import org.jetbrains.annotations.NotNull;

import static fr.notri1.minewolves.MineWolves.*;

public class PlayerLeave implements EventListener<PlayerDisconnectEvent> {
    @Override
    public @NotNull Class<PlayerDisconnectEvent> eventType() {
        return PlayerDisconnectEvent.class;
    }

    @Override
    public Result run(PlayerDisconnectEvent event) {
        System.out.println("Player " + event.getPlayer().getUsername() + " left");

        final Player player = event.getPlayer();

        // [-] PLAYER [1/20]
        instanceContainer.sendMessage(Component.text("[-] ").color(NamedTextColor.RED).append(Component.text(player.getUsername()).color(NamedTextColor.WHITE)).append(Component.text(" [" + instanceContainer.getPlayers().size() + "/" + config.getGame().getMaxPlayers() + "]").color(NamedTextColor.GRAY)));

        if(mineWolvesManager.status != Status.IN_GAME) return Result.SUCCESS;

        if(instanceContainer.getPlayers().isEmpty()) {
            mineWolvesManager.endGame();
        }

        if(mineWolvesManager.roleManager.getRole(player) != null) {
            instanceContainer.sendMessage(Component.translatable("minewolves.eliminated.leave", Component.text(player.getUsername()).color(NamedTextColor.WHITE), mineWolvesManager.roleManager.getRole(player).getDisplayName().color(mineWolvesManager.roleManager.getRole(player).getTeam().getColor())).color(NamedTextColor.GRAY));
            mineWolvesManager.roleManager.removeRole(player);
        }

        if(mineWolvesManager.getMayor() != null && mineWolvesManager.getMayor().equals(player)) {
            mineWolvesManager.setMayor(null);
        }

        return Result.SUCCESS;
    }
}
package fr.notri1.minewolves.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerBlockPlaceEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerBlockPlace implements EventListener<PlayerBlockPlaceEvent> {
    @Override
    public @NotNull Class<PlayerBlockPlaceEvent> eventType() {
        return PlayerBlockPlaceEvent.class;
    }

    @Override
    public Result run(PlayerBlockPlaceEvent event) {
        final Player player = event.getPlayer();
        event.setCancelled(true);
        player.sendActionBar(Component.translatable("minewolves.cannot_place").color(NamedTextColor.RED));
        return null;
    }
}
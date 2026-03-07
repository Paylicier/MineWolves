package fr.notri1.minewolves.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerBlockBreak implements EventListener<PlayerBlockBreakEvent> {
    @Override
    public @NotNull Class<PlayerBlockBreakEvent> eventType() {
        return PlayerBlockBreakEvent.class;
    }

    @Override
    public Result run(PlayerBlockBreakEvent event) {
        final Player player = event.getPlayer();
        event.setCancelled(true);
        player.sendActionBar(Component.translatable("minewolves.cannot_break").color(NamedTextColor.RED));

        //player.showBossBar(BossBar.bossBar(Component.text("\uF808\uF808\uF808\uF808\uF808\uF808\uF806\uF806\uF802\uF804W").font(Key.key("minewolves", "roles")), 0f, BossBar.Color.WHITE, BossBar.Overlay.PROGRESS));
        //MineWolves.mineWolvesManager.start();
        return null;
    }
}
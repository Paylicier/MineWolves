package fr.notri1.minewolves.listeners;

import fr.notri1.minewolves.Status;
import fr.notri1.minewolves.game.phases.DayPhase;
import fr.notri1.minewolves.game.phases.GamePhase;
import fr.notri1.minewolves.game.phases.NightPhase;
import fr.notri1.minewolves.game.phases.turns.WerewolfTurn;
import fr.notri1.minewolves.game.roles.LittleGirl;
import fr.notri1.minewolves.game.roles.Role;
import fr.notri1.minewolves.game.roles.Team;
import fr.notri1.minewolves.game.roles.Werewolf;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerChatEvent;

import static fr.notri1.minewolves.MineWolves.mineWolvesManager;

public class Chat implements EventListener<PlayerChatEvent> {

    @Override
    public Class<PlayerChatEvent> eventType() {
        return PlayerChatEvent.class;
    }

    @Override
    public Result run(PlayerChatEvent event) {
        Player player = event.getPlayer();
        GamePhase phase = mineWolvesManager.getPhase();
        if ((mineWolvesManager.status == Status.IN_GAME) && mineWolvesManager.roleManager.getRole(player) == null) {
            // spec cannot chat
            event.setCancelled(true);
            Audience specAudience = Audience.audience(player.getInstance().getPlayers().stream().filter(p -> p.getGameMode().equals(GameMode.SPECTATOR)).toArray(Player[]::new));
            specAudience.sendMessage(Component.translatable("minewolves.spectator").color(NamedTextColor.GRAY).append(Component.text(" | ").color(NamedTextColor.GRAY)).append(Component.text(player.getUsername()).color(NamedTextColor.WHITE)).append(Component.text(": ").color(NamedTextColor.GRAY)).append(Component.text(event.getRawMessage()).color(NamedTextColor.WHITE)));
            return Result.SUCCESS;
        }
        if ((mineWolvesManager.status == Status.IN_GAME) && !(phase instanceof DayPhase) && (((NightPhase) phase).currentTurns != null)) {
            event.setCancelled(true);
            // if player is a wolf, allow them to chat with other wolves
            Role playerRole = mineWolvesManager.roleManager.getRole(player);
            if (playerRole != null && (playerRole.getTeam() == Team.WEREWOLVES)) {
                if (((NightPhase) phase).currentTurns.stream().anyMatch(t -> t instanceof WerewolfTurn)) {

                    // maybe add an option in config to allow/disallow specs to see wolf chat

                    // wolf

                    // todo: make it so it sends to everyone in the team, not only normal wolves
                    Audience wolfAudience = Audience.audience(mineWolvesManager.roleManager.getPlayersWithRole(playerRole).toArray(new Player[0]));
                    wolfAudience.sendMessage(Component.translatable("minewolves.role.werewolf").color(Team.WEREWOLVES.getColor()).append(Component.text(" | ").color(NamedTextColor.GRAY)).append(Component.text(player.getUsername()).color(NamedTextColor.WHITE)).append(Component.text(": ").color(NamedTextColor.GRAY)).append(Component.text(event.getRawMessage()).color(NamedTextColor.WHITE)));

                    // spec
                    Audience specAudience = Audience.audience(player.getInstance().getPlayers().stream().filter(p -> p.getGameMode().equals(GameMode.SPECTATOR)).toArray(Player[]::new));
                    specAudience.sendMessage(Component.translatable("minewolves.role.werewolf").color(Team.WEREWOLVES.getColor()).append(Component.text(" | ").color(NamedTextColor.GRAY)).append(Component.text(player.getUsername()).color(NamedTextColor.WHITE)).append(Component.text(": ").color(NamedTextColor.GRAY)).append(Component.text(event.getRawMessage()).color(NamedTextColor.WHITE)));

                    //little girl
                    Audience littleGirlAudience = Audience.audience(player.getInstance().getPlayers().stream().filter(p -> mineWolvesManager.roleManager.getRole(p) != null && (mineWolvesManager.roleManager.getRole(p) instanceof LittleGirl)).toArray(Player[]::new));
                    littleGirlAudience.sendMessage(Component.translatable("minewolves.role.werewolf").color(Team.WEREWOLVES.getColor()).append(Component.text(" | ").color(NamedTextColor.GRAY)).append(Component.text("player").decorate(TextDecoration.OBFUSCATED).color(((Werewolf) playerRole).getColorForPlayer(player))).append(Component.text(": ").color(NamedTextColor.GRAY)).append(Component.text(event.getRawMessage()).color(NamedTextColor.WHITE)));

                    return Result.SUCCESS;
                }
            }
            player.sendActionBar(Component.translatable("minewolves.cannot_chat").color(NamedTextColor.RED));
        }
        return Result.SUCCESS;
    }
}

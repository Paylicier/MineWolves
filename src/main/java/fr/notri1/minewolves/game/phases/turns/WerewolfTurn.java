package fr.notri1.minewolves.game.phases.turns;

import fr.notri1.minewolves.game.menus.WolfMenu;
import fr.notri1.minewolves.game.phases.NightPhase;
import fr.notri1.minewolves.game.roles.Team;
import fr.notri1.minewolves.game.roles.Werewolf;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.timer.TaskSchedule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import static fr.notri1.minewolves.MineWolves.instanceContainer;
import static fr.notri1.minewolves.MineWolves.mineWolvesManager;

public class WerewolfTurn extends NightTurn {

    private final Werewolf role;

    public WerewolfTurn(Werewolf role) {
        this.role = role;
    }

    // <voter uuid, target uuid>
    private final Map<UUID, UUID> votes = new HashMap<>();

    @Override
    public void onTurn() {
        role.getPlayers().forEach(player -> {
            WolfMenu wolfMenu = new WolfMenu(mineWolvesManager.roleManager.getRole(player));
            wolfMenu.open(player);
        });

        AtomicInteger countdown = new AtomicInteger(30);

        MinecraftServer.getSchedulerManager().submitTask(() -> {
            int current = countdown.getAndDecrement();

            if (current < 0) {
                role.getPlayers().forEach(fr.notri1.minewolves.game.menus.Menu::closeStatic);

                Player eliminatedPlayer = null;

                if (!votes.isEmpty()) {
                    Map<UUID, Long> voteCounts = votes.values().stream()
                            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

                    long maxVotes = voteCounts.values().stream()
                            .max(Long::compare)
                            .orElse(0L);

                    List<UUID> topVoted = voteCounts.entrySet().stream()
                            .filter(entry -> entry.getValue() == maxVotes)
                            .map(Map.Entry::getKey)
                            .toList();

                    if (topVoted.size() == 1) {
                        eliminatedPlayer = instanceContainer.getPlayerByUuid(topVoted.getFirst());

                        mineWolvesManager.addPlayerToEliminate(eliminatedPlayer);

                        Audience.audience(role.getPlayers()).sendMessage(Component.translatable("minewolves.role.werewolf").color(Team.WEREWOLVES.getColor())
                                .append(Component.text(" | ").color(NamedTextColor.GRAY))
                                .append(Component.translatable("minewolves.werewolf.vote_result", Component.text(eliminatedPlayer.getUsername())).color(NamedTextColor.WHITE)));

                    } else {
                        // no one gets killed
                        Audience.audience(role.getPlayers()).sendMessage(Component.translatable("minewolves.role.werewolf").color(Team.WEREWOLVES.getColor())
                                .append(Component.text(" | ").color(NamedTextColor.GRAY))
                                .append(Component.translatable("minewolves.werewolf.tie").color(NamedTextColor.WHITE)));
                    }
                }

                ((NightPhase) mineWolvesManager.getPhase()).nextTurn();
                return TaskSchedule.stop();
            }

            role.getPlayers().forEach(player -> {
                fr.notri1.minewolves.game.menus.Menu.MenuSession session = fr.notri1.minewolves.game.menus.Menu.getSession(player);
                if (session != null && session.getMenu() instanceof WolfMenu wolfMenu) {
                    wolfMenu.updateElement(player, "countdown", Component.translatable("minewolves.menu.countdown", Component.text(current)).color(role.getTeam().getColor()));
                }
            });

            return TaskSchedule.seconds(1);
        });
    }

    public void handleVote(Player voter, Player target) {
        votes.put(voter.getUuid(), target.getUuid());

        // Update the vote menu for all werewolves
        role.getPlayers().forEach(p -> {
            fr.notri1.minewolves.game.menus.Menu.MenuSession session = fr.notri1.minewolves.game.menus.Menu.getSession(p);
            if (session != null && session.getMenu() instanceof WolfMenu wolfMenu) {
                wolfMenu.updateVotes(p, votes);
            }
        });

        Audience audience = Audience.audience(role.getPlayers());
        audience.sendMessage(Component.translatable("minewolves.role.werewolf").color(Team.WEREWOLVES.getColor())
                .append(Component.text(" | ").color(NamedTextColor.GRAY))
                .append(Component.translatable("minewolves.werewolf.vote", Component.text(voter.getUsername()), Component.text(target.getUsername())).color(NamedTextColor.WHITE)));
    }
}
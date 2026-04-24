package fr.notri1.minewolves.game.phases;

import fr.notri1.minewolves.game.menus.EliminationVoteMenu;
import fr.notri1.minewolves.game.menus.MayorElectionMenu;
import fr.notri1.minewolves.game.menus.Menu;
import fr.notri1.minewolves.game.roles.Role;
import fr.notri1.minewolves.game.roles.Team;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.GameMode;
import net.minestom.server.potion.PotionEffect;
import net.minestom.server.timer.TaskSchedule;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import static fr.notri1.minewolves.MineWolves.instanceContainer;
import static fr.notri1.minewolves.MineWolves.mineWolvesManager;
import static fr.notri1.minewolves.pack.LocalizationUtils.getLocalizedSound;

public class DayPhase extends GamePhase {

    public DayPhase() {

    }

    @Override
    public void onStart() {
        instanceContainer.sendMessage(Component.translatable("minewolves.day.start"));
        //todo: sound

        // day time
        instanceContainer.setTime(1000);

        // clear blindess
        instanceContainer.getPlayers().forEach(player -> player.removeEffect(PotionEffect.BLINDNESS));


        // narrator
        instanceContainer.getPlayers().forEach(player -> player.playSound(getLocalizedSound("minewolves", "narrator.night_falls", player)));


        mineWolvesManager.getPlayersToEliminate().forEach(player -> {
            instanceContainer.sendMessage(Component.translatable("minewolves.day.killed", Component.text(player.getUsername()), mineWolvesManager.roleManager.getRole(player).getDisplayName()));
            mineWolvesManager.eliminatePlayer(player);
        });

        if (mineWolvesManager.getPlayersToEliminate().isEmpty()) {
            instanceContainer.sendMessage(Component.translatable("minewolves.day.no_kill"));
        }

        mineWolvesManager.clearPlayersToEliminate();

        Team winner = winCheck();
        if (winner != null) {
            // todo: translation for team name
            instanceContainer.sendMessage(Component.translatable("minewolves.game.win", Component.text(winner.getName())));

            mineWolvesManager.endGame();

            return;
        }

        if (mineWolvesManager.getMayor() == null) {
            mayorElection();
        } else {
            eliminationVote();
        }
    }

    private void mayorElection() {
        instanceContainer.sendMessage(Component.translatable("minewolves.day.mayor.start"));

        List<net.minestom.server.entity.Player> candidates = instanceContainer.getPlayers().stream()
                .filter(player -> mineWolvesManager.roleManager.getRole(player) != null)
                .toList();

        if (candidates.isEmpty()) {
            onEnd();
            return;
        }

        Map<UUID, UUID> votes = new java.util.HashMap<>();

        candidates.forEach(candidate -> {
            MayorElectionMenu mayorElectionMenu = new MayorElectionMenu((voter, target) -> {
                votes.put(voter.getUuid(), target.getUuid());
                candidates.forEach(c -> {
                    Menu.MenuSession session = Menu.getSession(c);
                    if (session != null && session.getMenu() instanceof MayorElectionMenu menu) {
                        menu.updateVotes(c, votes);
                    }
                });
            });
            mayorElectionMenu.open(candidate);
        });

        AtomicInteger countdown = new AtomicInteger(70);

        MinecraftServer.getSchedulerManager().submitTask(() -> {
            int current = countdown.getAndDecrement();

            if (current < 0) {
                candidates.forEach(Menu::closeStatic);

                // Count votes
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
                    net.minestom.server.entity.Player electedMayor = instanceContainer.getPlayerByUuid(topVoted.getFirst());
                    if (electedMayor != null) {
                        instanceContainer.sendMessage(Component.translatable("minewolves.day.mayor.elected", Component.text(electedMayor.getUsername())));
                        mineWolvesManager.setMayor(electedMayor);
                    }
                } else {
                    instanceContainer.sendMessage(Component.translatable("minewolves.day.mayor.tie"));
                }
                eliminationVote();
                return TaskSchedule.stop();
            }

            candidates.forEach(candidate -> {
                Menu.MenuSession session = Menu.getSession(candidate);
                if (session != null && session.getMenu() instanceof MayorElectionMenu mayorElectionMenu) {
                    mayorElectionMenu.updateElement(candidate, "countdown", Component.translatable("minewolves.menu.countdown", Component.text(current)).color(NamedTextColor.GRAY));
                }
            });

            return TaskSchedule.seconds(1);
        });
    }

    private void eliminationVote() {
        instanceContainer.sendMessage(Component.translatable("minewolves.day.kill.start"));

        List<net.minestom.server.entity.Player> candidates = instanceContainer.getPlayers().stream()
                .filter(player -> mineWolvesManager.roleManager.getRole(player) != null)
                .toList();

        if (candidates.isEmpty()) {
            onEnd();
            return;
        }

        Map<UUID, UUID> votes = new java.util.HashMap<>();

        candidates.forEach(candidate -> {
            EliminationVoteMenu menu = new EliminationVoteMenu((voter, target) -> {
                votes.put(voter.getUuid(), target.getUuid());
                candidates.forEach(c -> {
                    Menu.MenuSession session = Menu.getSession(c);
                    if (session != null && session.getMenu() instanceof EliminationVoteMenu eliminationVoteMenu) {
                        eliminationVoteMenu.updateVotes(c, votes);
                    }
                });
            });
            menu.open(candidate);
        });

        AtomicInteger countdown = new AtomicInteger(Math.max(20, Math.min(candidates.size() * 10, 70)));
        MinecraftServer.getSchedulerManager().submitTask(() -> {
            int current = countdown.getAndDecrement();

            if (current < 0) {
                candidates.forEach(Menu::closeStatic);

                // Count votes, mayor's vote counts double
                Map<UUID, Long> voteCounts = new java.util.HashMap<>();
                for (Map.Entry<UUID, UUID> entry : votes.entrySet()) {
                    UUID voterId = entry.getKey();
                    UUID targetId = entry.getValue();
                    long weight = 1L;
                    if (mineWolvesManager.getMayor() != null && mineWolvesManager.getMayor().getUuid().equals(voterId)) {
                        weight = 2L;
                    }
                    voteCounts.put(targetId, voteCounts.getOrDefault(targetId, 0L) + weight);
                }

                long maxVotes = voteCounts.values().stream()
                        .max(Long::compare)
                        .orElse(0L);

                List<UUID> topVoted = voteCounts.entrySet().stream()
                        .filter(entry -> entry.getValue() == maxVotes)
                        .map(Map.Entry::getKey)
                        .toList();

                if (topVoted.size() == 1) {
                    net.minestom.server.entity.Player eliminated = instanceContainer.getPlayerByUuid(topVoted.getFirst());
                    if (eliminated != null) {
                        instanceContainer.sendMessage(Component.translatable("minewolves.day.killed.village", Component.text(eliminated.getUsername()), mineWolvesManager.roleManager.getRole(eliminated).getDisplayName()));
                        mineWolvesManager.eliminatePlayer(eliminated);
                    }
                } else if (!topVoted.isEmpty()) {
                    instanceContainer.sendMessage(Component.translatable("minewolves.day.kill.tie"));
                } else {
                    instanceContainer.sendMessage(Component.translatable("minewolves.day.kill.no_vote"));
                }

                // Check again for win condition after a kill
                Team winner = winCheck();
                if (winner != null) {
                    instanceContainer.sendMessage(Component.translatable("minewolves.game.win", Component.text(winner.getName())));
                    mineWolvesManager.endGame();
                } else {
                    onEnd();
                }

                return TaskSchedule.stop();
            }

            candidates.forEach(candidate -> {
                Menu.MenuSession session = Menu.getSession(candidate);
                if (session != null && session.getMenu() instanceof EliminationVoteMenu eliminationVoteMenu) {
                    eliminationVoteMenu.updateElement(candidate, "countdown", Component.translatable("minewolves.menu.countdown", Component.text(current)).color(NamedTextColor.GRAY));
                }
            });

            return TaskSchedule.seconds(1);
        });
    }

    private Team winCheck() { //todo: cupid, the couple win (so it's no a team)
        // if more wolves than villagers, they win
        List<Role> aliveRoles = mineWolvesManager.roleManager.getAliveRoles();

        // solo (or only one villager/wolf)
        if (aliveRoles.size() == 1) return aliveRoles.getFirst().getTeam();

        List<Role> aliveWolves = aliveRoles.stream().filter(r -> r.getTeam().equals(Team.WEREWOLVES)).toList();
        List<Role> aliveVillagers = aliveRoles.stream().filter(r -> r.getTeam().equals(Team.VILLAGE)).toList();

        // if more wolves than villagers, wolves win
        if (aliveWolves.size() > aliveVillagers.size()) return Team.WEREWOLVES;

        // if the village killed every wolf, village wins
        if (aliveWolves.isEmpty()) return Team.VILLAGE;

        // no one wins (game continues)
        return null;
    }

    @Override
    public void onEnd() {

        mineWolvesManager.incrementDayCount();

        // switch to night
        mineWolvesManager.setPhase(new NightPhase());
    }
}

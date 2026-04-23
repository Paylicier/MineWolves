package fr.notri1.minewolves.game;

import fr.notri1.minewolves.Status;
import fr.notri1.minewolves.game.phases.GamePhase;
import fr.notri1.minewolves.game.phases.RolePhase;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static fr.notri1.minewolves.MineWolves.*;

public class MineWolvesManager {
    public Status status = Status.WAITING;
    public RoleManager roleManager = new RoleManager();
    private GamePhase currentPhase;

    private List<Player> playersToEliminate = new ArrayList<>();

    private Map<Player, Entity> seats = new java.util.HashMap<>();

    private int dayCount = 0;
    private Player mayor = null;

    private Task countdownTask;
    private int countdown = -1;

    public GamePhase getPhase() {
        return this.currentPhase;
    }

    public List<Player> getPlayersToEliminate() {
        return playersToEliminate;
    }

    public void addPlayerToEliminate(Player player) {
        if (!playersToEliminate.contains(player)) {
            playersToEliminate.add(player);
        }
    }

    public void clearPlayersToEliminate() {
        playersToEliminate.clear();
    }

    public void removePlayerToEliminate(Player player) {
        playersToEliminate.remove(player);
    }

    public int getDayCount() {
        return dayCount;
    }

    public void incrementDayCount() {
        dayCount++;
    }

    public void setMayor(Player player) {
        mayor = player;
    }
    public Player getMayor() {
        return mayor;
    }

    public void setPhase(GamePhase phase) {
        this.currentPhase = phase;
        System.out.println("[PHASE] New phase: " + phase.getClass().getSimpleName());
        phase.onStart();
    }

    public void checkStart() {
        int players = instanceContainer.getPlayers().size();
        int min = config.getGame().getMinPlayers();
        int max = config.getGame().getMaxPlayers();

        if (players < min) {
            if (countdownTask != null) {
                countdownTask.cancel();
                countdownTask = null;
                countdown = -1;
                instanceContainer.sendMessage(Component.translatable("minewolves.game.cancelled").color(NamedTextColor.RED));
            }
            return;
        }

        if (status == Status.WAITING) {
            updateCountdown(players, min, max);

            if (countdownTask == null) {
                countdownTask = MinecraftServer.getSchedulerManager().buildTask(this::tickCountdown)
                        .repeat(TaskSchedule.seconds(1))
                        .schedule();
            }
        }
    }

    public void eliminatePlayer(Player player) {
        if(status != Status.IN_GAME) return;
        if (roleManager.getRole(player) == null) return;
        mineWolvesManager.roleManager.removeRole(player);
        if (mineWolvesManager.getMayor() != null && mineWolvesManager.getMayor().equals(player)) {
            mineWolvesManager.setMayor(null);
        }
        if(!player.isOnline()) return;
        player.setGameMode(GameMode.SPECTATOR);
        player.setAutoViewable(false);
        player.getViewers().forEach(p -> player.removeViewer(p));
        mineWolvesManager.unSitPlayer(player);
    }

    private void updateCountdown(int players, int min, int max) {
        int targetCountdown;
        if (players >= max) {
            targetCountdown = 10;
        } else {
            int minTime = 10;
            int maxTime = 60;
            if (max == min) targetCountdown = minTime;
            else {
                float ratio = (float) (players - min) / (max - min);
                targetCountdown = (int) (maxTime - ratio * (maxTime - minTime));
            }
        }

        if (countdown == -1 || targetCountdown < countdown) {
            countdown = targetCountdown;
        }
    }

    private void tickCountdown() {
        if (countdown <= 0 || status == Status.IN_GAME) {
            countdownTask.cancel();
            countdownTask = null;
            startGame();
            return;
        }

        if (countdown <= 5 || countdown % 10 == 0) {
            instanceContainer.playSound(Sound.sound(Key.key("block.note_block.bell"), Sound.Source.MASTER, 1f, 1f));
            instanceContainer.sendMessage(Component.translatable("minewolves.game.starting_in", Component.text(countdown).color(NamedTextColor.GREEN)).color(NamedTextColor.GREEN));
        }
        countdown--;
    }

    public void startGame() {
        if (status != Status.STARTING && status != Status.WAITING) return;
        status = Status.IN_GAME;

        instanceContainer.sendMessage(Component.translatable("minewolves.game.started").color(NamedTextColor.GREEN));
        System.out.println("Game started!");
        sitPlayers();
        setPhase(new RolePhase());
    }

    public void endGame() {
        if(status != Status.IN_GAME) return;
        mineWolvesManager.status = Status.ENDING;

        MinecraftServer.getSchedulerManager().buildTask(() -> {
            instanceContainer.sendMessage(Component.translatable("minewolves.restarting").color(NamedTextColor.RED));
        }).delay(Duration.ofSeconds(20)).schedule();

        MinecraftServer.getSchedulerManager().buildTask(() -> {
            instanceContainer.getPlayers().forEach(p -> p.kick("Game ended, server is restarting..."));
            MinecraftServer.stopCleanly();
        }).delay(Duration.ofSeconds(25)).schedule();
    }

    private void sitPlayers() {
        List<List<Float>> sitPoints = new ArrayList<>(config.getGame().getSitPoints().stream().limit(instanceContainer.getPlayers().size()).toList());
        instanceContainer.getPlayers().forEach(player -> {
            Pos seatPos = new Pos(sitPoints.getFirst().get(0) + 0.5, sitPoints.getFirst().get(1) - 2, sitPoints.getFirst().get(2) + 0.5);
            Entity seat = new Entity(EntityType.ARMOR_STAND);
            seat.setInstance(instanceContainer, seatPos);
            seat.setNoGravity(true);
            seat.setInvisible(true);
            seat.addPassenger(player);

            //player.teleport(new Pos(sitPoints.getFirst().get(0).doubleValue(), sitPoints.getFirst().get(1).doubleValue(), sitPoints.getFirst().get(2).doubleValue()));
            seats.put(player, seat);
            sitPoints.removeFirst();
        });
    }

    public void unSitPlayer(Player p) {
        Entity seat = seats.get(p);
        if (seat != null) {
            if (!seat.getPassengers().contains(p)) return;
            seat.removePassenger(p);
            seat.remove();
            seats.remove(p);
        }
    }
}

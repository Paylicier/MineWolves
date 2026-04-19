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
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;

import java.util.ArrayList;
import java.util.List;

import static fr.notri1.minewolves.MineWolves.config;
import static fr.notri1.minewolves.MineWolves.instanceContainer;

public class MineWolvesManager {
    public Status status = Status.WAITING;
    public RoleManager roleManager = new RoleManager();
    private int fakePlayerCount = 0;
    private GamePhase currentPhase;

    private Task countdownTask;
    private int countdown = -1;

    public int getFakePlayerCount() {
        return fakePlayerCount;
    }

    public void setFakePlayerCount(int count) {
        this.fakePlayerCount = count;
    }

    /**
     * Returns the effective player count (real + fake) for game logic.
     */
    public int getEffectivePlayerCount() {
        return instanceContainer.getPlayers().size() + fakePlayerCount;
    }

    public GamePhase getPhase() {
        return this.currentPhase;
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
                instanceContainer.sendMessage(Component.text("Not enough players anymore, countdown stopped.").color(NamedTextColor.RED));
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
            instanceContainer.sendMessage(Component.text("Game starting in " + countdown + " seconds!").color(NamedTextColor.YELLOW));
        }
    }

    private void tickCountdown() {
        if (countdown <= 0) {
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

    public boolean startGame() {
        if (status != Status.STARTING && status != Status.WAITING) return false;
        status = Status.IN_GAME;

        instanceContainer.sendMessage(Component.translatable("minewolves.game.started").color(NamedTextColor.GREEN));
        System.out.println("Game started!");
        sitPlayers();
        setPhase(new RolePhase());

        return true;
    }

    public boolean start() {
        return startGame();
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

            sitPoints.removeFirst();
        });
    }
}

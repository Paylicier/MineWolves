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
import net.minestom.server.timer.Scheduler;
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
        phase.onStart();
    }

    public boolean start() {
        if (status != Status.WAITING) return false;
        status = Status.STARTING;

        System.out.println("Starting the game...");

        Scheduler scheduler = MinecraftServer.getSchedulerManager();
        for (int second = 5; second > 0; second--) {
            int finalSecond = second;
            scheduler.buildTask(() -> {
                instanceContainer.playSound(Sound.sound(Key.key("block.note_block.bell"), Sound.Source.MASTER, 1f, 1f));
                instanceContainer.sendMessage(Component.translatable("minewolves.game.starting_in", Component.text(finalSecond).color(NamedTextColor.GREEN)).color(NamedTextColor.GREEN));
                System.out.println(finalSecond);
            }).delay(TaskSchedule.seconds(5 - second)).schedule();
        }

        scheduler.buildTask(() -> {
            status = Status.IN_GAME;
            instanceContainer.sendMessage(Component.translatable("minewolves.game.started").color(NamedTextColor.GREEN));
            System.out.println("Game started!");
            sitPlayers();
            setPhase(new RolePhase());
        }).delay(TaskSchedule.seconds(5)).schedule();

        return true;
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

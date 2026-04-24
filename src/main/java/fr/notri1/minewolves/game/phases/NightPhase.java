package fr.notri1.minewolves.game.phases;

import fr.notri1.minewolves.game.phases.turns.NightTurn;
import fr.notri1.minewolves.game.roles.Role;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.GameMode;
import net.minestom.server.potion.Potion;
import net.minestom.server.potion.PotionEffect;
import net.minestom.server.timer.TaskSchedule;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static fr.notri1.minewolves.MineWolves.instanceContainer;
import static fr.notri1.minewolves.MineWolves.mineWolvesManager;
import static fr.notri1.minewolves.pack.LocalizationUtils.getLocalizedSound;

public class NightPhase extends GamePhase {

    private final List<List<NightTurn>> groupedTurns;
    private int currentGroupIndex = 0;

    private int completedTurnsInCurrentGroup = 0;

    public List<NightTurn> currentTurns;

    public NightPhase() {
        this.groupedTurns = new ArrayList<>();

        Map<Integer, List<Role>> rolesByOrder = mineWolvesManager.roleManager.getAliveRoles().stream()
                .filter(role -> role.getNightOrder() >= 0)
                .filter(role -> role.createNightTurn() != null)
                .collect(Collectors.groupingBy(Role::getNightOrder));

        rolesByOrder.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    List<NightTurn> group = entry.getValue().stream()
                            .map(Role::createNightTurn)
                            .toList();
                    groupedTurns.add(group);
                });

        System.out.println(groupedTurns.size() + " groups for this night.");
    }

    @Override
    public void onStart() {
        instanceContainer.sendMessage(Component.translatable("minewolves.night.start"));

        instanceContainer.setTime(6000);

        instanceContainer.getPlayers().forEach(player -> {
            player.playSound(getLocalizedSound("minewolves", "narrator.night_falls", player));
        });

        final int targetTime = 18000;
        final int increment = 37;

        MinecraftServer.getSchedulerManager().submitTask(() -> {
            long currentTime = instanceContainer.getTime();

            if (currentTime >= targetTime) {
                instanceContainer.setTime(targetTime);
                applyBlindness();

                nextTurn();
                return TaskSchedule.stop();
            }

            instanceContainer.setTime(currentTime + increment);
            return TaskSchedule.tick(1);
        });
    }

    public void nextTurn() {
        if (currentTurns != null && completedTurnsInCurrentGroup < currentTurns.size()) {
            completedTurnsInCurrentGroup++;

            if (completedTurnsInCurrentGroup < currentTurns.size()) {
                return;
            }
        }

        System.out.println(groupedTurns.size() + " groups total, current: " + currentGroupIndex);
        if (currentGroupIndex >= groupedTurns.size()) {
            onEnd();
            return;
        }

        currentTurns = groupedTurns.get(currentGroupIndex);
        currentGroupIndex++;
        completedTurnsInCurrentGroup = 0;

        for (NightTurn turn : currentTurns) {
            System.out.println("Turn : " + turn.getClass().getSimpleName());
            turn.onTurn();
        }
    }

    private void applyBlindness() {
        instanceContainer.getPlayers().stream().filter(p -> p.getGameMode()!= GameMode.SPECTATOR).forEach(player ->
                player.addEffect(new Potion(PotionEffect.BLINDNESS, 0, -1, 0x00))
        );
    }

    @Override
    public void onEnd() {
        MinecraftServer.getSchedulerManager().buildTask(() -> {
            mineWolvesManager.setPhase(new DayPhase());
        }).delay(Duration.ofSeconds(2)).schedule();
    }
}
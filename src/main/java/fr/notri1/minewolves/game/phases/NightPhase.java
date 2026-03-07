package fr.notri1.minewolves.game.phases;

import fr.notri1.minewolves.game.phases.turns.NightTurn;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.potion.Potion;
import net.minestom.server.potion.PotionEffect;
import net.minestom.server.timer.TaskSchedule;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static fr.notri1.minewolves.MineWolves.instanceContainer;
import static fr.notri1.minewolves.MineWolves.mineWolvesManager;
import static fr.notri1.minewolves.pack.LocalizationUtils.getLocalizedSound;

public class NightPhase extends GamePhase {

    private final List<NightTurn> turns;
    private int currentTurnIndex = 0;

    public NightPhase() {
        turns = new ArrayList<NightTurn>();

        mineWolvesManager.roleManager.getAliveRoles().stream()
                .filter(role -> role.getNightOrder() >= 0)
                .sorted(Comparator.comparingInt(role -> role.getNightOrder()))
                .forEach(role -> turns.add(role.createNightTurn()));
    }

    @Override
    public void onStart() {
        instanceContainer.sendMessage(Component.text("Rompiche"));

        // set time to noon
        instanceContainer.setTime(6000);

        // narrator
        instanceContainer.getPlayers().forEach(player -> {
            player.playSound(getLocalizedSound("minewolves", "narrator.night_falls", player));
        });

        // animation

        final int targetTime = 18000;
        final int increment = 37; // approx 16 sec (= voice line duration)

        MinecraftServer.getSchedulerManager().submitTask(() -> {
            long currentTime = instanceContainer.getTime();

            if (currentTime >= targetTime) {
                instanceContainer.setTime(targetTime);

                applyBlindness();

                return TaskSchedule.stop();
            }

            instanceContainer.setTime(currentTime + increment);
            return TaskSchedule.tick(1);
        });
    }

    private void applyBlindness() {
        instanceContainer.getPlayers().forEach(player ->
                player.addEffect(new Potion(PotionEffect.BLINDNESS, 0, -1, 0x00))
        );
    }

    @Override
    public void onEnd() {
        // switch to day
    }
}
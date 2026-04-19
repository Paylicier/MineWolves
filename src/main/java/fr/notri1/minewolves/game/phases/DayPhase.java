package fr.notri1.minewolves.game.phases;

import net.kyori.adventure.text.Component;
import net.minestom.server.potion.PotionEffect;

import static fr.notri1.minewolves.MineWolves.instanceContainer;
import static fr.notri1.minewolves.pack.LocalizationUtils.getLocalizedSound;

public class DayPhase extends GamePhase {

    private boolean isFirstDay = true;

    public DayPhase() {

    }

    @Override
    public void onStart() {
        instanceContainer.sendMessage(Component.text("good morning"));
        //todo: cocorico

        // day time
        instanceContainer.setTime(1000);

        // clear blindess
        instanceContainer.getPlayers().forEach(player -> player.removeEffect(PotionEffect.BLINDNESS));

        // narrator
        instanceContainer.getPlayers().forEach(player -> {
            player.playSound(getLocalizedSound("minewolves", "narrator.night_falls", player));
        });

    }

    @Override
    public void onEnd() {
        // anti infinite mayor
        if (isFirstDay) {
            isFirstDay = false;
        }

        // switch to night
    }
}
package fr.notri1.minewolves.game.phases.turns;

import fr.notri1.minewolves.game.phases.NightPhase;
import fr.notri1.minewolves.game.roles.Seer;
import net.minestom.server.MinecraftServer;

import java.time.Duration;

import static fr.notri1.minewolves.MineWolves.mineWolvesManager;

public class SeerTurn extends NightTurn {

    private final Seer role;

    public SeerTurn(Seer role) {
        this.role = role;
    }


    @Override
    public void onTurn() {
        //todo: open select menu
        role.getPlayers().forEach(player -> {
            player.sendMessage("It's your turn to use your Seer ability! Select a player to inspect.");
        });

        MinecraftServer.getSchedulerManager().buildTask(() -> {

        ((NightPhase) mineWolvesManager.getPhase()).nextTurn();
        }).delay(Duration.ofSeconds(5)).schedule();
    }
}

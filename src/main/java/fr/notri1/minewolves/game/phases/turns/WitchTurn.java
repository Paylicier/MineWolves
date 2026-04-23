package fr.notri1.minewolves.game.phases.turns;

import fr.notri1.minewolves.game.phases.NightPhase;
import fr.notri1.minewolves.game.roles.Witch;
import net.minestom.server.MinecraftServer;

import java.time.Duration;

import static fr.notri1.minewolves.MineWolves.mineWolvesManager;

public class WitchTurn extends NightTurn {

    private final Witch role;

    public WitchTurn(Witch role) {
        this.role = role;
    }

    @Override
    public void onTurn() {
        // todo: open potion menu
        role.getPlayers().forEach(player -> {
            player.sendMessage("It's your turn to use your Witch potions!");
        });

        MinecraftServer.getSchedulerManager().buildTask(() -> {

            ((NightPhase) mineWolvesManager.getPhase()).nextTurn();
        }).delay(Duration.ofSeconds(5)).schedule();
    }
}


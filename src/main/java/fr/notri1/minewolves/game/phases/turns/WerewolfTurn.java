package fr.notri1.minewolves.game.phases.turns;

import fr.notri1.minewolves.game.phases.NightPhase;
import fr.notri1.minewolves.game.roles.Werewolf;

import static fr.notri1.minewolves.MineWolves.mineWolvesManager;

public class WerewolfTurn extends NightTurn {

    private final Werewolf role;

    public WerewolfTurn(Werewolf role) {
        this.role = role;
    }

    @Override
    public void onTurn() {
        // todo: open select menu
        role.getPlayers().forEach(player -> {
            player.sendMessage("It's your turn to choose a player to eliminate!");
        });

        ((NightPhase) mineWolvesManager.getPhase()).nextTurn();
    }
}


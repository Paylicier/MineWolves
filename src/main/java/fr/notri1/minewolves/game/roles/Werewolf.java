package fr.notri1.minewolves.game.roles;

import fr.notri1.minewolves.game.phases.turns.NightTurn;
import fr.notri1.minewolves.game.phases.turns.WerewolfTurn;
import net.kyori.adventure.key.Key;
import net.minestom.server.entity.Player;

/**
 * The Werewolf wakes up each night to eliminate a villager.
 * Wins with the Werewolves team.
 */
public class Werewolf extends Role {

    @Override
    public String getKey() {
        return "werewolf";
    }

    @Override
    public int getMinPlayers() {
        return 1;
    }

    @Override
    public int getMaxPlayers() {
        return 3;
    }

    @Override
    public String getIcon() {
        return "W";
    }

    @Override
    public Key getSound() {
        return Key.key("minewolves", "role.werewolf");
    }

    @Override
    public Team getTeam() {
        return Team.WEREWOLVES;
    }


    public int getNightOrder() {
        return 1;
    }

    public NightTurn createNightTurn() {
        return new WerewolfTurn(this);
    }
}


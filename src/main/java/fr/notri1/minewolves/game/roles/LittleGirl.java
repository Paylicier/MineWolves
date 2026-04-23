package fr.notri1.minewolves.game.roles;

import fr.notri1.minewolves.game.phases.turns.NightTurn;
import fr.notri1.minewolves.game.phases.turns.SeerTurn;
import net.kyori.adventure.key.Key;

/**
 * The Seer can inspect one player each night to learn their role.
 * Wins with the Village team.
 */
public class LittleGirl extends Role {

    @Override
    public String getKey() {
        return "littlegirl";
    }

    @Override
    public int getMinPlayers() {
        return 0;
    }

    @Override
    public int getMaxPlayers() {
        return 1;
    }

    @Override
    public String getIcon() {
        return "l";
    }

    @Override
    public Key getSound() {
        return Key.key("minewolves", "role.littlegirl");
    }

    @Override
    public Team getTeam() {
        return Team.VILLAGE;
    }

    @Override
    public int getNightOrder() {
        return -1;
    }
}


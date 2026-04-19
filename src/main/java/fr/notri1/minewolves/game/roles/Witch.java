package fr.notri1.minewolves.game.roles;

import fr.notri1.minewolves.game.phases.turns.NightTurn;
import fr.notri1.minewolves.game.phases.turns.WitchTurn;
import net.kyori.adventure.key.Key;

/**
 * The Witch has two potions: one to heal and one to kill.
 * Each potion can only be used once per game.
 * Wins with the Village team.
 */
public class Witch extends Role {

    @Override
    public String getKey() {
        return "witch";
    }

    @Override
    public int getMinPlayers() {
        return 1;
    }

    @Override
    public int getMaxPlayers() {
        return 1;
    }

    @Override
    public String getIcon() {
        return "w";
    }

    @Override
    public Key getSound() {
        return Key.key("minecraft", "entity.witch.celebrate");
    }

    @Override
    public Team getTeam() {
        return Team.VILLAGE;
    }

    public int getNightOrder() {
        return 3;
    }

    public NightTurn createNightTurn() {
        return new WitchTurn(this);
    }
}


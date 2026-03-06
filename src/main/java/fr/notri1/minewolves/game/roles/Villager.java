package fr.notri1.minewolves.game.roles;

/**
 * The Villager has no special ability.
 * Wins with the Village team.
 */
public class Villager extends Role {

    @Override
    public String getKey() {
        return "villager";
    }

    @Override
    public int getMinPlayers() {
        return 0;
    }

    @Override
    public int getMaxPlayers() {
        return 3;
    }

    @Override
    public String getIcon() {
        return "V";
    }

    @Override
    public Team getTeam() {
        return Team.VILLAGE;
    }
}


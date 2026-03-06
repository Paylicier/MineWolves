package fr.notri1.minewolves.game.roles;

/**
 * The Seer can inspect one player each night to learn their role.
 * Wins with the Village team.
 */
public class Seer extends Role {

    @Override
    public String getKey() {
        return "seer";
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
        return "S";
    }

    @Override
    public Team getTeam() {
        return Team.VILLAGE;
    }

    @Override
    public void onNightStart() {
        //todo
    }
}


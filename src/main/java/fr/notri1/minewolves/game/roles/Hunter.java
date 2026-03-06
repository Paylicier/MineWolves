package fr.notri1.minewolves.game.roles;

/**
 * The Hunter can eliminate one player upon death.
 * Wins with the Village team.
 */
public class Hunter extends Role {

    @Override
    public String getKey() {
        return "hunter";
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
        return "H";
    }

    @Override
    public Team getTeam() {
        return Team.VILLAGE;
    }

    @Override
    public void onDeath() {
        // todo
    }
}


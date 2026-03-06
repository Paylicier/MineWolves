package fr.notri1.minewolves.game.roles;

import net.minestom.server.entity.Player;

/**
 * The Werewolf wakes up each night to eliminate a villager.
 * Wins with the Werewolves team.
 */
public class Werewolf extends Role {

    private Player target;

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
    public Team getTeam() {
        return Team.WEREWOLVES;
    }

    @Override
    public void onNightStart() {
        // todo
    }

    public Player getTarget() {
        return target;
    }

    /**
     * Sets the player targeted for elimination this night.
     */
    public void setTarget(Player target) {
        this.target = target;
    }
}


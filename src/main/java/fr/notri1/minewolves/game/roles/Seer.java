package fr.notri1.minewolves.game.roles;

import fr.notri1.minewolves.game.phases.turns.NightTurn;
import fr.notri1.minewolves.game.phases.turns.SeerTurn;
import net.kyori.adventure.key.Key;

/**
 * The Seer can inspect one player each night to learn their role.
 * Wins with the Village team.
 */
public class Seer extends Role {

    private final java.util.List<net.minestom.server.entity.Player> revealedPlayers = new java.util.ArrayList<>();

    public java.util.List<net.minestom.server.entity.Player> getRevealedPlayers() {
        return this.revealedPlayers;
    }

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
    public Key getSound() {
        return Key.key("minecraft", "block.enchantment_table.use");
    }

    @Override
    public Team getTeam() {
        return Team.VILLAGE;
    }

    @Override
    public int getNightOrder() {
        return 1;
    }

    @Override
    public NightTurn createNightTurn() {
        return new SeerTurn(this);
    }
}

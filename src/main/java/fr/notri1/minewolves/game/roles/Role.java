package fr.notri1.minewolves.game.roles;

import fr.notri1.minewolves.game.phases.turns.NightTurn;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;

import java.util.List;

import static fr.notri1.minewolves.MineWolves.mineWolvesManager;

/**
 * Abstract base class for all roles in MineWolves.
 */
public abstract class Role {

    /**
     * Returns the unique identifier key for this role (used for translations + icon).
     */
    public abstract String getKey();

    public abstract int getMinPlayers();

    public abstract int getMaxPlayers();

    /**
     * Returns the display name component for this role.
     */
    public Component getDisplayName() {
        return Component.translatable("minewolves.role." + getKey()).color(getTeam().getColor());
    }

    /**
     * Returns the description component for this role.
     */
    public Component getDescription() {
        return Component.translatable("minewolves.role." + getKey() + ".description");
    }

    public abstract String getIcon();

    public abstract Key getSound();

    /**
     * Returns which team this role belongs to.
     */
    public abstract Team getTeam();

    public int getNightOrder() {
        return -1; // default: not called during night
    }

    public NightTurn createNightTurn() {
        return null; // default: no night turn
    }

    /**
     * Called when this player dies.
     */
    public void onDeath() {
        // Default: no action
    }

    public List<Player> getPlayers() {
        return mineWolvesManager.roleManager.getPlayersWithRole(this);
    }
}



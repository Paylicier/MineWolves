package fr.notri1.minewolves.game.roles;

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
        return Component.translatable("minewolves.role." + getKey() + ".description").color(getTeam().getColor());
    }

    public abstract String getIcon();

    /**
     * Returns which team this role belongs to.
     */
    public abstract Team getTeam();

    /**
     * Called when the night phase begins for this role.
     * Override to implement night actions (e.g., werewolf kill, seer inspect).
     */
    public void onNightStart() {
        // Default: no action
    }

    /**
     * Called when the day phase begins.
     */
    public void onDayStart() {
        // Default: no action
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



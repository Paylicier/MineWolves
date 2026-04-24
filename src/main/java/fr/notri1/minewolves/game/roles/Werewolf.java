package fr.notri1.minewolves.game.roles;

import fr.notri1.minewolves.game.phases.turns.NightTurn;
import fr.notri1.minewolves.game.phases.turns.WerewolfTurn;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;

import java.util.List;
import java.util.Map;

import static fr.notri1.minewolves.MineWolves.mineWolvesManager;
import static fr.notri1.minewolves.Utils.COLORS;

/**
 * The Werewolf wakes up each night to eliminate a villager.
 * Wins with the Werewolves team.
 */
public class Werewolf extends Role {

    private Map<Player, NamedTextColor> colors = new java.util.HashMap<>();

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

    public NamedTextColor getColorForPlayer(Player player) {
        return colors.get(player);
    }

    public void setRandomColors() {
        List<NamedTextColor> availableColors = new java.util.ArrayList<>(COLORS);
        for (Player player : mineWolvesManager.roleManager.getPlayersWithRole(this)) {
            NamedTextColor color = availableColors.remove((int) (Math.random() * availableColors.size()));
            colors.put(player, color);
        }
    }

    public void setColorForPlayer(Player player, NamedTextColor color) {
        this.colors.put(player, color);
    }

    public int getNightOrder() {
        return 1;
    }

    public NightTurn createNightTurn() {
        return new WerewolfTurn(this);
    }
}


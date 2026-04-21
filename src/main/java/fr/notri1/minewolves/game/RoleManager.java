package fr.notri1.minewolves.game;

import fr.notri1.minewolves.game.roles.Role;
import fr.notri1.minewolves.game.roles.Team;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.ShadowColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static fr.notri1.minewolves.MineWolves.instanceContainer;

public class RoleManager {

    private Map<UUID, Role> playerRoles = new HashMap<>();
    private Map<Team, BossBar> teamBossBars = new HashMap<>();

    public void assignRole(Player player, Role role) {
        playerRoles.put(player.getUuid(), role);
    }

    public Role getRole(Player player) {
        return playerRoles.get(player.getUuid());
    }

    public void removeRole(Player player) {
        playerRoles.remove(player.getUuid());
    }

    public void updateTeamBossBar(Team team) {
        List<Player> teamPlayers = playerRoles.entrySet().stream()
                .filter(entry -> entry.getValue().getTeam() == team)
                .map(entry -> instanceContainer.getPlayerByUuid(entry.getKey()))
                .filter(p -> p != null)
                .toList();

//        String names = teamPlayers.stream().map(Player::getUsername).collect(Collectors.joining("\n"));
        // make a component with the names, each with a different color (incrementing the hue for each player)

        TranslatableComponent titleC = Component.translatable("minewolves.teammates").color(team.getColor()).decorate(TextDecoration.BOLD).shadowColor(ShadowColor.shadowColor(0, 0, 0, 0));
        Component component = Component.empty();
        component = component.append(Component.text("\uF806".repeat(22)).font(Key.key("minewolves", "roles")));
        component = component.append(titleC);
        for (int i = 0; i < teamPlayers.size(); i++) {
            Player p = teamPlayers.get(i);
            // one \uF801 per char of the previous name
            String spaces = i == 0 ? "\uF806\uF803\uF803\uF803\uF803\uF803" : "\uF803".repeat(teamPlayers.get(i - 1).getUsername().length() + i);
            component = component.append(Component.text(spaces).font(Key.key("minewolves", "roles")));
            component = component.append(Component.text(p.getUsername()).color(TextColor.color(170, i + 1, 55)).shadowColor(ShadowColor.shadowColor(0, 0, 0, 0)));
        }

        BossBar bar = getTeamBossBar(team);
        bar.name(component);
    }

    public BossBar getTeamBossBar(Team team) {
        return teamBossBars.computeIfAbsent(team, t -> BossBar.bossBar(Component.empty(), 0f, BossBar.Color.WHITE, BossBar.Overlay.PROGRESS));
    }

    public List<Role> getAliveRoles() {
        return playerRoles.values().stream().distinct().toList();
    }


    public List<Player> getPlayersWithRole(Role role) {
        return playerRoles.entrySet().stream()
                .filter(entry -> entry.getValue().equals(role))
                .map(entry -> instanceContainer.getPlayerByUuid(entry.getKey()))
                .filter(player -> player != null)
                .collect(Collectors.toList());
    }
}
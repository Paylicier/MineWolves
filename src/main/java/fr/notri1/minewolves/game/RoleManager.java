package fr.notri1.minewolves.game;

import fr.notri1.minewolves.game.roles.Role;
import net.minestom.server.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static fr.notri1.minewolves.MineWolves.instanceContainer;

public class RoleManager {

    private Map<UUID, Role> playerRoles = new HashMap<>();

    public void assignRole(Player player, Role role) {
        playerRoles.put(player.getUuid(), role);
    }

    public Role getRole(Player player) {
        return playerRoles.get(player.getUuid());
    }

    public void removeRole(Player player) {
        playerRoles.remove(player.getUuid());
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

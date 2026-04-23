package fr.notri1.minewolves.game;

import fr.notri1.minewolves.game.roles.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles role selection and team balancing for MineWolves.
 * <p>
 * The algorithm ensures:
 * <ul>
 *   <li>Each role respects its min/max player constraints</li>
 *   <li>Werewolves never exceed ~1/3 of total players (balanced ratio)</li>
 *   <li>All mandatory roles (minPlayers > 0) are always included</li>
 *   <li>Special village roles are prioritized over simple Villagers</li>
 *   <li>Remaining slots are filled with Villagers</li>
 * </ul>
 */
public class RoleSelection {

    /**
     * All available roles in the game, registered once.
     */
    private static final List<Role> ALL_ROLES = List.of(
            new Werewolf(),
            new Witch(),
            new Seer(),
            new Hunter(),
            new Villager()
    );

    /**
     * Generates a balanced list of roles for the given player count.
     * Each entry in the returned list represents one player's role.
     *
     * @param playerCount the number of players in the game
     * @return a shuffled list of roles, one per player
     * @throws IllegalArgumentException if playerCount is too low to satisfy mandatory roles
     */
    public static List<Role> getRolesForPlayerCount(int playerCount) {
        List<Role> selectedRoles = new ArrayList<>();

        int targetWerewolves = Math.max(1, Math.round((float) playerCount / 3));

        for (Role role : ALL_ROLES) {
            for (int i = 0; i < role.getMinPlayers(); i++) {
                selectedRoles.add(role);
            }
        }

        if (selectedRoles.size() > playerCount) {
            throw new IllegalArgumentException(
                    "Not enough players (" + playerCount + ") to fill mandatory roles (" + selectedRoles.size() + ")");
        }

        int currentWerewolves = countByTeam(selectedRoles, Team.WEREWOLVES);
        int remainingSlots = playerCount - selectedRoles.size();

        List<Role> werewolvesRoles = getRolesByTeam(Team.WEREWOLVES);
        Collections.shuffle(werewolvesRoles); // nb: a bit useless rn since we only have one werewolf role, but futureproof ykyk

        for (Role role : werewolvesRoles) {
            int currentCount = countRole(selectedRoles, role);
            int canAdd = role.getMaxPlayers() - currentCount;
            int wolvesNeeded = targetWerewolves - currentWerewolves;

            int toAdd = Math.min(canAdd, Math.min(wolvesNeeded, remainingSlots));
            for (int i = 0; i < toAdd; i++) {
                selectedRoles.add(role);
                currentWerewolves++;
                remainingSlots--;
            }
        }

        List<Role> specialVillageRoles = new ArrayList<>(getRolesByTeam(Team.VILLAGE).stream()
                .filter(r -> !(r instanceof Villager))
                .toList());
        Collections.shuffle(specialVillageRoles); // Shuffle to add variety in role selection

        for (Role role : specialVillageRoles) {
            if (remainingSlots <= 0) break;

            int currentCount = countRole(selectedRoles, role);
            int canAdd = role.getMaxPlayers() - currentCount;
            int toAdd = Math.min(canAdd, remainingSlots);

            for (int i = 0; i < toAdd; i++) {
                selectedRoles.add(role);
                remainingSlots--;
            }
        }

        Role villager = ALL_ROLES.stream()
                .filter(r -> r instanceof Villager)
                .findFirst()
                .orElseThrow();

        for (int i = 0; i < remainingSlots; i++) {
            selectedRoles.add(villager);
        }

        Collections.shuffle(selectedRoles);

        return selectedRoles;
    }

    /**
     * Counts how many roles in the list belong to the given team.
     */
    private static int countByTeam(List<Role> roles, Team team) {
        return (int) roles.stream().filter(r -> r.getTeam() == team).count();
    }

    /**
     * Counts occurrences of a specific role (by key) in the list.
     */
    private static int countRole(List<Role> roles, Role target) {
        return (int) roles.stream().filter(r -> r.getKey().equals(target.getKey())).count();
    }

    /**
     * Returns all registered roles belonging to the given team.
     */
    private static List<Role> getRolesByTeam(Team team) {
        return ALL_ROLES.stream()
                .filter(r -> r.getTeam() == team)
                .collect(Collectors.toList());
    }
}

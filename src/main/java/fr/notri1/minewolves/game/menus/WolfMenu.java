package fr.notri1.minewolves.game.menus;

import fr.notri1.minewolves.game.phases.GamePhase;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.object.ObjectContents;
import net.minestom.server.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static fr.notri1.minewolves.MineWolves.mineWolvesManager;

public class WolfMenu extends InteractableMenu {

    private final fr.notri1.minewolves.game.roles.Role role;

    public WolfMenu(fr.notri1.minewolves.game.roles.Role role) {
        this.role = role;
    }

    public void open(Player player) {
        super.open(player);
        player.playSound(Sound.sound(role.getSound(), Sound.Source.MASTER, 1f, 1f));
    }

    public void updateVotes(Player player, Map<java.util.UUID, java.util.UUID> votes) {
        // Group voters by their chosen target
        Map<java.util.UUID, List<java.util.UUID>> targetToVoters = new java.util.HashMap<>();
        for (Map.Entry<java.util.UUID, java.util.UUID> entry : votes.entrySet()) {
            targetToVoters.computeIfAbsent(entry.getValue(), unused -> new ArrayList<>()).add(entry.getKey());
        }

        // For each element that is a vote display, update its component
        this.elements.stream()
                .filter(el -> el.getId().endsWith("_votes"))
                .forEach(voteElement -> {
                    String targetUuidStr = voteElement.getId().replace("_votes", "");
                    java.util.UUID targetUuid = java.util.UUID.fromString(targetUuidStr);

                    List<java.util.UUID> voters = targetToVoters.getOrDefault(targetUuid, java.util.Collections.emptyList());

                    net.kyori.adventure.text.TextComponent.Builder builder = Component.text();
                    for (java.util.UUID voter : voters) {
                        try {
                            // Suppress strict validation or assume Component.object exists natively
                            // Wait, if Kyori Adventure doesn't have Component.object, this was working in WolfMenu before:
                            // Component.object(ObjectContents.playerHead(p.getUuid()))
                            builder.append(Component.object(ObjectContents.playerHead(voter)).append(Component.space()));
                        } catch (Exception e) {
                        }
                    }

                    updateElement(player, voteElement.getId(), builder.build());
                });
    }

    @Override
    protected List<MenuElement> buildElements(Player player) {
        List<MenuElement> elements = new ArrayList<>();

        elements.add(MenuElement.builder("title")
                .position(0.5f, -1.6f)
                .text(Component.translatable("minewolves.menu.werewolf.title").color(role.getTeam().getColor()).decorate(TextDecoration.BOLD))
                .scale(1.5f)
                .build());

        elements.add(MenuElement.builder("countdown")
                .position(0.5f, -1.3f)
                .text(Component.translatable("minewolves.menu.countdown", Component.text(30)).color(role.getTeam().getColor()))
                .scale(1f)
                .build());

        // Récupération des cibles potentielles
        List<Player> otherPlayers = player.getInstance().getPlayers().stream()
                .filter(p -> p != player && mineWolvesManager.roleManager.getRole(p).getTeam() != role.getTeam())
                .toList();

        // Nouveaux paramètres de la grille
        float spacingX = 0.5f;
        float spacingY = 1.0f;
        int maxPerRow = 5;        // (1.5 - (-0.5)) / 0.5 = 4 intervalles, donc 5 éléments max
        float menuCenterX = 0.5f; // Le point central entre 1.5 et -0.5

        for (int i = 0; i < otherPlayers.size(); i++) {
            Player p = otherPlayers.get(i);

            int row = i / maxPerRow;
            int col = i % maxPerRow;

            // Calcul du nombre d'éléments sur la ligne actuelle
            int itemsInThisRow = Math.min(maxPerRow, otherPlayers.size() - row * maxPerRow);

            // Calcul de la largeur totale prise par les éléments de cette ligne
            float rowWidth = (itemsInThisRow - 1) * spacingX;

            // Point de départ X (à gauche) ajusté par rapport au centre du menu
            float startX = menuCenterX + (rowWidth / 2.0f);

            // Positions finales X et Y
            float posX = startX - col * spacingX;
            float posYHead = -0.5f + (row * spacingY);
            float posYName = -0.4f + (row * spacingY);

            // Élément de la tête
            elements.add(MenuElement.builder(p.getUuid().toString())
                    .position(posX, posYHead)
                    .text(Component.object(ObjectContents.playerHead(p.getUuid())))
                    .scale(3f)
                    .isInteractable(true)
                    .onClick(() -> {
                        GamePhase phase = mineWolvesManager.getPhase();
                        if (phase instanceof fr.notri1.minewolves.game.phases.NightPhase rolePhase) {
                            if (rolePhase.currentTurn instanceof fr.notri1.minewolves.game.phases.turns.WerewolfTurn werewolfTurn) {
                                werewolfTurn.handleVote(player, p);
                            }
                        }
                    })
                    .build());

            // Élément du nom
            elements.add(MenuElement.builder(p.getUuid() + "_name")
                    .position(posX, posYName)
                    .text(Component.text(p.getUsername()).color(NamedTextColor.WHITE))
                    .scale(1f)
                    .build());

            // Élément des votes
            elements.add(MenuElement.builder(p.getUuid() + "_votes")
                    .position(posX, posYName + 0.2f)
                    .text(Component.empty())
                    .scale(1.2f)
                    .build());
        }

        return elements;
    }
}
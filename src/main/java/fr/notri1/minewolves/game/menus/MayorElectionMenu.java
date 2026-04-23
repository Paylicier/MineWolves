package fr.notri1.minewolves.game.menus;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.object.ObjectContents;
import net.minestom.server.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;

import static fr.notri1.minewolves.MineWolves.mineWolvesManager;

public class MayorElectionMenu extends InteractableMenu {

    private final BiConsumer<Player, Player> voteHandler;

    public MayorElectionMenu(BiConsumer<Player, Player> voteHandler) {
        this.voteHandler = Objects.requireNonNull(voteHandler, "voteHandler");
    }

    public void open(Player player) {
        player.playSound(Sound.sound(Key.key("block.note_block.bell"), Sound.Source.MASTER, 1f, 1f));
        super.open(player);
    }

    public void handleVote(Player voter, Player target) {
        voteHandler.accept(voter, target);
    }

    public void updateVotes(Player player, Map<java.util.UUID, java.util.UUID> votes) {
        Map<java.util.UUID, List<java.util.UUID>> targetToVoters = new java.util.HashMap<>();
        for (Map.Entry<java.util.UUID, java.util.UUID> entry : votes.entrySet()) {
            targetToVoters.putIfAbsent(entry.getValue(), new ArrayList<>());
            targetToVoters.get(entry.getValue()).add(entry.getKey());
        }

        this.elements.stream()
                .filter(el -> el.getId().endsWith("_votes"))
                .forEach(voteElement -> {
                    String targetUuidStr = voteElement.getId().replace("_votes", "");
                    java.util.UUID targetUuid = java.util.UUID.fromString(targetUuidStr);

                    List<java.util.UUID> voters = targetToVoters.getOrDefault(targetUuid, java.util.Collections.emptyList());

                    net.kyori.adventure.text.TextComponent.Builder builder = Component.text();
                    for (java.util.UUID voter : voters) {
                        try {
                            builder.append(Component.object(ObjectContents.playerHead(voter)).append(Component.space()));
                        } catch (Exception ignored) {
                            builder.append(Component.space());
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
                .text(Component.translatable("minewolves.menu.mayor_election.title").color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD))
                .scale(1.5f)
                .build());

        elements.add(MenuElement.builder("subtitle")
                .position(0.5f, -1.3f)
                .text(Component.translatable("minewolves.menu.mayor_election.subtitle").color(NamedTextColor.WHITE))
                .scale(0.9f)
                .build());

        elements.add(MenuElement.builder("countdown")
                .position(0.5f, -1.0f)
                .text(Component.translatable("minewolves.menu.countdown", Component.text(70)).color(NamedTextColor.WHITE))
                .scale(1f)
                .build());

        List<Player> candidates = player.getInstance().getPlayers().stream()
                .filter(p -> mineWolvesManager.roleManager.getRole(p) != null)
                .toList();

        float spacingX = 0.5f;
        float spacingY = 1.0f;
        int maxPerRow = 5;
        float menuCenterX = 0.5f;

        for (int i = 0; i < candidates.size(); i++) {
            Player candidate = candidates.get(i);

            int row = i / maxPerRow;
            int col = i % maxPerRow;

            int itemsInThisRow = Math.min(maxPerRow, candidates.size() - row * maxPerRow);
            float rowWidth = (itemsInThisRow - 1) * spacingX;
            float startX = menuCenterX + (rowWidth / 2.0f);

            float posX = startX - col * spacingX;
            float posYHead = -0.5f + (row * spacingY);
            float posYName = -0.4f + (row * spacingY);

            elements.add(MenuElement.builder(candidate.getUuid().toString())
                    .position(posX, posYHead)
                    .text(Component.object(ObjectContents.playerHead(candidate.getUuid())))
                    .scale(3f)
                    .isInteractable(true)
                    .onClick(() -> handleVote(player, candidate))
                    .build());

            elements.add(MenuElement.builder(candidate.getUuid() + "_name")
                    .position(posX, posYName)
                    .text(Component.text(candidate.getUsername()).color(NamedTextColor.WHITE))
                    .scale(1f)
                    .build());

            elements.add(MenuElement.builder(candidate.getUuid() + "_votes")
                    .position(posX, posYName + 0.2f)
                    .text(Component.empty())
                    .scale(1.2f)
                    .build());
        }

        return elements;
    }
}
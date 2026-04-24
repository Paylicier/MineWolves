package fr.notri1.minewolves.game.menus;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SeerMenu extends InteractableMenu {

    private final fr.notri1.minewolves.game.roles.Seer role;
    private final Consumer<Player> onPlayerSelected;

    public SeerMenu(fr.notri1.minewolves.game.roles.Seer role, Consumer<Player> onPlayerSelected) {
        this.role = role;
        this.onPlayerSelected = onPlayerSelected;
    }

    public void open(Player player) {
        super.open(player);
        player.playSound(Sound.sound(role.getSound(), Sound.Source.MASTER, 1f, 1f));
    }

    @Override
    protected List<MenuElement> buildElements(Player player) {
        List<MenuElement> elements = new ArrayList<>();

        elements.add(MenuElement.builder("title")
                .position(0.5f, -1.6f)
                .text(Component.translatable("minewolves.menu.seer.title")
                        .color(net.kyori.adventure.text.format.NamedTextColor.LIGHT_PURPLE)
                        .decorate(net.kyori.adventure.text.format.TextDecoration.BOLD))
                .scale(1.5f)
                .build()
        );

        elements.add(MenuElement.builder("countdown")
                .position(0.5f, -1.3f)
                .text(Component.translatable("minewolves.menu.countdown", Component.text(20)).color(role.getTeam().getColor()))
                .scale(1f)
                .build());

        List<Player> candidates = player.getInstance().getPlayers().stream()
                .filter(p -> p != player && fr.notri1.minewolves.MineWolves.mineWolvesManager.roleManager.getRole(p) != null)
                .toList();

        float spacingX = 0.5f;
        float spacingY = 1.0f;
        int maxPerRow = 5;
        float menuCenterX = 0.5f;

        List<Player> revealed = role.getRevealedPlayers();

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

            boolean isRevealed = revealed.contains(candidate);

            elements.add(MenuElement.builder(candidate.getUuid().toString())
                    .position(posX, posYHead)
                    .text(Component.object(net.kyori.adventure.text.object.ObjectContents.playerHead(candidate.getUuid())))
                    .scale(3f)
                    .isInteractable(!isRevealed)
                    .onClick(isRevealed ? null : () -> {
                        if (this.onPlayerSelected != null) {
                            this.onPlayerSelected.accept(candidate);
                        }
                    })
                    .build());

            elements.add(MenuElement.builder(candidate.getUuid() + "_name")
                    .position(posX, posYName)
                    .text(Component.text(candidate.getUsername()).color(net.kyori.adventure.text.format.NamedTextColor.WHITE))
                    .scale(1f)
                    .build());

            if (isRevealed) {
                fr.notri1.minewolves.game.roles.Role targetRole = fr.notri1.minewolves.MineWolves.mineWolvesManager.roleManager.getRole(candidate);
                if (targetRole != null) {
                    elements.add(MenuElement.builder(candidate.getUuid() + "_role")
                            .position(posX + 0.15f, posYHead - 0.15f)
                            .text(Component.text(targetRole.getIcon()).font(Key.key("minewolves", "roles")))
                            .scale(0.2f)
                            .build());
                }
            }
        }

        return elements;
    }
}
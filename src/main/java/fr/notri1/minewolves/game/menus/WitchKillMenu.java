package fr.notri1.minewolves.game.menus;

import fr.notri1.minewolves.game.phases.turns.WitchTurn;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.object.ObjectContents;
import net.minestom.server.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static fr.notri1.minewolves.MineWolves.mineWolvesManager;

public class WitchKillMenu extends InteractableMenu {

    private final fr.notri1.minewolves.game.roles.Witch role;
    private final WitchTurn turn;

    public WitchKillMenu(fr.notri1.minewolves.game.roles.Witch role, WitchTurn turn) {
        this.role = role;
        this.turn = turn;
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
                .text(Component.translatable("minewolves.menu.werewolf.title").color(role.getTeam().getColor()).decorate(TextDecoration.BOLD)) // recycling translations
                .scale(1.5f)
                .build());

        elements.add(MenuElement.builder("countdown")
                .position(0.5f, -1.3f)
                .text(Component.translatable("minewolves.menu.countdown", Component.text(20)).color(role.getTeam().getColor()))
                .scale(1f)
                .build());

        List<Player> otherPlayers = player.getInstance().getPlayers().stream()
                .filter(p -> p != player && (mineWolvesManager.roleManager.getRole(p) != null))
                .toList();

        float spacingX = 0.5f;
        float spacingY = 1.0f;
        int maxPerRow = 5;
        float menuCenterX = 0.5f;

        for (int i = 0; i < otherPlayers.size(); i++) {
            Player p = otherPlayers.get(i);

            int row = i / maxPerRow;
            int col = i % maxPerRow;

            int itemsInThisRow = Math.min(maxPerRow, otherPlayers.size() - row * maxPerRow);

            float rowWidth = (itemsInThisRow - 1) * spacingX;

            float startX = menuCenterX + (rowWidth / 2.0f);

            float posX = startX - col * spacingX;
            float posYHead = -0.5f + (row * spacingY);
            float posYName = -0.4f + (row * spacingY);

            elements.add(MenuElement.builder(p.getUuid().toString())
                    .position(posX, posYHead)
                    .text(Component.object(ObjectContents.playerHead(p.getUuid())))
                    .scale(3f)
                    .isInteractable(true)
                    .onClick(() -> turn.onKillTarget(player, p))
                    .build());

            elements.add(MenuElement.builder(p.getUuid() + "_name")
                    .position(posX, posYName)
                    .text(Component.text(p.getUsername()).color(NamedTextColor.WHITE))
                    .scale(1f)
                    .build());
        }

        return elements;
    }
}
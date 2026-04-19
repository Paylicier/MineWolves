package fr.notri1.minewolves.game.menus;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SeerMenu extends InteractableMenu {

    private final fr.notri1.minewolves.game.roles.Role role;

    public SeerMenu(fr.notri1.minewolves.game.roles.Role role) {
        this.role = role;
    }

    public void open(Player player) {
        super.open(player);
        player.playSound(Sound.sound(role.getSound(), Sound.Source.MASTER, 1f, 1f));
    }

    @Override
    protected List<MenuElement> buildElements(Player player) {
        List<MenuElement> elements = new ArrayList<>();

        elements.add(MenuElement.builder("1")
                .position(-1f, 0f)
                .text(Component.text(role.getIcon())
                        .font(Key.key("minewolves", "roles")))
                .scale(0.5f)
                .isInteractable(true)
                .build());
        elements.add(MenuElement.builder("2")
                .position(-0.5f, 0f)
                .text(Component.text(role.getIcon())
                        .font(Key.key("minewolves", "roles")))
                .scale(0.5f)
                .isInteractable(false)
                .build());
        elements.add(MenuElement.builder("3")
                .position(0f, 0f)
                .text(Component.text(role.getIcon())
                        .font(Key.key("minewolves", "roles")))
                .scale(0.5f)
                .isInteractable(true)
                .build());

        return elements;
    }
}
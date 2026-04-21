package fr.notri1.minewolves.game.menus;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class RoleMenu extends Menu {

    private final fr.notri1.minewolves.game.roles.Role role;

    public RoleMenu(fr.notri1.minewolves.game.roles.Role role) {
        this.role = role;
    }

    public void open(Player player) {
        super.open(player);
        player.playSound(Sound.sound(role.getSound(), Sound.Source.MASTER, 1f, 1f));
    }

    @Override
    protected List<MenuElement> buildElements(Player player) {
        List<MenuElement> elements = new ArrayList<>();

        // Title
        elements.add(MenuElement.builder("title")
                .position(0f, -1.6f)
                .text(Component.translatable("minewolves.menu.roles.title")
                        .color(NamedTextColor.GOLD)
                        .decorate(TextDecoration.BOLD))
                .scale(1.5f)
                .build()
        );

        // Role Icon
        elements.add(MenuElement.builder("icon")
                .position(0f, -1.6f)
                .text(Component.text(role.getIcon())
                        .font(Key.key("minewolves", "roles")))
                .scale(2f)
                .build());

        // Role Name
        elements.add(MenuElement.builder("name")
                .position(0f, 0.6f)
                .text(role.getDisplayName().decorate(TextDecoration.BOLD))
                .scale(1.8f)
                .build());

        // Role desc
        elements.add(MenuElement.builder("description")
                .position(0f, 0.9f)
                .text(role.getDescription().color(NamedTextColor.GRAY))
                .scale(0.85f)
                .build());

        // Footer
        elements.add(MenuElement.builder("footer")
                .position(0f, 1.1f)
                .text(Component.translatable("minewolves.menu.roles.footer")
                        .color(NamedTextColor.DARK_GRAY)
                        .decorate(TextDecoration.ITALIC))
                .scale(0.6f)
                .build());

        return elements;
    }
}
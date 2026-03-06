package fr.notri1.minewolves.game.roles;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public enum Team {
    VILLAGE("Village", NamedTextColor.GREEN),
    WEREWOLVES("Werewolves", NamedTextColor.DARK_RED),
    SOLO("Solo", NamedTextColor.GRAY);

    private final String name;
    private final TextColor color;

    Team(String name, TextColor color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public TextColor getColor() {
        return color;
    }
}


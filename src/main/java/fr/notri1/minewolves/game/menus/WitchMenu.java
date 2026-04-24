package fr.notri1.minewolves.game.menus;

import fr.notri1.minewolves.game.phases.GamePhase;
import fr.notri1.minewolves.game.phases.turns.WerewolfTurn;
import fr.notri1.minewolves.game.phases.turns.WitchTurn;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.object.ObjectContents;
import net.minestom.server.component.DataComponent;
import net.minestom.server.component.DataComponents;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.component.PotionContents;
import net.minestom.server.potion.PotionType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static fr.notri1.minewolves.MineWolves.mineWolvesManager;

public class WitchMenu extends InteractableMenu {

    private final fr.notri1.minewolves.game.roles.Witch role;
    private final WitchTurn turn;

    public WitchMenu(fr.notri1.minewolves.game.roles.Witch role, WitchTurn turn) {
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
                .text(Component.translatable("minewolves.menu.witch.title").color(role.getTeam().getColor()).decorate(TextDecoration.BOLD))
                .scale(1.5f)
                .build());

        elements.add(MenuElement.builder("countdown")
                .position(0.5f, -1.3f)
                .text(Component.translatable("minewolves.menu.countdown", Component.text(20)).color(role.getTeam().getColor()))
                .scale(1f)
                .build());

        elements.add(MenuElement.builder("victim_label")
                .position(0.5f, -1f)
                .text(Component.translatable("minewolves.menu.witch.victim", Component.text(20))) // "This player died tonight:"
                .scale(1f)
                .build());

        elements.add(MenuElement.builder("victim_head")
                .position(0.5f, -0.7f)
                .text(mineWolvesManager.getPlayersToEliminate().isEmpty() ? Component.text(" ") : Component.object(ObjectContents.playerHead(mineWolvesManager.getPlayersToEliminate().getFirst().getUuid())))
                .scale(1.5f)
                .build());

        elements.add(MenuElement.builder("victim_name")
                .position(0.5f, -0.5f)
                .text(mineWolvesManager.getPlayersToEliminate().isEmpty() ? Component.text("N/A") : Component.text(mineWolvesManager.getPlayersToEliminate().get(0).getUsername()).color(NamedTextColor.RED))
                .scale(1f)
                .build());

        // Potions

        elements.add(MenuElement.builder("revive_potion")
                .position(1.5f, 0.1f)
                .item(role.hasHealingPotion() ? (ItemStack.of(Material.POTION).with(DataComponents.POTION_CONTENTS, new PotionContents(PotionType.HEALING))) : ItemStack.of(Material.GLASS_BOTTLE))
                .scale(1.6f)
                .isInteractable(true)
                .onClick(() -> turn.onRevive(player))
                .build());

        elements.add(MenuElement.builder("revive_label")
                .position(1.5f, 0.9f)
                .text(Component.translatable("minewolves.menu.witch.revive").color(NamedTextColor.WHITE)) // "Revive the victim"
                .scale(1f)
                .build());

        elements.add(MenuElement.builder("do_nothing")
                .position(0.5f, 0.1f)
                .item(ItemStack.of(Material.DEAD_BUSH))
                .scale(1.6f)
                .isInteractable(true)
                .onClick(() -> turn.onDoNothing(player))
                .build());

        elements.add(MenuElement.builder("do_nothing_label")
                .position(0.5f, 0.9f)
                .text(Component.translatable("minewolves.menu.witch.do_nothing").color(NamedTextColor.WHITE)) // "Do nothing"
                .scale(1f)
                .build());

        elements.add(MenuElement.builder("kill_potion")
                .position(-0.5f, 0.1f)
                .item(role.hasKillingPotion() ? (ItemStack.of(Material.POTION).with(DataComponents.POTION_CONTENTS, new PotionContents(PotionType.WEAKNESS))) : ItemStack.of(Material.GLASS_BOTTLE))
                .scale(1.6f)
                .isInteractable(true)
                .onClick(() -> turn.onKill(player))
                .build());

        elements.add(MenuElement.builder("kill_label")
                .position(-0.5f, 0.9f)
                .text(Component.translatable("minewolves.menu.witch.kill").color(NamedTextColor.WHITE)) // "Kill another player"
                .scale(1f)
                .build());

        return elements;
    }
}
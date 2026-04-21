package fr.notri1.minewolves.game.menus;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.timer.Scheduler;
import net.minestom.server.timer.TaskSchedule;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Like Menu but interactable
 */
public abstract class InteractableMenu extends Menu {

    private int currentIndex = 0;

    /**
     * send action bar to player on open
     */
    @Override
    public void open(Player player) {
        Scheduler scheduler = MinecraftServer.getSchedulerManager();
        AtomicBoolean isFirstRun = new AtomicBoolean(true);
        scheduler.submitTask(() -> {
            if (!hasMenuOpen(player) && !isFirstRun.get()) {
                player.sendActionBar(Component.text(" "));
                return TaskSchedule.stop();
            }
            player.sendActionBar(Component.translatable("minewolves.menu.interactable", "", Component.keybind("key.use")).color(NamedTextColor.GRAY));
            isFirstRun.set(false);
            return TaskSchedule.seconds(1);
        });
        super.open(player);
    }

    /**
     * Called in the scroll event thing
     */
    public void handleScroll(Player player, int direction) {
        List<MenuElement> interactableElements = this.elements.stream().filter(MenuElement::isInteractable).toList();
        if (interactableElements.isEmpty()) return;

        int maxElements = interactableElements.size();


        MenuElement oldEl = interactableElements.get(currentIndex);
        updateElementScale(player, oldEl.getId(), 1.0f, 1);

        currentIndex = (currentIndex + direction) % maxElements;
        if (currentIndex < 0) currentIndex += maxElements;

        MenuElement newEl = interactableElements.get(currentIndex);
        updateElementScale(player, newEl.getId(), 1.2f, 1);
    }

    public void handleInteract(Player player) {
        List<MenuElement> interactableElements = this.elements.stream().filter(MenuElement::isInteractable).toList();
        if (interactableElements.isEmpty()) return;

        MenuElement element = interactableElements.get(currentIndex);

        player.sendMessage(Component.text("Clicked on element " + element.getId() + ", index: " + currentIndex));
        player.playSound(Sound.sound(Key.key("minecraft", "ui.button.click"), Sound.Source.UI, 1f, 1f));

        Runnable onClick = element.getOnClick();
        if (onClick == null) {
            player.sendMessage(Component.text("uh, this button doesn't do anything (too lazy to translate this)").color(NamedTextColor.RED));
            return;
        }
        element.getOnClick().run();
    }
}
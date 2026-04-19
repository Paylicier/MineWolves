package fr.notri1.minewolves.listeners;

import fr.notri1.minewolves.game.menus.InteractableMenu;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerChangeHeldSlotEvent;

import static fr.notri1.minewolves.game.menus.Menu.getSession;
import static fr.notri1.minewolves.game.menus.Menu.hasMenuOpen;

public class PlayerScroll implements EventListener<PlayerChangeHeldSlotEvent> {

    @Override
    public Class<PlayerChangeHeldSlotEvent> eventType() {
        return PlayerChangeHeldSlotEvent.class;
    }

    @Override
    public Result run(PlayerChangeHeldSlotEvent event) {

        Player player = event.getPlayer();

        if (!hasMenuOpen(player)) return Result.SUCCESS;
        if (!(getSession(player).getMenu() instanceof InteractableMenu)) return Result.SUCCESS;
        event.setCancelled(true);
        int newSlot = event.getNewSlot();
        int oldSlot = event.getOldSlot();
        int scrollDirection = (int) Math.signum(newSlot - oldSlot);
        if (scrollDirection == 0) return Result.SUCCESS;
        ((InteractableMenu) getSession(player).getMenu()).handleScroll(player, scrollDirection);
        return Result.SUCCESS;
    }
}

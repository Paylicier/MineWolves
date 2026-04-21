package fr.notri1.minewolves.listeners;

import fr.notri1.minewolves.game.menus.InteractableMenu;
import fr.notri1.minewolves.game.menus.Menu;
import fr.notri1.minewolves.game.menus.Menu.MenuSession;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerHandAnimationEvent;
import org.jspecify.annotations.NonNull;

import static fr.notri1.minewolves.game.menus.Menu.getSession;
import static fr.notri1.minewolves.game.menus.Menu.hasMenuOpen;

public class PlayerLeftClick implements EventListener<PlayerHandAnimationEvent> {

    @Override
    public @NonNull Class<PlayerHandAnimationEvent> eventType() {
        return PlayerHandAnimationEvent.class;
    }

    @Override
    public @NonNull Result run(PlayerHandAnimationEvent event) {
        Player player = event.getPlayer();
        if ((event.getHand() == PlayerHand.OFF) || !hasMenuOpen(player)) return Result.SUCCESS;

        MenuSession session = getSession(player);
        Menu menu = session.getMenu();
        if (!(menu instanceof InteractableMenu)) return Result.SUCCESS;

        ((InteractableMenu) menu).handleInteract(player);


        return Result.SUCCESS;
    }
}

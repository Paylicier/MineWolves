package fr.notri1.minewolves.game.phases.turns;

import fr.notri1.minewolves.game.phases.NightPhase;
import fr.notri1.minewolves.game.roles.Role;
import fr.notri1.minewolves.game.roles.Seer;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.timer.TaskSchedule;

import java.util.concurrent.atomic.AtomicInteger;

import static fr.notri1.minewolves.MineWolves.mineWolvesManager;

public class SeerTurn extends NightTurn {

    private final Seer role;

    public SeerTurn(Seer role) {
        this.role = role;
    }


    @Override
    public void onTurn() {
        role.getPlayers().forEach(player -> {
            new fr.notri1.minewolves.game.menus.SeerMenu(role, target -> {
                fr.notri1.minewolves.game.menus.Menu.closeStatic(player);
                role.getRevealedPlayers().add(target);
                Role targetRole = fr.notri1.minewolves.MineWolves.mineWolvesManager.roleManager.getRole(target);
                if (targetRole != null) {
                    player.sendMessage(Component.translatable("minewolves.role.seer.inspect", Component.text(target.getUsername()).color(targetRole.getTeam().getColor()), targetRole.getDisplayName()));
                }
                endTurn();
            }).open(player);
        });

        AtomicInteger countdown = new AtomicInteger(15);
        MinecraftServer.getSchedulerManager().submitTask(() -> {
            if (turnEnded) return TaskSchedule.stop();

            int current = countdown.getAndDecrement();

            if (current < 0) {
                endTurn();
                return TaskSchedule.stop();
            }

            role.getPlayers().forEach(player -> {
                fr.notri1.minewolves.game.menus.Menu.MenuSession session = fr.notri1.minewolves.game.menus.Menu.getSession(player);
                if (session != null && session.getMenu() instanceof fr.notri1.minewolves.game.menus.SeerMenu seerMenu) {
                    seerMenu.updateElement(player, "countdown", net.kyori.adventure.text.Component.translatable("minewolves.menu.countdown", net.kyori.adventure.text.Component.text(current)).color(net.kyori.adventure.text.format.NamedTextColor.GRAY));
                }
            });

            return TaskSchedule.seconds(1);
        });
    }

    private boolean turnEnded = false;

    private void endTurn() {
        if (turnEnded) return;
        turnEnded = true;
        role.getPlayers().forEach(fr.notri1.minewolves.game.menus.Menu::closeStatic);
        ((NightPhase) mineWolvesManager.getPhase()).nextTurn();
    }
}

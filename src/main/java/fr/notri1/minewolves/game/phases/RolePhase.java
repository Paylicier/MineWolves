package fr.notri1.minewolves.game.phases;

import fr.notri1.minewolves.game.RoleSelection;
import fr.notri1.minewolves.game.roles.Role;
import net.minestom.server.MinecraftServer;
import net.minestom.server.timer.Scheduler;
import net.minestom.server.timer.TaskSchedule;

import java.util.List;

import static fr.notri1.minewolves.MineWolves.instanceContainer;
import static fr.notri1.minewolves.MineWolves.mineWolvesManager;

public class RolePhase extends GamePhase {

    @Override
    public void onStart() {

        // Assign roles

        List<Role> roleSelection = RoleSelection.getRolesForPlayerCount(mineWolvesManager.getEffectivePlayerCount());

        instanceContainer.getPlayers().forEach(player -> {
            mineWolvesManager.roleManager.assignRole(player, roleSelection.getFirst());
            roleSelection.removeFirst();
        });

        // Show roles

        Scheduler scheduler = MinecraftServer.getSchedulerManager();

        instanceContainer.getPlayers().forEach(player -> {
            fr.notri1.minewolves.game.menus.Role roleMenu = new fr.notri1.minewolves.game.menus.Role(mineWolvesManager.roleManager.getRole(player));
            roleMenu.open(player);
            scheduler.buildTask(() -> {
                roleMenu.close(player);
            }).delay(TaskSchedule.seconds(5)).schedule();
        });

        scheduler.buildTask(() -> {
            mineWolvesManager.setPhase(new NightPhase());
        }).delay(TaskSchedule.seconds(5)).schedule();
    }

    @Override
    public void onEnd() {

    }
}

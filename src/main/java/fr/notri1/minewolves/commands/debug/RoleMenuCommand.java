package fr.notri1.minewolves.commands.debug;

import fr.notri1.minewolves.game.menus.Role;
import fr.notri1.minewolves.game.roles.Werewolf;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.Player;
import net.minestom.server.timer.Scheduler;
import net.minestom.server.timer.TaskSchedule;

public class RoleMenuCommand extends Command {

    public RoleMenuCommand() {
        super("rolemenu");

        setDefaultExecutor((sender, context) -> {
            Scheduler scheduler = MinecraftServer.getSchedulerManager();
            Role role = new Role(new Werewolf());
            role.open((Player) sender);
            scheduler.buildTask(() -> {
                role.close((Player) sender);
            }).delay(TaskSchedule.seconds(5)).schedule();
        });
    }
}

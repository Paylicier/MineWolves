package fr.notri1.minewolves.commands.debug;

import fr.notri1.minewolves.game.menus.SeerMenu;
import fr.notri1.minewolves.game.roles.Werewolf;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.Player;
import net.minestom.server.timer.Scheduler;
import net.minestom.server.timer.TaskSchedule;

public class SeerMenuCommand extends Command {

    public SeerMenuCommand() {
        super("seermenu");

        setDefaultExecutor((sender, context) -> {
            Scheduler scheduler = MinecraftServer.getSchedulerManager();
            SeerMenu roleMenu = new SeerMenu(new Werewolf());
            roleMenu.open((Player) sender);
            scheduler.buildTask(() -> {
                roleMenu.close((Player) sender);
            }).delay(TaskSchedule.seconds(5)).schedule();
        });
    }
}

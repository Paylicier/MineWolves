package fr.notri1.minewolves.commands.debug;

import fr.notri1.minewolves.MineWolves;
import net.minestom.server.command.builder.Command;

public class StartCommand extends Command {

    public StartCommand() {
        super("start");

        setDefaultExecutor((sender, context) -> {
            sender.sendMessage("Starting the game...");
            MineWolves.mineWolvesManager.startGame();
        });
    }
}

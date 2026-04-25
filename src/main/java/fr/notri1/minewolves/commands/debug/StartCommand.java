package fr.notri1.minewolves.commands.debug;

import fr.notri1.minewolves.MineWolves;
import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.Player;

public class StartCommand extends Command {

    public StartCommand() {
        super("start");

        setDefaultExecutor((sender, context) -> {
            if(((Player)sender).getInstance().getPlayers().size() < 2) {
                sender.sendMessage("Not enough players :/");
                return;
            }
            sender.sendMessage("Starting the game...");
            MineWolves.mineWolvesManager.startGame();
        });
    }
}

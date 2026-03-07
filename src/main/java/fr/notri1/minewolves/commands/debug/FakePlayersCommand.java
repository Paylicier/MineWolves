package fr.notri1.minewolves.commands.debug;

import fr.notri1.minewolves.MineWolves;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;

public class FakePlayersCommand extends Command {

    public FakePlayersCommand() {
        super("fakeplayers");

        var countArg = ArgumentType.Integer("count").min(0).max(50);

        // Default: show current fake player count
        setDefaultExecutor((sender, context) -> {
            int fakeCount = MineWolves.mineWolvesManager.getFakePlayerCount();
            int realCount = MineWolves.instanceContainer.getPlayers().size();
            sender.sendMessage(Component.text("Fake players: " + fakeCount + " | Real players: " + realCount + " | Total: " + (realCount + fakeCount)).color(NamedTextColor.AQUA));
        });

        // Set fake player count
        addSyntax((sender, context) -> {
            int count = context.get(countArg);
            MineWolves.mineWolvesManager.setFakePlayerCount(count);
            int realCount = MineWolves.instanceContainer.getPlayers().size();
            sender.sendMessage(Component.text("Set fake player count to " + count + " (total effective: " + (realCount + count) + ")").color(NamedTextColor.GREEN));
        }, countArg);
    }
}



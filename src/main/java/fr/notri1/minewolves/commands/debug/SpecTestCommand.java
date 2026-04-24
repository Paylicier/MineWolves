package fr.notri1.minewolves.commands.debug;

import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.minecraft.ArgumentEntity;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.entity.EntityFinder;

import static fr.notri1.minewolves.MineWolves.mineWolvesManager;

public class SpecTestCommand extends Command {
    public SpecTestCommand() {
        super("spectest");

        ArgumentEntity targetArgument = ArgumentType.Entity("target").onlyPlayers(true).singleEntity(true);

        setDefaultExecutor((sender, context) -> {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("You must be a player to execute this command!");
                return;
            }

            toggleSpec(player, sender);
        });

        addSyntax((sender, context) -> {
            EntityFinder finder = context.get(targetArgument);
            Player target = finder.findFirstPlayer(sender);

            if (target != null) {
                toggleSpec(target, sender);
            } else {
                sender.sendMessage("Player not found!");
            }
        }, targetArgument);
    }

    private void toggleSpec(Player player, net.minestom.server.command.CommandSender sender) {
//        if (player.getGameMode() == GameMode.SPECTATOR) {
//            player.setGameMode(GameMode.ADVENTURE);
////            player.updateViewerRule(entity -> true);
//            player.updateViewableRule(entity -> true);
//            player.setAutoViewable(true);
//            sender.sendMessage(player.getUsername() + " is now in adventure mode.");
//        } else {
//            player.setGameMode(GameMode.SPECTATOR);
////            player.updateViewerRule(entity -> false);
//            player.updateViewableRule(entity -> false);
//            player.setAutoViewable(false);
//            sender.sendMessage(player.getUsername() + " is now a spectator.");
//        }
        mineWolvesManager.eliminatePlayer(player);
    }
}

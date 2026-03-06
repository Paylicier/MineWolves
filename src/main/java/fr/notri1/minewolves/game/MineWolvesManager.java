package fr.notri1.minewolves.game;

import fr.notri1.minewolves.Status;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.potion.Potion;
import net.minestom.server.potion.PotionEffect;
import net.minestom.server.timer.Scheduler;
import net.minestom.server.timer.TaskSchedule;

import static fr.notri1.minewolves.MineWolves.instanceContainer;

public class MineWolvesManager {
    public Status status = Status.WAITING;
    public RoleManager roleManager = new RoleManager();

    public boolean start() {
        if (status != Status.WAITING) return false;
        status = Status.STARTING;

        System.out.println("Starting the game...");

        Scheduler scheduler = MinecraftServer.getSchedulerManager();
        for (int second = 5; second > 0; second--) {
            int finalSecond = second;
            scheduler.buildTask(() -> {
                instanceContainer.playSound(Sound.sound(Key.key("block.note_block.bell"), Sound.Source.MASTER, 1f, 1f));
                instanceContainer.sendMessage(Component.translatable("minewolves.game.starting_in", Component.text(finalSecond).color(NamedTextColor.GREEN)).color(NamedTextColor.GREEN));
                System.out.println(finalSecond);
            }).delay(TaskSchedule.seconds(5 - second)).schedule();
        }

        scheduler.buildTask(() -> {
            status = Status.IN_GAME;
            instanceContainer.sendMessage(Component.translatable("minewolves.game.started").color(NamedTextColor.GREEN));
            System.out.println("Game started!");
            instanceContainer.playSound(Sound.sound(Key.key("item.totem.use"), Sound.Source.MASTER, 1f, 1f));
            instanceContainer.getPlayers().forEach(player -> player.addEffect(new Potion(PotionEffect.BLINDNESS, 100, -1, 0x00)));
            RoleSelection.getRolesForPlayerCount(5).forEach(role -> System.out.println(role.getKey()));
            //todo : tp players and sit them so they can't move
        }).delay(TaskSchedule.seconds(5)).schedule();

        return true;
    }
}

package fr.notri1.minewolves.game.phases.turns;

import fr.notri1.minewolves.game.menus.Menu;
import fr.notri1.minewolves.game.menus.WitchKillMenu;
import fr.notri1.minewolves.game.menus.WitchMenu;
import fr.notri1.minewolves.game.menus.WolfMenu;
import fr.notri1.minewolves.game.phases.NightPhase;
import fr.notri1.minewolves.game.roles.Team;
import fr.notri1.minewolves.game.roles.Witch;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.timer.TaskSchedule;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import static fr.notri1.minewolves.MineWolves.instanceContainer;
import static fr.notri1.minewolves.MineWolves.mineWolvesManager;

public class WitchTurn extends NightTurn {

    private final Witch role;
    private AtomicInteger countdown = new AtomicInteger(20);

    public WitchTurn(Witch role) {
        this.role = role;
    }

    @Override
    public void onTurn() {
        // open potion menu and countdown
        role.getPlayers().forEach(player -> {
            WitchMenu menu = new WitchMenu(role, this);
            menu.open(player);
        });

        countdown = new AtomicInteger(20);

        MinecraftServer.getSchedulerManager().submitTask(() -> {
            int current = countdown.getAndDecrement();

            if (current <= 0) {
                role.getPlayers().forEach(fr.notri1.minewolves.game.menus.Menu::closeStatic);

                ((NightPhase) mineWolvesManager.getPhase()).nextTurn();
                return TaskSchedule.stop();
            }

            role.getPlayers().forEach(player -> {
                fr.notri1.minewolves.game.menus.Menu.MenuSession session = fr.notri1.minewolves.game.menus.Menu.getSession(player);
                if (session != null && (session.getMenu() instanceof WitchMenu || session.getMenu() instanceof WitchKillMenu)) {
                    session.getMenu().updateElement(player, "countdown", Component.translatable("minewolves.menu.countdown", Component.text(current)).color(role.getTeam().getColor()));
                }
            });

            return TaskSchedule.seconds(1);
        });

    }

    public void onRevive(Player player) {
        if (!role.hasHealingPotion()) {
            player.sendActionBar(Component.translatable("minewolves.menu.witch.no_potion").color(NamedTextColor.RED));
            return;
        }

        if (mineWolvesManager.getPlayersToEliminate().isEmpty()) {
            player.sendActionBar(Component.translatable("minewolves.menu.witch.no_victim").color(NamedTextColor.RED));
            return;
        }

        role.useHealingPotion();

        Audience.audience(role.getPlayers()).sendMessage(Component.translatable("minewolves.role.witch").color(Team.VILLAGE.getColor())
                .append(Component.text(" | ").color(NamedTextColor.GRAY))
                .append(Component.translatable("minewolves.witch.revived", Component.text(mineWolvesManager.getPlayersToEliminate().getFirst().getUsername())).color(NamedTextColor.WHITE)));

        mineWolvesManager.clearPlayersToEliminate();

        countdown.set(0); // end turn
    }

    public void onDoNothing(Player player) {

        Audience.audience(role.getPlayers()).sendMessage(Component.translatable("minewolves.role.witch").color(Team.VILLAGE.getColor())
                .append(Component.text(" | ").color(NamedTextColor.GRAY))
                .append(Component.translatable("minewolves.witch.nothing").color(NamedTextColor.WHITE)));

        countdown.set(0); // end turn
    }

    public void onKill(Player player) {
        if (!role.hasKillingPotion()) {
            player.sendActionBar(Component.translatable("minewolves.menu.witch.no_potion").color(NamedTextColor.RED));
            return;
        }

        Menu.closeStatic(player); // close current menu

        WitchKillMenu menu = new WitchKillMenu(role, this);
        menu.open(player);



    }

    public void onKillTarget(Player player, Player target) {
        if (!role.hasKillingPotion()) {
            player.sendActionBar(Component.translatable("minewolves.menu.witch.no_potion").color(NamedTextColor.RED));
            return;
        }

        mineWolvesManager.addPlayerToEliminate(target);
        role.useKillingPotion();


        Audience.audience(role.getPlayers()).sendMessage(Component.translatable("minewolves.role.witch").color(Team.VILLAGE.getColor())
                .append(Component.text(" | ").color(NamedTextColor.GRAY))
                .append(Component.translatable("minewolves.witch.killed", Component.text(target.getUsername())).color(NamedTextColor.WHITE)));

        countdown.set(0); // end turn
    }
}

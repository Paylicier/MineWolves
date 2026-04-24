package fr.notri1.minewolves.game.roles;

import fr.notri1.minewolves.game.menus.HunterMenu;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.timer.TaskSchedule;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import static fr.notri1.minewolves.MineWolves.instanceContainer;
import static fr.notri1.minewolves.MineWolves.mineWolvesManager;

/**
 * The Hunter can eliminate one player upon death.
 * Wins with the Village team.
 */
public class Hunter extends Role {

    @Override
    public String getKey() {
        return "hunter";
    }

    @Override
    public int getMinPlayers() {
        return 0;
    }

    @Override
    public int getMaxPlayers() {
        return 1;
    }

    @Override
    public String getIcon() {
        return "H";
    }

    @Override
    public Key getSound() {
        return Key.key("minewolves", "role.hunter");
    }

    @Override
    public Team getTeam() {
        return Team.VILLAGE;
    }

    private AtomicInteger countdown = new AtomicInteger(15);

    private CompletableFuture<Void> deathFuture;

    @Override
    public CompletableFuture<Void> onDeath() {
        deathFuture = new CompletableFuture<>();
        instanceContainer.sendMessage(Component.translatable("minewolves.role.hunter.death"));
        HunterMenu menu = new HunterMenu(this);
        this.getPlayers().forEach(menu::open);

        countdown = new AtomicInteger(15);

        MinecraftServer.getSchedulerManager().submitTask(() -> {
            int current = countdown.getAndDecrement();

            if (current <= 0) {
                this.getPlayers().forEach(fr.notri1.minewolves.game.menus.Menu::closeStatic);
                deathFuture.complete(null);
                return TaskSchedule.stop();
            }

            this.getPlayers().forEach(player -> {
                fr.notri1.minewolves.game.menus.Menu.MenuSession session = fr.notri1.minewolves.game.menus.Menu.getSession(player);
                if (session != null && (session.getMenu() instanceof HunterMenu)) {
                    session.getMenu().updateElement(player, "countdown", Component.translatable("minewolves.menu.countdown", Component.text(current)).color(this.getTeam().getColor()));
                }
            });

            return TaskSchedule.seconds(1);
        });

        return deathFuture;
    }

    public void onKill(Player target) {
        Role tr = mineWolvesManager.roleManager.getRole(target);
        Audience.audience(this.getPlayers()).sendMessage(Component.translatable("minewolves.role.hunter.shot", Component.text(target.getUsername())));
        instanceContainer.sendMessage(Component.translatable("minewolves.role.hunter.broadcast", Component.text(target.getUsername()), tr.getDisplayName().color(tr.getTeam().getColor())));

        mineWolvesManager.eliminatePlayer(target);
        countdown.set(0);
    }
}


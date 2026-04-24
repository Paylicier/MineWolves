package fr.notri1.minewolves.game.phases;

import fr.notri1.minewolves.game.RoleSelection;
import fr.notri1.minewolves.game.menus.RoleMenu;
import fr.notri1.minewolves.game.roles.Role;
import fr.notri1.minewolves.game.roles.Team;
import fr.notri1.minewolves.game.roles.Werewolf;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.ShadowColor;
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

        List<Role> roleSelection = RoleSelection.getRolesForPlayerCount(instanceContainer.getPlayers().size());

        instanceContainer.getPlayers().forEach(player -> {
            Role pRole = roleSelection.getFirst();
//            Role pRole = (player.getUsername().equals("ri1_") || player.getUsername().equals("Paylicier")) ? roleSelection.stream().filter(role -> role instanceof Werewolf).findFirst().orElse(roleSelection.getFirst()) : roleSelection.getFirst();
            mineWolvesManager.roleManager.assignRole(player, pRole);
            System.out.println("Assigned role " + roleSelection.getFirst().getClass().getSimpleName() + " to player " + player.getUsername());
            roleSelection.removeFirst();
        });

        mineWolvesManager.roleManager.updateTeamBossBar(Team.WEREWOLVES);

        mineWolvesManager.roleManager.getAliveRoles().stream().filter(r -> r instanceof Werewolf).forEach(r -> ((Werewolf) r).setRandomColors());

        // Show roles

        Scheduler scheduler = MinecraftServer.getSchedulerManager();

        instanceContainer.setTime(18000);

        instanceContainer.getPlayers().forEach(player -> {
            Role playerRole = mineWolvesManager.roleManager.getRole(player);
            RoleMenu roleMenu = new RoleMenu(playerRole);

            roleMenu.open(player);
            scheduler.buildTask(() -> {
                player.showBossBar(BossBar.bossBar(Component.text("\uF808\uF808\uF808\uF808\uF808\uF808\uF806\uF806\uF802\uF804" + playerRole.getIcon()).font(Key.key("minewolves", "roles")).shadowColor(ShadowColor.shadowColor(0, 0, 0, 0)), 0f, BossBar.Color.WHITE, BossBar.Overlay.PROGRESS));

                if (playerRole.getTeam() == Team.WEREWOLVES) {
                    player.showBossBar(mineWolvesManager.roleManager.getTeamBossBar(playerRole.getTeam()));
                }
                roleMenu.close(player);
            }).delay(TaskSchedule.seconds(5)).schedule();
        });

        scheduler.buildTask(() -> {
            instanceContainer.setTime(1000);
            onEnd();
        }).delay(TaskSchedule.seconds(5)).schedule();
    }

    @Override
    public void onEnd() {
        mineWolvesManager.setPhase(new NightPhase());
    }
}
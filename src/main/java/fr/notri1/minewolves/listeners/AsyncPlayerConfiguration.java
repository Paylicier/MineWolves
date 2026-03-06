package fr.notri1.minewolves.listeners;

import fr.notri1.minewolves.MineWolves;
import fr.notri1.minewolves.pack.PackGenerator;
import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackRequest;
import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import static fr.notri1.minewolves.MineWolves.instanceContainer;

public class AsyncPlayerConfiguration implements EventListener<AsyncPlayerConfigurationEvent> {
    @Override
    public @NotNull Class<AsyncPlayerConfigurationEvent> eventType() {
        return AsyncPlayerConfigurationEvent.class;
    }

    @Override
    public Result run(AsyncPlayerConfigurationEvent event) {
        final Player player = event.getPlayer();
        event.setSpawningInstance(instanceContainer);
        player.setRespawnPoint(new Pos(0, 40, 0));
        ResourcePackRequest request = null;
        try {
            request = ResourcePackRequest.resourcePackRequest()
                    .packs(ResourcePackInfo.resourcePackInfo(UUID.randomUUID(), MineWolves.config.getWeb().getUrl(), PackGenerator.getPackHash()))
                    .prompt(Component.text("pack :)"))
                    .required(true)
                    .build();
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        player.sendResourcePacks(request);
        return null;
    }
}
package fr.notri1.minewolves.pack;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.entity.Player;

import java.util.Locale;

public class LocalizationUtils {
    public static Sound getLocalizedSound(String namespace, String key, Player player) {
        Locale locale = player.getLocale();

        return Sound.sound(
                Key.key(namespace, key + "." + locale.toString().split("_")[0].toLowerCase()), // e.g. "namespace:key.fr" for fr_FR and fr_CA
                Sound.Source.VOICE,
                1f, 1f
        );
    }
}

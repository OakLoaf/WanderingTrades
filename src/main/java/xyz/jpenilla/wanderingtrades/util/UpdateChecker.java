package xyz.jpenilla.wanderingtrades.util;

import xyz.jpenilla.wanderingtrades.WanderingTrades;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Consumer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class UpdateChecker {

    private final WanderingTrades plugin;
    private final int resourceId;

    public UpdateChecker(WanderingTrades plugin, int resourceId) {
        this.plugin = plugin;
        this.resourceId = resourceId;
    }

    public void getVersion(final Consumer<String> consumer) {
        class GetVersionTask extends BukkitRunnable {
            @Override
            public void run() {
                try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + resourceId).openStream(); Scanner scanner = new Scanner(inputStream)) {
                    if (scanner.hasNext()) {
                        consumer.accept(scanner.next());
                    }
                } catch (IOException exception) {
                    plugin.getLog().info("&eCannot look for updates:&r " + exception.getMessage());
                }
            }
        }
        new GetVersionTask().runTaskAsynchronously(plugin);
    }

    public static void updateCheck(String version) {
        updateCheck(version, false);
    }

    public static void updateCheck(String version, boolean startup) {
        if (WanderingTrades.getInstance().getDescription().getVersion().equalsIgnoreCase(version)) {
            if(startup) {
                WanderingTrades.getInstance().getLog().info("&aYou are running the latest version of " + WanderingTrades.getInstance().getName() + "! :)");
            }
        } else if(WanderingTrades.getInstance().getDescription().getVersion().contains("SNAPSHOT")) {
            WanderingTrades.getInstance().getLog().info("&e[!] &6You are running a development build of " + WanderingTrades.getInstance().getName() + " (" + WanderingTrades.getInstance().getDescription().getVersion() + ") &e[!]");
        } else {
            WanderingTrades.getInstance().getLog().info("&e[!] &6You are running an outdated version of " + WanderingTrades.getInstance().getName() + " (" + WanderingTrades.getInstance().getDescription().getVersion() + ") &e[!]");
            WanderingTrades.getInstance().getLog().info("&bVersion " + version + " is available at &b&ohttps://www.spigotmc.org/resources/wanderingtrades.79068/");
        }
    }
}
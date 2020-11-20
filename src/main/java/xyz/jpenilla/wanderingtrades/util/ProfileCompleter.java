package xyz.jpenilla.wanderingtrades.util;

import com.destroystokyo.paper.profile.PlayerProfile;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.nullness.qual.NonNull;
import xyz.jpenilla.wanderingtrades.WanderingTrades;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

class ProfileCompleter extends BukkitRunnable {

    private final Queue<PlayerProfile> completionQueue = new ConcurrentLinkedQueue<>();

    public void submitProfile(final @NonNull PlayerProfile profile) {
        this.completionQueue.add(profile);
    }

    public void clearQueue() {
        this.completionQueue.clear();
    }

    @Override
    public void run() {
        if (!this.completionQueue.isEmpty()) {
            final PlayerProfile profile = this.completionQueue.remove();
            try {
                profile.complete();
            } catch (Exception e) {
                WanderingTrades.getInstance().getLog().debug(String.format("Failed to cache player head skin for player: [username=%s,uuid=%s]", profile.getName(), profile.getId()));
            }
        }
    }

}

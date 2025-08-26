package de.pascalpex.pof.events.listener;

import de.pascalpex.pof.PillarsOfFortune;
import de.pascalpex.pof.util.ScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerTeleportListener implements Listener {

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        if (PillarsOfFortune.getManager().isIngame(player) && !event.getTo().getWorld().getName().equalsIgnoreCase(PillarsOfFortune.GAME_WORLD_NAME)) {
            PillarsOfFortune.getManager().leavePlayer(player);
            player.setFlying(false);
            if (ScoreboardManager.scoreboards.containsKey(player)) {
                ScoreboardManager.scoreboards.remove(player);
                player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            }
            player.performCommand("sbreset");
        }
    }

}

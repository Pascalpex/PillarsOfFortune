package de.pascalpex.pof.events.listener;

import de.pascalpex.pof.PillarsOfFortune;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if(PillarsOfFortune.getManager().isIngame(player)) {
            player.getInventory().clear();
            PillarsOfFortune.getManager().leavePlayer(player);
        }
    }

}

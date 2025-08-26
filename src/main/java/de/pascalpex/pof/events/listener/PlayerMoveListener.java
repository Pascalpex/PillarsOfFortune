package de.pascalpex.pof.events.listener;

import de.pascalpex.pof.PillarsOfFortune;
import de.pascalpex.pof.model.Arena;
import de.pascalpex.pof.model.GameState;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (PillarsOfFortune.getManager().isIngame(player)) {
            if (PillarsOfFortune.getManager().getPlayerArena(player).isAlive(player)) {
                Arena arena = PillarsOfFortune.getManager().getPlayerArena(player);
                if (player.getLocation().getY() < arena.getLoseLevel()) {
                    if(arena.getState() == GameState.RUNNING) {
                        PillarsOfFortune.getManager().loseActions(player);
                    } else {
                        player.teleport(arena.getSpawn());
                    }
                }
            } else {
                Arena arena = PillarsOfFortune.getManager().getPlayerArena(player);
                Location firstPos = arena.getFirstWoolPos();
                Location secondPos = arena.getSecondWoolPos();
                Location playerLoc = player.getLocation();

                double minX = Math.min(firstPos.getX(), secondPos.getX()) - 2;
                double maxX = Math.max(firstPos.getX(), secondPos.getX()) + 2;
                double minZ = Math.min(firstPos.getZ(), secondPos.getZ()) - 2;
                double maxZ = Math.max(firstPos.getZ(), secondPos.getZ()) + 2;

                if (playerLoc.getX() < minX || playerLoc.getX() > maxX ||
                        playerLoc.getZ() < minZ || playerLoc.getZ() > maxZ) {
                    player.teleport(arena.getSpectatorSpawn());
                }

            }
        }
    }
}

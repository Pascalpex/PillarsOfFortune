package de.pascalpex.pof.events.listener;

import de.pascalpex.pof.PillarsOfFortune;
import de.pascalpex.pof.files.Config;
import de.pascalpex.pof.util.MessageHandler;
import de.pascalpex.pof.util.ScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerInteractListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item.getType() == Material.CYAN_BED
                    && item.getItemMeta().itemName().contains(PillarsOfFortune.LEAVE_ITEM_NAME)
                    && item.getItemMeta().hasLore()
                    && PillarsOfFortune.getManager().isIngame(player)) {
                if (PillarsOfFortune.getManager().isIngame(player)) {
                    PillarsOfFortune.getManager().leavePlayer(player);
                    player.getInventory().clear();
                    player.setFlying(false);
                    if (ScoreboardManager.scoreboards.containsKey(player)) {
                        ScoreboardManager.scoreboards.remove(player);
                        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
                    }
                    player.teleport(Config.getLobby());
                    player.performCommand("sbreset");
                    event.setCancelled(true);
                } else {
                    player.sendMessage(MessageHandler.prefixedMini("<red>Du bist nicht in PillarsOfFortune."));
                }
            }
        }
    }

}

package de.pascalpex.pof.events.listener;

import de.pascalpex.pof.PillarsOfFortune;
import de.pascalpex.pof.model.Arena;
import de.pascalpex.pof.model.GameState;
import de.pascalpex.pof.util.MessageHandler;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class InventoryClickListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(PillarsOfFortune.getManager().isIngame((Player) event.getView().getPlayer())) {
            event.setCancelled(true);
        }
        Inventory inventory = event.getClickedInventory();
        if(inventory != null && PillarsOfFortune.getManager().isArenaSelector(inventory)) {
            event.setCancelled(true);
            ItemStack clickedItem = event.getCurrentItem();
            if(clickedItem != null && !clickedItem.getType().isAir()) {
                Set<Material> possibleMaterials = new HashSet<>();
                PillarsOfFortune.getManager().getArenas().forEach(arena -> possibleMaterials.add(arena.getSelectorItem()));
                if(possibleMaterials.contains(clickedItem.getType())) {
                    Arena arena = PillarsOfFortune.getManager().getArenas().get(event.getSlot() - 10);
                    Player player = (Player) event.getView().getPlayer();
                    if(arena.getPlayers().size() < arena.getMaxPlayers()) {
                        if (arena.getState() == GameState.WAITING) {
                            PillarsOfFortune.getManager().joinPlayer(player, arena);
                            player.closeInventory();
                        } else {
                            player.sendMessage(MessageHandler.prefixedMini("<red>Das Spiel läuft bereits"));
                        }
                    } else {
                        player.sendMessage(MessageHandler.prefixedMini("<red>Das Spiel ist bereits voll"));
                    }
                }
            }
        }
    }

}

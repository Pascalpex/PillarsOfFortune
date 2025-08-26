package de.pascalpex.pof;

import de.pascalpex.pof.files.ArenasFile;
import de.pascalpex.pof.files.Config;
import de.pascalpex.pof.model.Arena;
import de.pascalpex.pof.model.GameState;
import de.pascalpex.pof.util.MessageHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameManager {

    private List<Arena> arenas;
    private final Map<Player, Arena> ingame;
    private Inventory arenaSelector;
    private static List<Material> validItems;

    public GameManager() {
        reloadValidItems();
        reloadArenas();
        ingame = new HashMap<>();
        buildArenaSelector();
    }

    private void buildArenaItem(Arena arena) {
        int arenaIndex = arenas.indexOf(arena);
        ItemStack arenaItem = new ItemStack(arena.getSelectorItem(), 1);
        ItemMeta arenaMeta = arenaItem.getItemMeta();
        arenaMeta.itemName(Component.text(arena.getName()).color(NamedTextColor.GREEN));
        arenaMeta.lore(List.of(
                Component.text("Klicke zum beitreten").color(NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false),
                arena.getState() == GameState.WAITING ? Component.text("Spieler: ").color(NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false).append(Component.text(arena.getPlayers().size() + "/" + arena.getMaxPlayers()).color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false)) : Component.text("Spiel läuft").color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false)));
        arenaItem.setItemMeta(arenaMeta);

        arenaSelector.setItem(arenaIndex + 10, arenaItem);
    }

    public void buildArenaSelector() {
        arenaSelector = Bukkit.createInventory(null, PillarsOfFortune.ARENA_SELECTOR_ROWS * 9, MiniMessage.miniMessage().deserialize(Config.getArenaSelectorTitle()));

        ItemStack spacer = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE, 1);
        ItemMeta spacerMeta = spacer.getItemMeta();
        spacerMeta.itemName(Component.text("").color(NamedTextColor.GRAY));
        spacer.setItemMeta(spacerMeta);

        for(int i = 0; i < arenaSelector.getSize(); i++) {
            arenaSelector.setItem(i, spacer);
        }

        arenas.forEach(this::buildArenaItem);
    }

    public boolean isArenaSelector(Inventory inventory) {
        return arenaSelector == inventory;
    }

    public void openArenaSelector(Player player) {
        player.openInventory(arenaSelector);
    }

    public boolean isIngame(Player player) {
        return ingame.containsKey(player);
    }

    public void softLeavePlayer(Player player) {
        if(ingame.containsKey(player)) {
            Arena arena = ingame.get(player);
            ingame.remove(player);
            player.hideBossBar(arena.getBossbar());
            arena.updateBoosbar();

            buildArenaItem(arena);
        }
    }

    public void leavePlayer(Player player) {
        if(ingame.containsKey(player)) {
            Arena arena = ingame.get(player);
            arena.removePlayer(player);
            ingame.remove(player);
            player.hideBossBar(arena.getBossbar());
            arena.updateBoosbar();

            buildArenaItem(arena);

            broadcastMessage(arena, MessageHandler.prefixedMini("<gold>" + player.getName() + " <aqua>hat das Spiel verlassen"));
            player.sendMessage(MessageHandler.prefixedMini("Du hast das Spiel verlassen"));
        }
    }

    public List<Arena> getArenas() {
        return arenas;
    }

    public void broadcastMessage(Arena arena, Component message) {
        for (Player p : arena.getPlayers()) {
            p.sendMessage(message);
        }
    }

    public Arena getPlayerArena(Player player) {
        return ingame.get(player);
    }

    public void reloadArenas() {
        arenas = ArenasFile.loadArenas();
        Bukkit.getConsoleSender().sendMessage(MessageHandler.prefixedMini("<green>Loaded <gold>" + arenas.size() + "<green> arena(s)"));
    }

    public void joinPlayer(Player player, Arena arena) {
        ingame.put(player, arena);
        arena.addPlayer(player);
        arena.updateBoosbar();

        buildArenaItem(arena);

        player.teleport(arena.getSpawn());
        player.setGameMode(GameMode.SURVIVAL);
        player.setFlying(false);
        player.showBossBar(arena.getBossbar());
        broadcastMessage(arena, MessageHandler.prefixedMini("<gold>" + player.getName() + " <aqua>hat das Spiel betreten"));
        player.sendMessage(MessageHandler.prefixedMini("Du hast das Spiel betreten"));
    }

    public void reload() {
        ingame.clear();
        arenas.forEach(arena -> arena.getPlayers().clear());
        reloadValidItems();
        reloadArenas();
        buildArenaSelector();
    }

    private void reloadValidItems() {
        validItems = Arrays.stream(Material.values()).filter(material -> !Config.getItemBlacklist().contains(material)).toList();
    }

    public List<Material> getValidItems() {
        return validItems;
    }

    public void loseActions(Player player) {
        Arena arena = ingame.get(player);
        broadcastMessage(arena, MessageHandler.prefixedMini("<gold>" + player.getName() + " <aqua>hat das Spiel verloren"));
        player.sendMessage(MessageHandler.prefixedMini("<red>Du hast das Spiel verloren"));
        player.setGameMode(GameMode.SPECTATOR);
        player.setFlying(true);
        arena.loseActions(player);
    }
}

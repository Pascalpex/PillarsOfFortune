package de.pascalpex.pof;

import de.pascalpex.pof.events.listener.*;
import de.pascalpex.pof.files.ArenasFile;
import de.pascalpex.pof.files.Config;
import de.pascalpex.pof.util.MessageHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class PillarsOfFortune extends JavaPlugin {

    private static PillarsOfFortune instance;
    private static Logger logger;
    private static String pluginVersion;
    private static GameManager manager;

    public static final String GAME_WORLD_NAME = "world";
    public static final int ARENA_SELECTOR_ROWS = 3;

    public static final Component LEAVE_ITEM_NAME = Component.text("Verlassen").color(NamedTextColor.DARK_AQUA);

    @Override
    public void onEnable() {
        instance = this;
        pluginVersion = getPluginMeta().getVersion();
        logger = getLogger();

        Config.load();
        ArenasFile.load();
        MessageHandler.prefix = MessageHandler.parse(Config.getPrefix());

        manager = new GameManager();

        // Listeners
        this.getServer().getPluginManager().registerEvents(new PlayerMoveListener(), this);
        this.getServer().getPluginManager().registerEvents(new InventoryClickListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerInteractListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerTeleportListener(), this);

        Bukkit.getConsoleSender().sendMessage(MessageHandler.prefixedMini("<green>PillarsOfFortune <gold>" + pluginVersion + "<green> by Pascalpex was activated"));
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(MessageHandler.prefixedMini("<red>PillarsOfFortune <gold>" + pluginVersion + "<red> by Pascalpex was deactivated"));
    }

    public static PillarsOfFortune getInstance() {
        return instance;
    }

    public static Logger logger() {
        return logger;
    }

    public static GameManager getManager() {
        return manager;
    }

    public static void reload() {
        Config.load();
        ArenasFile.load();
        MessageHandler.prefix = MessageHandler.parse(Config.getPrefix());
        PillarsOfFortune.getManager().reload();
    }

    public static String getPluginVersion() {
        return pluginVersion;
    }

}
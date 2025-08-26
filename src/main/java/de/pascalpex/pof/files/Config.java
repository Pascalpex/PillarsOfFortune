package de.pascalpex.pof.files;

import de.pascalpex.pof.PillarsOfFortune;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

public class Config {

    public static final File configFile = new File("plugins/PillarsOfFortune", "config.yml");
    public static final FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

    public static void load() {
        try {
            configFile.getParentFile().mkdirs();
            if (!configFile.exists()) {
                configFile.createNewFile();
                config.set("prefix", "<gray>[<blue>PillarsOfFortune<gray>]");
                save();
            }
            config.load(configFile);
            if(!config.contains("arenaSelectorTitle")) {
                config.set("arenaSelectorTitle", "<gold><bold>PillarsOfFortune Arenas");
            }
            if(!config.contains("itemBlacklist")) {
                config.set("itemBlacklist", List.of(
                        Material.AIR.toString(),
                        Material.STRUCTURE_VOID.toString()
                ));
            }
            save();
        } catch (IOException | InvalidConfigurationException e) {
            PillarsOfFortune.logger().log(Level.SEVERE, "PillarsOfFortune was unable to load the config file");
        }

    }

    public static void save() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            PillarsOfFortune.logger().log(Level.SEVERE, "PillarsOfFortune was unable to save the config file");
        }
    }

    public static List<Material> getItemBlacklist() {
        List<String> stringList = config.getStringList("itemBlacklist");
        return stringList.stream().map(Material::valueOf).toList();
    }

    public static String getPrefix() {
        return config.getString("prefix");
    }

    public static Location getLobby() {
        double x = config.getDouble("lobby.x");
        double y = config.getDouble("lobby.y");
        double z = config.getDouble("lobby.z");
        double yaw = config.getDouble("lobby.yaw");
        double pitch = config.getDouble("lobby.pitch");
        String worldName = config.getString("lobby.world");

        return new Location(Bukkit.getWorld(worldName), x, y, z, (float)yaw, (float)pitch);
    }

    public static String getArenaSelectorTitle() {
        return config.getString("arenaSelectorTitle");
    }
}
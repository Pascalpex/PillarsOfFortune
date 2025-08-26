package de.pascalpex.pof.files;

import de.pascalpex.pof.model.Arena;
import de.pascalpex.pof.PillarsOfFortune;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

public class ArenasFile {

    public static final File configFile = new File("plugins/PillarsOfFortune", "arenas.yml");
    public static final FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

    public static void load() {
        try {
            configFile.getParentFile().mkdirs();
            if (!configFile.exists()) {
                configFile.createNewFile();
                Arena arena = new Arena("Name",
                        new Location(Bukkit.getWorld("world"), 0, 0, 0),
                        new Location(Bukkit.getWorld("world"), 0, 0, 0),
                        new Location(Bukkit.getWorld("world"), 0, 0, 0),
                        new Location(Bukkit.getWorld("world"), 0, 0, 0),
                        0,
                        0,
                        0.0,
                        Material.DIAMOND);
                saveArena(arena);
                save();
            }
            config.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            PillarsOfFortune.logger().log(Level.SEVERE, "PillarsOfFortune was unable to load the arenas file");
        }

    }

    public static void save() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            PillarsOfFortune.logger().log(Level.SEVERE, "PillarsOfFortune was unable to save the arenas file");
        }
    }

    public static void saveArena(Arena arena) {
        config.set("arenas." + arena.getName() + ".world", arena.getSpawn().getWorld().getName());

        config.set("arenas." + arena.getName() + ".spawn" + ".x", arena.getSpawn().getX());
        config.set("arenas." + arena.getName() + ".spawn" + ".y", arena.getSpawn().getY());
        config.set("arenas." + arena.getName() + ".spawn" + ".z", arena.getSpawn().getZ());
        config.set("arenas." + arena.getName() + ".spawn" + ".pitch", arena.getSpawn().getPitch());
        config.set("arenas." + arena.getName() + ".spawn" + ".yaw", arena.getSpawn().getYaw());

        config.set("arenas." + arena.getName() + ".spectator" + ".x", arena.getSpectatorSpawn().getX());
        config.set("arenas." + arena.getName() + ".spectator" + ".y", arena.getSpectatorSpawn().getY());
        config.set("arenas." + arena.getName() + ".spectator" + ".z", arena.getSpectatorSpawn().getZ());
        config.set("arenas." + arena.getName() + ".spectator" + ".pitch", arena.getSpectatorSpawn().getPitch());
        config.set("arenas." + arena.getName() + ".spectator" + ".yaw", arena.getSpectatorSpawn().getYaw());

        config.set("arenas." + arena.getName() + ".woolArea" + ".first" + ".x", arena.getFirstWoolPos().getBlockX());
        config.set("arenas." + arena.getName() + ".woolArea" + ".first" + ".y", arena.getFirstWoolPos().getBlockY());
        config.set("arenas." + arena.getName() + ".woolArea" + ".first" + ".z", arena.getFirstWoolPos().getBlockZ());

        config.set("arenas." + arena.getName() + ".woolArea" + ".second" + ".x", arena.getSecondWoolPos().getBlockX());
        config.set("arenas." + arena.getName() + ".woolArea" + ".second" + ".y", arena.getSecondWoolPos().getBlockY());
        config.set("arenas." + arena.getName() + ".woolArea" + ".second" + ".z", arena.getSecondWoolPos().getBlockZ());

        config.set("arenas." + arena.getName() + ".minPlayers", arena.getMinPlayers());
        config.set("arenas." + arena.getName() + ".maxPlayers", arena.getMaxPlayers());
        config.set("arenas." + arena.getName() + ".loseLevel", arena.getLoseLevel());

        config.set("arenas." + arena.getName() + ".selectorItem", arena.getSelectorItem());

        save();
    }

    private static Arena loadArena(String arenaName) {
        String worldName = config.getString("arenas." + arenaName + ".world");
        if(worldName == null) {
            return null;
        }
        World world = Bukkit.getWorld(worldName);
        if(world == null) {
            return null;
        }
        double spawnX = config.getDouble("arenas." + arenaName + ".spawn" + ".x");
        double spawnY = config.getDouble("arenas." + arenaName + ".spawn" + ".y");
        double spawnZ = config.getDouble("arenas." + arenaName + ".spawn" + ".z");
        float spawnPitch = (float) config.getDouble("arenas." + arenaName + ".spawn" + ".pitch");
        float spawnYaw = (float) config.getDouble("arenas." + arenaName + ".spawn" + ".yaw");
        Location spawn = new Location(world, spawnX, spawnY, spawnZ, spawnYaw, spawnPitch);

        double spectatorX = config.getDouble("arenas." + arenaName + ".spectator" + ".x");
        double spectatorY = config.getDouble("arenas." + arenaName + ".spectator" + ".y");
        double spectatorZ = config.getDouble("arenas." + arenaName + ".spectator" + ".z");
        float spectatorPitch = (float) config.getDouble("arenas." + arenaName + ".spectator" + ".pitch");
        float spectatorYaw = (float) config.getDouble("arenas." + arenaName + ".spectator" + ".yaw");
        Location spectator = new Location(world, spectatorX, spectatorY, spectatorZ, spectatorYaw, spectatorPitch);

        int firstWoolX = config.getInt("arenas." + arenaName + ".woolArea" + ".first" + ".x");
        int firstWoolY = config.getInt("arenas." + arenaName + ".woolArea" + ".first" + ".y");
        int firstWoolZ = config.getInt("arenas." + arenaName + ".woolArea" + ".first" + ".z");
        Location firstWool = new Location(world, firstWoolX, firstWoolY, firstWoolZ);

        int secondWoolX = config.getInt("arenas." + arenaName + ".woolArea" + ".second" + ".x");
        int secondWoolY = config.getInt("arenas." + arenaName + ".woolArea" + ".second" + ".y");
        int secondWoolZ = config.getInt("arenas." + arenaName + ".woolArea" + ".second" + ".z");
        Location secondWool = new Location(world, secondWoolX, secondWoolY, secondWoolZ);

        int minPlayers = config.getInt("arenas." + arenaName + ".minPlayers");
        int maxPlayers = config.getInt("arenas." + arenaName + ".maxPlayers");
        double loseLevel = config.getDouble("arenas." + arenaName + ".loseLevel");

        Material selectorItem = Material.valueOf(config.getString("arenas." + arenaName + ".selectorItem"));

        return new Arena(arenaName, spawn, spectator, firstWool, secondWool, minPlayers, maxPlayers, loseLevel, selectorItem);
    }

    public static List<Arena> loadArenas() {
        if(config.getConfigurationSection("arenas") == null) {
            return Collections.emptyList();
        }
        return config.getConfigurationSection("arenas").getKeys(false).stream().map(ArenasFile::loadArena).filter(Objects::nonNull).toList();
    }

}
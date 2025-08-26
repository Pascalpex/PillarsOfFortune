package de.pascalpex.pof.model;

import de.pascalpex.pof.PillarsOfFortune;
import de.pascalpex.pof.files.Config;
import de.pascalpex.pof.util.MessageHandler;
import de.pascalpex.pof.util.ScoreboardManager;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class Arena {

    private static final int SECONDS_GAME_OVER = 4;
    private static final long TIME_BETWEEN_ITEMS = 20 * 10;
    private static final long FIRST_START_TIME = 20 * 2;

    private String name;
    private Location spawn;
    private Location spectatorSpawn;
    private Location firstWoolPos;
    private Location secondWoolPos;
    private int minPlayers;
    private int maxPlayers;
    private double loseLevel;

    private final Set<Player> players;
    private final Set<Player> alive;
    private GameState gameState;

    private final BossBar bossbar;
    private BukkitTask timerTask;
    private int waitingTimer;

    private int itemCounter;
    private Material selectorItem;

    private static final Random rng = new Random();

    public Arena(String name, Location spawn, Location spectatorSpawn, Location firstWoolPos, Location secondWoolPos, int minPlayers, int maxPlayers, double loseLevel, Material selectorItem) {
        this.name = name;
        this.spawn = spawn;
        this.spectatorSpawn = spectatorSpawn;
        this.firstWoolPos = firstWoolPos;
        this.secondWoolPos = secondWoolPos;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.loseLevel = loseLevel;
        this.selectorItem = selectorItem;

        waitingTimer = 20;
        itemCounter = 0;

        this.players = new HashSet<>();
        this.alive = new HashSet<>();
        gameState = GameState.WAITING;
        bossbar = BossBar.bossBar(Component.text("Spieler: 0/" + maxPlayers).color(NamedTextColor.AQUA), 0, BossBar.Color.WHITE, BossBar.Overlay.PROGRESS);
        updateBoosbar();
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
        if(gameState == GameState.WAITING) {
            itemCounter = 0;
            updateBoosbar();
        }
        PillarsOfFortune.getManager().buildArenaSelector();
    }

    public int getPlayersLeft() {
        return alive.size();
    }

    public void updateBoosbar() {
        if(gameState == GameState.WAITING && players.size() >= minPlayers) {
            bossbar.color(BossBar.Color.GREEN);
        } else {
            bossbar.color(BossBar.Color.WHITE);
        }
        bossbar.name(Component.text("Spieler: " + players.size() + "/" + maxPlayers).color(NamedTextColor.AQUA));
        bossbar.progress((float) players.size() / (float) maxPlayers);
    }

    public BossBar getBossbar() {
        return bossbar;
    }

    public GameState getState() {
        return gameState;
    }

    public Set<Player> getPlayers() {
        return players;
    }

    public void addPlayer(Player player) {
        players.add(player);
        alive.add(player);
        updateScoreboards();

        if(gameState == GameState.WAITING) {
            ItemStack leave = new ItemStack(Material.CYAN_BED, 1);
            ItemMeta leaveMeta = leave.getItemMeta();
            leaveMeta.itemName(PillarsOfFortune.LEAVE_ITEM_NAME);
            leaveMeta.lore(List.of(Component.text("Klicke um das Spiel zu verlassen").color(NamedTextColor.AQUA)));
            leave.setItemMeta(leaveMeta);
            player.getInventory().clear();
            player.getInventory().setItem(8, leave);
        }

        if(gameState == GameState.WAITING && players.size() >= minPlayers) {
            if(timerTask == null) {
                timerTask = new BukkitRunnable() {
                    @Override
                    public void run() {
                        if(waitingTimer > 0) {
                            waitingTimer--;
                            if(waitingTimer % 5 == 0 || waitingTimer <= 5) {
                                if(waitingTimer == 0) {
                                    PillarsOfFortune.getManager().broadcastMessage(Arena.this, MessageHandler.prefixedMini("Das Spiel startet jetzt"));
                                    players.forEach(player -> player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10f, 1f));
                                } else {
                                    PillarsOfFortune.getManager().broadcastMessage(Arena.this, MessageHandler.prefixedMini("<gold>" + waitingTimer + " Sekunden <aqua>bis zum Start"));
                                }
                            }
                        } else {
                            startGame();
                        }
                    }
                }.runTaskTimer(PillarsOfFortune.getInstance(), 0, 20);
            }
        }
    }

    private void startGame() {
        alive.addAll(players);
        alive.forEach(player -> player.getInventory().clear());
        timerTask.cancel();
        timerTask = null;
        waitingTimer = 20;
        setGameState(GameState.RUNNING);
        bossbar.color(BossBar.Color.WHITE);

        new BukkitRunnable() {
            @Override
            public void run() {
                chooseItem();
            }
        }.runTaskLater(PillarsOfFortune.getInstance(), FIRST_START_TIME);
    }

    public int getItemCounter() {
        return itemCounter;
    }

    private void chooseItem() {
        for(Player player : players) {
            ItemStack item = getRandomItem();
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 10f, 1f);
            player.getInventory().addItem(item);
        }
        bossbar.name(Component.text("Zeit bis zum nächsten Item").color(NamedTextColor.GOLD));
        bossbar.progress(1.0f);
        final long[] ticksLeft = {TIME_BETWEEN_ITEMS};
        BukkitTask countdownTask = new BukkitRunnable() {
            @Override
            public void run() {
                ticksLeft[0]--;
                if(ticksLeft[0] <= 0) {
                    ticksLeft[0] = 0;
                }
                bossbar.progress(ticksLeft[0] / (float) TIME_BETWEEN_ITEMS);
            }
        }.runTaskTimer(PillarsOfFortune.getInstance(), 0, 1);
        updateScoreboards();
        new BukkitRunnable() {
            @Override
            public void run() {
                countdownTask.cancel();
                if(alive.size() > 1) {
                    chooseItem();
                }
            }
        }.runTaskLater(PillarsOfFortune.getInstance(), TIME_BETWEEN_ITEMS);
    }

    private ItemStack getRandomItem() {
        List<Material> possibleItems = PillarsOfFortune.getManager().getValidItems();
        ItemStack itemStack = null;
        while (itemStack == null) {
            try {
                Material material = possibleItems.get(rng.nextInt(possibleItems.size()));
                itemStack = new ItemStack(material);
            } catch (IllegalArgumentException ignored) {
                // Weird material name
            }

        }
        return itemStack;
    }

    private void gameEnding() {
        setGameState(GameState.ENDING);
        bossbar.color(BossBar.Color.RED);
        bossbar.name(Component.text("Das Spiel ist beendet!").color(NamedTextColor.RED));
        bossbar.progress(1.0f);
        new BukkitRunnable() {
            @Override
            public void run() {
                exitGame();
            }
        }.runTaskLater(PillarsOfFortune.getInstance(), 20 * SECONDS_GAME_OVER);
    }

    private void exitGame() {
        for(Player player : players) {
            if (ScoreboardManager.scoreboards.containsKey(player)) {
                ScoreboardManager.scoreboards.remove(player);
                player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            }
            player.teleport(Config.getLobby());
            player.performCommand("sbreset");
            player.setGameMode(GameMode.SURVIVAL);
            player.getInventory().clear();
            PillarsOfFortune.getManager().softLeavePlayer(player);
        }
        for (int i = firstWoolPos.getBlockX(); i <= secondWoolPos.getBlockX(); i++) {
            for (int j = firstWoolPos.getBlockZ(); j <= secondWoolPos.getBlockZ(); j++) {
                for (int k = firstWoolPos.getBlockY(); k <= secondWoolPos.getWorld().getMaxHeight(); k++) {
                    Block block = firstWoolPos.getWorld().getBlockAt(i, k, j);
                    block.setType(Material.AIR);
                }
            }
        }
        players.clear();
        alive.clear();
        setGameState(GameState.WAITING);
    }

    private double calculateTime(int round) {
        return 5.0 * Math.exp(-0.2 * (round - 1)) + 1.0;
    }

    public void removePlayer(Player player) {
        players.remove(player);
        alive.remove(player);
        if(gameState == GameState.WAITING && players.size() < minPlayers) {
            if(timerTask != null) {
                timerTask.cancel();
                timerTask = null;
                waitingTimer = 20;
            }
        }
        updateScoreboards();
        if(gameState == GameState.RUNNING) {
            if(alive.size() == 1) {
                Player winner = alive.iterator().next();
                PillarsOfFortune.getManager().broadcastMessage(Arena.this, MessageHandler.prefixedMini("<gold>" + winner.getName() + " <aqua>hat das Spiel gewonnen!"));
                gameEnding();
            }
            if(alive.isEmpty()) {
                PillarsOfFortune.getManager().broadcastMessage(Arena.this, MessageHandler.prefixedMini("Niemand hat das Spiel gewonnen :("));
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getSpawn() {
        return spawn;
    }

    public void setSpawn(Location spawn) {
        this.spawn = spawn;
    }

    public Location getSpectatorSpawn() {
        return spectatorSpawn;
    }

    public void setSpectatorSpawn(Location spectatorSpawn) {
        this.spectatorSpawn = spectatorSpawn;
    }

    public Location getFirstWoolPos() {
        return firstWoolPos;
    }

    public void setFirstWoolPos(Location firstWoolPos) {
        this.firstWoolPos = firstWoolPos;
    }

    public Location getSecondWoolPos() {
        return secondWoolPos;
    }

    public void setSecondWoolPos(Location secondWoolPos) {
        this.secondWoolPos = secondWoolPos;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public double getLoseLevel() {
        return loseLevel;
    }

    public void setLoseLevel(double loseLevel) {
        this.loseLevel = loseLevel;
    }

    public Material getSelectorItem() {
        return selectorItem;
    }

    public void setSelectorItem(Material selectorItem) {
        this.selectorItem = selectorItem;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Arena arena = (Arena) o;
        return minPlayers == arena.minPlayers && maxPlayers == arena.maxPlayers && Double.compare(loseLevel, arena.loseLevel) == 0 && Objects.equals(name, arena.name) && Objects.equals(spawn, arena.spawn) && Objects.equals(spectatorSpawn, arena.spectatorSpawn) && Objects.equals(firstWoolPos, arena.firstWoolPos) && Objects.equals(secondWoolPos, arena.secondWoolPos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, spawn, spectatorSpawn, firstWoolPos, secondWoolPos, minPlayers, maxPlayers, loseLevel);
    }

    @Override
    public String toString() {
        return "Arena{" +
                "name='" + name + '\'' +
                ", spawn=" + spawn +
                ", spectatorSpawn=" + spectatorSpawn +
                ", firstWoolPos=" + firstWoolPos +
                ", secondWoolPos=" + secondWoolPos +
                ", minPlayers=" + minPlayers +
                ", maxPlayers=" + maxPlayers +
                ", loseLevel=" + loseLevel +
                '}';
    }

    public void updateScoreboards() {
        players.forEach(ScoreboardManager::setScoreboard);
    }

    public void loseActions(Player player) {
        player.teleport(getSpectatorSpawn());
        alive.remove(player);
        updateScoreboards();
        if(alive.size() == 1) {
            Player winner = alive.iterator().next();
            PillarsOfFortune.getManager().broadcastMessage(Arena.this, MessageHandler.prefixedMini("<gold>" + winner.getName() + " <aqua>hat das Spiel gewonnen!"));
            gameEnding();
        }
        if(alive.isEmpty()) {
            PillarsOfFortune.getManager().broadcastMessage(Arena.this, MessageHandler.prefixedMini("Niemand hat das Spiel gewonnen :("));
        }
    }

    public boolean isAlive(Player player) {
        return alive.contains(player);
    }
}

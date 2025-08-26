package de.pascalpex.pof.util;

import de.pascalpex.pof.PillarsOfFortune;
import io.papermc.paper.scoreboard.numbers.NumberFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;

public class ScoreboardManager {

    public static Map<Player, Scoreboard> scoreboards = new HashMap<>();

    public static void setScoreboard(Player player) {
        if(!scoreboards.containsKey(player)) {
            Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();
            Objective o = sb.registerNewObjective("System", "Scoreboard");
            o.setDisplaySlot(DisplaySlot.SIDEBAR);
            o.numberFormat(NumberFormat.blank());
            o.displayName(Component.text("PillarsOfFortune").color(NamedTextColor.AQUA));
            registerNewTeam(sb, "§0", "§8 ⎯⎯⎯⎯⎯⎯⎯", "§8⎯⎯⎯⎯⎯⎯⎯⎯", "§0");
            o.getScore("§0").setScore(9);
            registerNewTeam(sb, "§1", "§8» ", "§7Arena:", "§1");
            o.getScore("§1").setScore(8);
            registerNewTeam(sb, "§2", "§8 ➥ ", "§b" + PillarsOfFortune.getManager().getPlayerArena(player).getName(), "§2");
            o.getScore("§2").setScore(7);
            registerNewTeam(sb, "§3", "§0", "§0", "§3");
            o.getScore("§3").setScore(6);
            registerNewTeam(sb, "§4", "§8» ", "§7Runde:", "§4");
            o.getScore("§4").setScore(5);
            registerNewTeam(sb, "§5", "§8 ➥ ", "§e" + PillarsOfFortune.getManager().getPlayerArena(player).getItemCounter(), "§5");
            o.getScore("§5").setScore(4);
            registerNewTeam(sb, "§6", "§0", "§0", "§6");
            o.getScore("§6").setScore(3);
            registerNewTeam(sb, "§7", "§8» ", "§7Spieler übrig:", "§7");
            o.getScore("§7").setScore(2);
            registerNewTeam(sb, "§8", "§8 ➥ ", "§2" + PillarsOfFortune.getManager().getPlayerArena(player).getPlayersLeft(), "§8");
            o.getScore("§8").setScore(1);
            registerNewTeam(sb, "§9", "§8 ⎯⎯⎯⎯⎯⎯⎯", "§8⎯⎯⎯⎯⎯⎯⎯⎯", "§9");
            o.getScore("§9").setScore(0);
            player.setScoreboard(sb);
            scoreboards.put(player, sb);
        } else {
            Scoreboard sb = scoreboards.get(player);
            sb.getTeam("§2").setSuffix("§b" + PillarsOfFortune.getManager().getPlayerArena(player).getName());
            sb.getTeam("§5").setSuffix("§e" + PillarsOfFortune.getManager().getPlayerArena(player).getItemCounter());
            sb.getTeam("§8").setSuffix("§2" + PillarsOfFortune.getManager().getPlayerArena(player).getPlayersLeft());
        }
    }

    private static void registerNewTeam(Scoreboard sb, String teamName, String teamPrefix, String teamSuffix, String playerNameToAdd) {
        Team t = sb.registerNewTeam(teamName);
        t.setPrefix(teamPrefix);
        t.setSuffix(teamSuffix);
        t.addEntry(playerNameToAdd);
    }

}

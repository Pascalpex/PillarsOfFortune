package de.pascalpex.pof.commands.subcommands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import de.pascalpex.pof.PillarsOfFortune;
import de.pascalpex.pof.files.Config;
import de.pascalpex.pof.util.MessageHandler;
import de.pascalpex.pof.util.ScoreboardManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class LeaveSubcommand implements Command<CommandSourceStack> {
    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        Entity executor = context.getSource().getExecutor();
        if(executor instanceof Player player) {
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
            } else {
                player.sendMessage(MessageHandler.prefixedMini("<red>Du bist nicht in PillarsOfFortune."));
            }
        } else {
            sender.sendMessage(MessageHandler.prefixedMini("<red>Der Executor muss ein Spieler sein"));
        }

        return SINGLE_SUCCESS;
    }
}

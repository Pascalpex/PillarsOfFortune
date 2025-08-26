package de.pascalpex.pof.commands.subcommands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import de.pascalpex.pof.PillarsOfFortune;
import de.pascalpex.pof.util.MessageHandler;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class JoinSubcommand implements Command<CommandSourceStack> {
    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        Entity executor = context.getSource().getExecutor();
        if(executor instanceof Player player) {
            if (PillarsOfFortune.getManager().isIngame(player)) {
                player.sendMessage(MessageHandler.prefixedMini("<red>Du bist bereits in PillarsOfFortune."));
            } else if (!player.getWorld().getName().equalsIgnoreCase(PillarsOfFortune.GAME_WORLD_NAME)) {
                player.sendMessage(MessageHandler.prefixedMini("<red>Du kannst nur am Spawn beitreten."));
            } else {
                PillarsOfFortune.getManager().openArenaSelector(player);
            }
        } else {
            sender.sendMessage(MessageHandler.prefixedMini("<red>Der Executor muss ein Spieler sein"));
        }

        return SINGLE_SUCCESS;
    }
}

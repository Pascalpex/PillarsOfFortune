package de.pascalpex.pof.commands.subcommands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import de.pascalpex.pof.PillarsOfFortune;
import de.pascalpex.pof.util.MessageHandler;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;

public class VersionSubcommand implements Command<CommandSourceStack> {
    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        sender.sendMessage(MessageHandler.prefixedMini("PillarsOfFortune " + PillarsOfFortune.getPluginVersion() + " von Pascalpex ist aktiviert"));

        return SINGLE_SUCCESS;
    }
}

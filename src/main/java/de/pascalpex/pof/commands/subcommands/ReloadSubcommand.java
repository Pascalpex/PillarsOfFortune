package de.pascalpex.pof.commands.subcommands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import de.pascalpex.pof.PillarsOfFortune;
import de.pascalpex.pof.util.MessageHandler;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;

public class ReloadSubcommand implements Command<CommandSourceStack> {
    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        PillarsOfFortune.reload();
        sender.sendMessage(MessageHandler.prefixedMini("Das Plugin und die Dateien wurden neu geladen"));

        return SINGLE_SUCCESS;
    }
}

package de.pascalpex.pof.commands.subcommands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import de.pascalpex.pof.util.MessageHandler;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.command.CommandSender;

public class HelpSubcommand implements Command<CommandSourceStack> {
    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        HoverEvent<Component> hoverEvent = HoverEvent.showText(MessageHandler.parse("<aqua>Klicke zum ausführen"));

        sender.sendMessage(MessageHandler.prefixedMini("Verfügbare Befehle:"));
        sender.sendMessage(MessageHandler.prefixedMini("/pof help <dark_gray>| <gold>Zeigt diese Seite").hoverEvent(hoverEvent).clickEvent(ClickEvent.suggestCommand("/pof help")));
        sender.sendMessage(MessageHandler.prefixedMini("/pof version <dark_gray>| <gold>Zeigt die installierte Plugin Version").hoverEvent(hoverEvent).clickEvent(ClickEvent.suggestCommand("/pof version")));
        sender.sendMessage(MessageHandler.prefixedMini("/pof join <dark_gray>| <gold>Öffne das Join Menü").hoverEvent(hoverEvent).clickEvent(ClickEvent.suggestCommand("/pof join")));
        sender.sendMessage(MessageHandler.prefixedMini("/pof leave <dark_gray>| <gold>Verlässt das PillarsOfFortune Spiel").hoverEvent(hoverEvent).clickEvent(ClickEvent.suggestCommand("/pof leave")));

        return SINGLE_SUCCESS;
    }
}

package de.pascalpex.pof.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import de.pascalpex.pof.PillarsOfFortune;
import de.pascalpex.pof.util.MessageHandler;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.command.CommandSender;

public class PillarsOfFortuneCommand implements Command<CommandSourceStack> {
    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        HoverEvent<Component> hoverEvent = HoverEvent.showText(MessageHandler.parse("<aqua>Klicke zum ausführen"));

        sender.sendMessage(MessageHandler.prefixedMini("PillarsOfFortune " + PillarsOfFortune.getPluginVersion() + " von Pascalpex ist aktiviert"));
        sender.sendMessage(MessageHandler.prefixedMini("Nutze <gold>/pof help <dark_gray>| <aqua>für eine Befehlsübersicht").hoverEvent(hoverEvent).clickEvent(ClickEvent.suggestCommand("/pof help")));

        return SINGLE_SUCCESS;
    }
}

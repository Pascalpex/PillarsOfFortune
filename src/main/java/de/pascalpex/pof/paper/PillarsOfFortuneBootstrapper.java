package de.pascalpex.pof.paper;

import com.mojang.brigadier.tree.LiteralCommandNode;
import de.pascalpex.pof.PillarsOfFortune;
import de.pascalpex.pof.commands.PillarsOfFortuneCommand;
import de.pascalpex.pof.commands.subcommands.*;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PillarsOfFortuneBootstrapper implements PluginBootstrap {
    @Override
    public void bootstrap(BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            LiteralCommandNode<CommandSourceStack> advancedCommandRoot = Commands.literal("pof")
                    .then(Commands.literal("help")
                            .executes(new HelpSubcommand()))
                    .then(Commands.literal("join")
                            .executes(new JoinSubcommand()))
                    .then(Commands.literal("leave")
                            .executes(new LeaveSubcommand()))
                    .then(Commands.literal("version")
                            .executes(new VersionSubcommand()))
                    .then(Commands.literal("reload")
                            .requires(commandSourceStack -> commandSourceStack.getSender().hasPermission("pof.reload"))
                            .executes(new ReloadSubcommand()))
                    .executes(new PillarsOfFortuneCommand())
                    .build();

            commands.registrar().register(advancedCommandRoot, List.of("pillarsoffortune"));
        });
    }

    @Override
    public @NotNull JavaPlugin createPlugin(@NotNull PluginProviderContext context) {
        return new PillarsOfFortune();
    }
}

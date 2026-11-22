package ru.winxboyz.requests.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import net.kyori.adventure.text.Component;
import ru.winxboyz.requests.ReQuests;
import ru.winxboyz.requests.managers.CommandManager;
// import ru.winxboyz.requests.managers.DatabaseManager;
import ru.winxboyz.requests.managers.PluginManager;

public class ReQuestsCommand {

    private ReQuests plugin;
    private CommandManager commandManager;

    private final LiteralArgumentBuilder<CommandSourceStack>
        // Команда видна и игрокам: раньше она была только для консоли, и в игре
        // её не было видно вообще.
        requests = Commands.literal("requests").requires(ctx -> ctx.getSender().isOp()),
        list     = Commands.literal("list"),
        reload     = Commands.literal("reload"),
        accept   = Commands.literal("accept");
        // add      = Commands.literal("add"),
        // remove   = Commands.literal("remove"),
        // list     = Commands.literal("list"),
        // reload   = Commands.literal("reload");
    private final RequiredArgumentBuilder<CommandSourceStack,PlayerSelectorArgumentResolver>
        player  = Commands.argument("player", ArgumentTypes.player());
    private final RequiredArgumentBuilder<CommandSourceStack,String>
        quest = Commands.argument("quest", StringArgumentType.word()); 
    

    // private DatabaseManager databaseManager = PluginManager.getInstance().getDatabaseManager();

    private final Command<CommandSourceStack>
    executeAccept          = ctx -> {
        PlayerSelectorArgumentResolver resolver = ctx.getArgument("player", PlayerSelectorArgumentResolver.class);
        try {
            Player player = resolver.resolve(ctx.getSource()).getFirst();
            String questName = ctx.getArgument("quest", String.class);
            commandManager.handleAccept(player, questName);
        } catch(CommandSyntaxException e) {
            plugin.getLogger().warning(commandManager.getMessages().get("syntax-error") + " add:\n" + e.getMessage());
        }
        return Command.SINGLE_SUCCESS;
    },
    executeList = ctx -> {
        CommandSender sender = ctx.getSource().getSender();
        commandManager.handleList(sender);
        return Command.SINGLE_SUCCESS;
    },
    executeReload = ctx -> {
        plugin.reload();
        ctx.getSource().getSender().sendMessage(Component.text("Данные конфигурации перезагружены"));
        return Command.SINGLE_SUCCESS;
    };
    // executeAddRegion    = ctx -> {
    //     commandManager.handleLocations((Player) ctx.getSource().getSender(), "add", databaseManager);
    //     return Command.SINGLE_SUCCESS;
    // },
    // executeRemove       = ctx -> {
    //     Player player = (Player) ctx.getSource().getSender();
    //     World world = player.getWorld();

    //     BlockPositionResolver resolver = ctx.getArgument("pos", BlockPositionResolver.class);
    //     try {
    //         Location location = resolver.resolve(ctx.getSource()).toLocation(world).toBlockLocation();
    //         commandManager.removeLocation(player, location, databaseManager);
    //     }  catch(CommandSyntaxException e) {
    //         plugin.getLogger().warning(commandManager.getMessages().get("syntax-error") + " remove:\n" + e.getMessage());
    //     }
    //     return Command.SINGLE_SUCCESS;
    // },
    // executeRemoveRegion = ctx -> {
    //     commandManager.handleLocations((Player) ctx.getSource().getSender(), "remove", databaseManager);
    //     return Command.SINGLE_SUCCESS;
    // };
    
    // private final int LIMIT = 10;

    // private void displayPage(Player player, int page) {
    //     databaseManager.fetchLocations(LIMIT, page).thenAccept(locs -> {
    //         Bukkit.getScheduler().runTask(plugin, () -> {
    //             Component output = Component.text("--------------------------------------");
    //             for(Location loc : locs)
    //                 output = output
    //                     .append(Component.text("\n"+loc.getWorld().getName()).color(NamedTextColor.AQUA))
    //                     .append(Component.text(" " + loc.blockX() + " " + loc.blockY() + " " + loc.blockZ()).color(NamedTextColor.GOLD));
    //             player.sendMessage(output);
    //         });
    //     });
    // }

    public LiteralCommandNode<CommandSourceStack> initialize() {
        LiteralArgumentBuilder<CommandSourceStack> builder = requests
        .then(player.then(quest.then(accept.executes(executeAccept))))
        .then(list.executes(executeList))
        .then(reload.executes(executeReload));
            // .then(add   .executes(executeAddRegion)   .then(pos.executes(executeAdd)))
            // .then(remove.executes(executeRemoveRegion).then(pos.executes(executeRemove)))
            // .then(list.executes(ctx -> {
            //     Player player = (Player) ctx.getSource().getSender();
            //     displayPage(player, 1);

            //     return Command.SINGLE_SUCCESS;
            // }).then(page.executes(ctx -> {
            //     Player player = (Player) ctx.getSource().getSender();
            //     int pg = ctx.getArgument("page", Integer.class);
            //     displayPage(player, pg);

            //     return Command.SINGLE_SUCCESS;
            // })))
            // .then(reload.executes(ctx -> {
            //     Player player = (Player) ctx.getSource().getSender();
            //     plugin.reload();
            //     player.sendMessage(Component.text(commandManager.getMessages().get("reloaded")).color(NamedTextColor.GREEN));
            //     return Command.SINGLE_SUCCESS;
            // }));
        return builder.build();
    }

    public ReQuestsCommand(ReQuests plugin) {
        this.plugin = plugin;
        this.commandManager = PluginManager.getInstance().getCommandManager();
    }
}

package ru.winxboyz.requests.commands;

import org.bukkit.entity.Player;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import ru.winxboyz.requests.managers.QuestManager;

public class JournalCommand {

    private QuestManager manager;

    private LiteralArgumentBuilder<CommandSourceStack> journal = Commands.literal("journal");

    public LiteralCommandNode<CommandSourceStack> initialize() {
        return journal.executes(ctx -> {
            if(ctx.getSource().getSender() instanceof Player player)
                manager.handleJournal(player);
            return Command.SINGLE_SUCCESS;
        }).build();
    }

    public JournalCommand(QuestManager manager) {
        this.manager = manager;
    }

}

package com.nullXer0.beebot.commands;

import com.nullXer0.beebot.BeeBot;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.HashSet;
import java.util.List;

public class CommandHandler
{
    HashSet<BaseSlashCommand> commands = new HashSet<>();

    public CommandHandler(BaseSlashCommand... commands)
    {
        this.commands.addAll(List.of(commands));
    }

    public void updateCommands()
    {
        BeeBot.getJDA().updateCommands().addCommands(commands.stream().map(BaseSlashCommand::build).toList()).queue();
    }

    public void handleCommands(SlashCommandInteractionEvent event)
    {
        for(BaseSlashCommand command : commands)
        {
            if(command.execute(event))
                return;
        }
    }

    public void registerCommand(BaseSlashCommand command)
    {
        commands.add(command);
    }

    public void unregisterCommand(BaseSlashCommand command)
    {
        commands.remove(command);
    }
}

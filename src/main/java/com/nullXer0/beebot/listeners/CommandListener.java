package com.nullXer0.beebot.listeners;

import com.nullXer0.beebot.commands.CommandHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class CommandListener extends ListenerAdapter
{
    CommandHandler commandHandler;

    public CommandListener(CommandHandler handler)
    {
        commandHandler = handler;
    }

    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event)
    {
        commandHandler.handleCommands(event);
    }
}

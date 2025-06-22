package com.nullXer0.beebot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public abstract class BaseSlashCommand
{
    protected String name;
    protected String description;
    protected DefaultMemberPermissions permissions;
    protected OptionData[] options;

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public final SlashCommandData build()
    {
        SlashCommandData data = Commands.slash(name, description);
        if(options!=null)
        {
            data.addOptions(options);
        }
        if(permissions!=null)
        {
            data.setDefaultPermissions(permissions);
        }
        return data;
    }

    public final boolean execute(SlashCommandInteractionEvent event)
    {
        if(event.getName().equals(name))
        {
            run(event);
            return true;
        }
        return false;
    }

    protected abstract void run(SlashCommandInteractionEvent event);
}

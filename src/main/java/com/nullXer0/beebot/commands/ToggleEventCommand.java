package com.nullXer0.beebot.commands;

import com.nullXer0.beebot.scheduling.reminders.EventReminders;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class ToggleEventCommand extends BaseSlashCommand
{
    public ToggleEventCommand()
    {
        name = "toggleevent";
        description = "toggles the team event for the day";
        options = new OptionData[]{
                new OptionData(OptionType.STRING, "team", "The team to toggle events for", true)
                        .addChoice("Yellow", "yellow")
                        .addChoice("Black", "black")
        };
    }

    @Override
    protected void run(SlashCommandInteractionEvent event)
    {
        switch(event.getOption("team", OptionMapping::getAsString))
        {
            case "yellow" ->
            {
                if(EventReminders.toggleYellowEventToday())
                    event.reply("Next event for team yellow will be skipped!").queue();
                else
                    event.reply("Next event for team yellow will no longer be skipped!").queue();
            }
            case "black" ->
            {
                if(EventReminders.toggleBlackEventToday())
                    event.reply("Next event for team black will be skipped!").queue();
                else
                    event.reply("Next event for team black will no longer be skipped!").queue();
            }
            case null, default -> event.reply("Invalid team!").setEphemeral(true).queue();
        }
    }
}

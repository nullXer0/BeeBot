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
                if(!EventReminders.toggleYellowEventToday())
                    event.reply("Yellow event skipped for today!").queue();
                else
                    event.reply("Yellow event are no longer skipped for today!").queue();
            }
            case "black" ->
            {
                if(!EventReminders.toggleBlackEventToday())
                    event.reply("Black event skipped for today!").queue();
                else
                    event.reply("Black event are no longer skipped for today!").queue();
            }
            case null, default -> event.reply("Invalid team!").setEphemeral(true).queue();
        }
    }
}

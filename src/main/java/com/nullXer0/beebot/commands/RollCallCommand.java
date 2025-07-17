package com.nullXer0.beebot.commands;

import com.nullXer0.beebot.RollCallSender;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Calendar;
import java.util.TimeZone;

public class RollCallCommand extends BaseSlashCommand
{
    public RollCallCommand()
    {
        name = "rollcall";
        description = "Post a poll for attendance for an event";
        permissions = DefaultMemberPermissions.enabledFor(Permission.MESSAGE_SEND_POLLS);
        // <channel> <type> <hour> [minute] [am_pm] [day] [length_hours] [length_minutes]
        options = new OptionData[]{
                new OptionData(OptionType.CHANNEL, "channel", "The channel to post the poll", true)
                        .setChannelTypes(ChannelType.TEXT),
                new OptionData(OptionType.STRING, "type", "The type of event", true),
                new OptionData(OptionType.INTEGER, "hour", "The hour of the event (Eastern)", true)
                        .setRequiredRange(1, 12),
                new OptionData(OptionType.INTEGER, "minute", "The minute of the event (defaults to 0)")
                        .setRequiredRange(0, 59),
                new OptionData(OptionType.INTEGER, "am_pm", "Whether the event is in the AM or PM (Defaults to PM)")
                        .addChoice("AM", 0)
                        .addChoice("PM", 1),
                new OptionData(OptionType.INTEGER, "day", "The day of the event")
                        .addChoice("Monday", 2)
                        .addChoice("Tuesday", 3)
                        .addChoice("Wednesday", 4)
                        .addChoice("Thursday", 5)
                        .addChoice("Friday", 6)
                        .addChoice("Saturday", 7)
                        .addChoice("Sunday", 1),
                new OptionData(OptionType.INTEGER, "length_hours", "How many hours the event goes for")
                        .setRequiredRange(1, 6),
                new OptionData(OptionType.INTEGER, "length_minutes", "How many additional minutes the event goes for (defaults to 0)")
                        .setRequiredRange(0, 59)
        };
    }

    @Override
    protected void run(SlashCommandInteractionEvent event)
    {
        //noinspection DataFlowIssue
        TextChannel channel = event.getOption("channel", OptionMapping::getAsChannel).asTextChannel();
        String eventType = event.getOption("type", OptionMapping::getAsString);
        int hour = event.getOption("hour", 0, OptionMapping::getAsInt);
        int minute = event.getOption("minute", 0, OptionMapping::getAsInt);
        int am_pm = event.getOption("am_pm", 1, OptionMapping::getAsInt);
        int day = event.getOption("day", -1, OptionMapping::getAsInt);
        int lengthHours = event.getOption("length_hours", 2, OptionMapping::getAsInt);
        int lengthMinutes = event.getOption("length_minutes", 0, OptionMapping::getAsInt);

        try
        {
            Calendar startCal = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"));
            if(day != -1)
                startCal.set(Calendar.DAY_OF_WEEK, day);
            startCal.set(Calendar.HOUR, hour);
            startCal.set(Calendar.MINUTE, minute);
            startCal.set(Calendar.AM_PM, am_pm);

            if(startCal.before(Calendar.getInstance(TimeZone.getTimeZone("America/New_York"))))
            {
                // If the time is before the current time, set it to the next week
                startCal.add(Calendar.WEEK_OF_YEAR, 1);
            }

            Calendar endCal = (Calendar) startCal.clone();
            endCal.add(Calendar.HOUR, lengthHours);
            endCal.add(Calendar.MINUTE, lengthMinutes);

            RollCallSender.sendRollCall(channel, eventType, startCal, endCal);
            event.reply("Successfully send poll message").setEphemeral(true).queue();
        }
        catch(IllegalArgumentException ex)
        {
            event.reply("Invalid date provided.").setEphemeral(true).queue();
        }
    }
}

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
        options = new OptionData[]{
                new OptionData(OptionType.CHANNEL, "channel", "The channel to post the poll", true)
                        .setChannelTypes(ChannelType.TEXT),
                new OptionData(OptionType.STRING, "type", "The type of event", true)
                        .addChoice("Scrim", "Scrim")
                        .addChoice("VOD Review", "VOD Review"),
                new OptionData(OptionType.INTEGER, "month", "The month of the event", true),
                new OptionData(OptionType.INTEGER, "day", "The day of the event", true),
                new OptionData(OptionType.INTEGER, "hour", "The hour of the event", true),
                new OptionData(OptionType.INTEGER, "length_hours", "How many hours the event goes for", true),
                new OptionData(OptionType.INTEGER, "minute", "The minute of the event (defaults to 0)"),
                new OptionData(OptionType.INTEGER, "length_minutes", "How many additional minutes the event goes for (defaults to 0)")
        };
        // {channel} {name} {month} {day} {hour24} {minute} {duriationH} {durationM}
    }

    @Override
    protected void run(SlashCommandInteractionEvent event)
    {
        TextChannel channel = event.getOption("channel", OptionMapping::getAsChannel).asTextChannel();
        String eventType = event.getOption("type", OptionMapping::getAsString);
        int month = event.getOption("month", 0, OptionMapping::getAsInt)-1;
        int day = event.getOption("day", 0, OptionMapping::getAsInt);
        int hour = event.getOption("hour", 0, OptionMapping::getAsInt);
        int lengthHours = event.getOption("length_hours", 0, OptionMapping::getAsInt);
        int minute = event.getOption("minute", 0, OptionMapping::getAsInt);
        int lengthMinutes = event.getOption("length_minutes", 0, OptionMapping::getAsInt);

        Calendar startCal = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"));
        if(month < startCal.get(Calendar.MONTH))
        {
            startCal.add(Calendar.YEAR, 1);
        }
        startCal.set(Calendar.MONTH, month);
        startCal.set(Calendar.DAY_OF_MONTH, day);
        startCal.set(Calendar.HOUR_OF_DAY, hour);
        startCal.set(Calendar.MINUTE, minute);

        Calendar endCal = (Calendar) startCal.clone();
        endCal.add(Calendar.HOUR_OF_DAY, lengthHours);
        endCal.add(Calendar.MINUTE, lengthMinutes);

        RollCallSender.sendRollCall(channel, eventType, startCal, endCal);
        event.reply("Successfully send poll message").setEphemeral(true).queue();
    }
}

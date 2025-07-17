package com.nullXer0.beebot;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.utils.messages.MessagePollBuilder;

import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class RollCallSender
{
    public static void sendRollCall(long channelID, String event, Calendar startCal, Calendar endCal, int duration)
    {
        Guild guild = BeeBot.getJDA().getGuildById(BeeBot.getConfig().getLong("guild"));
        TextChannel channel = guild.getTextChannelById(channelID);

        sendRollCall(channel, event, startCal, endCal, duration);
    }

    public static void sendRollCall(TextChannel channel, String event, Calendar startCal, Calendar endCal, int duration)
    {
        // Will you be available for the Monday (23 Jun) scrim at 6:00pm-8:00pm EDT?
        MessagePollBuilder builder = new MessagePollBuilder(String.format("Will you be available for the %2$tA (%2$te %2$tb) %1$s at %2$tI:%2$tM%2$tp-%3$tI:%3$tM%3$tp %2$tZ?", event, startCal, endCal));

        // Add yes/no answers
        builder.addAnswer("Yes, I will be available.", Emoji.fromUnicode("U+31 U+FE0F U+20E3"));
        builder.addAnswer("No, I won't be available.", Emoji.fromUnicode("U+32 U+FE0F U+20E3"));
        builder.addAnswer("Not sure yet / TBD.", Emoji.fromUnicode("U+33 U+FE0F U+20E3"));

        if(duration != 0)
        {
            builder.setDuration(duration, TimeUnit.MINUTES);
        }
        else
        {
            long minutesUntil = ChronoUnit.MINUTES.between(Calendar.getInstance(TimeZone.getTimeZone("America/New_York")).toInstant(), startCal.toInstant());
            if(minutesUntil < 60)
                // 1 hour minimum
                minutesUntil = 60;
            else if(minutesUntil < 1440)
                // 1 hour before if less than 24 hours
                minutesUntil = minutesUntil - 60;
            else
                // 24 hours before if more than 24 hours
                minutesUntil = minutesUntil - 1440;
            builder.setDuration(minutesUntil, TimeUnit.MINUTES);
        }

        // Send the poll message
        channel.sendMessagePoll(builder.build()).queue();
    }

    public static void sendRollCall(TextChannel channel, String event, Calendar startCal, Calendar endCal)
    {
        sendRollCall(channel, event, startCal, endCal, 0);
    }

    public static void sendRollCall(long channelID, String event, Calendar startCal, Calendar endCal)
    {
        sendRollCall(channelID, event, startCal, endCal, 0);
    }

    public static Calendar createCalendar(int hour, int minute, int am_pm, int day)
    {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"));
        cal.add(Calendar.WEEK_OF_YEAR, 1);
        cal.set(Calendar.HOUR, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.AM_PM, am_pm);
        cal.set(Calendar.DAY_OF_WEEK, day);
        return cal;
    }
}

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
    public static void sendRollCall(long channelID, String event, Calendar startCal, Calendar endCal)
    {
        Guild guild = BeeBot.getJDA().getGuildById(BeeBot.getConfig().getLong("guild"));
        TextChannel channel = guild.getTextChannelById(channelID);

        sendRollCall(channel, event, startCal, endCal);
    }

    public static void sendRollCall(TextChannel channel, String event, Calendar startCal, Calendar endCal)
    {
        // Will you be available for the Monday (23 Jun) scrim at 6:00pm-8:00pm EDT?
        MessagePollBuilder builder = new MessagePollBuilder(String.format("Will you be available for the %2$tA (%2$te %2$tb) %1$s at %2$tI:%2$tM%2$tp-%3$tI:%3$tM%3$tp %2$tZ?", event, startCal, endCal));

        // Add yes/no answers
        // <:THUMB_UP:1239251740247851171>
        builder.addAnswer("Yes, I will.", Emoji.fromUnicode("\uD83D\uDC4D"));
        // <:THUMB_DOWN:1239251771327516803>
        builder.addAnswer("No, I Won't.", Emoji.fromUnicode("\uD83D\uDC4E"));

        // Set the expiration to 24 hours before the event.
        long minutesUntil = ChronoUnit.MINUTES.between(Calendar.getInstance(TimeZone.getTimeZone("America/New_York")).toInstant(), startCal.toInstant()) - 1440;
        if(minutesUntil<60)
            minutesUntil = 60;
        builder.setDuration(minutesUntil, TimeUnit.MINUTES);

        // Send the poll message
        channel.sendMessagePoll(builder.build()).queue();
    }
}

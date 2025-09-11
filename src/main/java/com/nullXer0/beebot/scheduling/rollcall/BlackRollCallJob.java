package com.nullXer0.beebot.scheduling.rollcall;

import com.nullXer0.beebot.BeeBot;
import com.nullXer0.beebot.RollCallSender;
import com.nullXer0.beebot.scheduling.BaseJob;
import com.typesafe.config.Config;
import net.dv8tion.jda.api.JDA;
import org.quartz.*;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class BlackRollCallJob extends BaseJob
{
    private static final List<Integer> SCRIM_DAYS = Arrays.asList(Calendar.MONDAY, Calendar.THURSDAY, Calendar.FRIDAY);
    private static final List<Integer> VOD_DAYS = Arrays.asList(Calendar.SATURDAY);

    public JobDetail getJobDetail()
    {
        return JobBuilder.newJob(BlackRollCallJob.class)
                .withIdentity("black")
                .withDescription("Sends team black roll call for scrims and VOD reviews")
                .storeDurably()
                .build();
    }

    public Trigger getTrigger()
    {
        return TriggerBuilder.newTrigger()
                .withIdentity("black")
                .withDescription("Trigger team black for sending roll call")
                .startNow()
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 16 ? * THU"))// Every Thursday at 12pm
                .build();
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext)
    {
        Config config = BeeBot.getConfig();
        JDA jda = BeeBot.getJDA();

        // Text Channels
        long tryoutsChannel = config.getLong("channels.blackTryouts");
        long pollChannel = config.getLong("channels.blackPolls");

        // Roles
        long teamRole = config.getLong("roles.blackTeam");
        long tryoutsRole = config.getLong("roles.blackTryouts");

        boolean tryoutsOpen = config.getBoolean("tryouts.black");

        // Ping relavent roles
        if(!SCRIM_DAYS.isEmpty() || !VOD_DAYS.isEmpty())
        {
            jda.getTextChannelById(pollChannel)
                    .sendMessage(String.format("""
                                    📣 %s
                                    Please respond to the polls to help us capture player availability for this week’s activities. Your input helps us plan sessions more effectively.""",
                            jda.getRoleById(teamRole).getAsMention())).queue();
        }
        if(tryoutsOpen && !SCRIM_DAYS.isEmpty())
        {
            jda.getTextChannelById(tryoutsChannel)
                    .sendMessage(String.format("""
                                    📣 %s
                                    Please respond to the polls to help us capture player availability for this week’s activities. Your input helps us plan sessions more effectively.""",
                            jda.getRoleById(tryoutsRole).getAsMention())).queue();
        }

        // Post polls
        for(int day = 1; day <= 7; day++)
        {
            if(SCRIM_DAYS.contains(day))
            {
                sendPoll(7, 45, 10, 0, day, "scrims", pollChannel);
                if(tryoutsOpen)
                {
                    sendPoll(7, 45, 10, 0, day, "tryouts", tryoutsChannel);
                }
            }
            if(VOD_DAYS.contains(day))
                sendPoll(7, 45, 10, 0, day, "VOD review", pollChannel);
        }
    }

    private static void sendPoll(int startHour, int startMinute, int endHour, int endMinute, int dayOfWeek, String eventType, long channelID)
    {
        Calendar startCal = RollCallSender.createCalendar(startHour, startMinute, Calendar.PM, dayOfWeek);
        Calendar endCal = (Calendar) startCal.clone();
        endCal.set(Calendar.HOUR, endHour);
        endCal.set(Calendar.MINUTE, endMinute);

        // Send the roll call for the scrim
        RollCallSender.sendRollCall(channelID, eventType, startCal, endCal, 4320);
    }

    public static List<Integer> getScrimDays()
    {
        return List.copyOf(SCRIM_DAYS);
    }

    public static List<Integer> getVodDays()
    {
        return List.copyOf(VOD_DAYS);
    }
}

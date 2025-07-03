package com.nullXer0.beebot.scheduling.rollcall;

import com.nullXer0.beebot.BeeBot;
import com.nullXer0.beebot.RollCallSender;
import com.nullXer0.beebot.scheduling.BaseJob;
import com.typesafe.config.Config;
import net.dv8tion.jda.api.JDA;
import org.quartz.*;

import java.util.Calendar;

public class YellowRollCallJob extends BaseJob
{
    public JobDetail getJobDetail()
    {
        return JobBuilder.newJob(YellowRollCallJob.class)
                .withIdentity("yellow")
                .withDescription("Sends team yellow roll call for scrims and VOD reviews")
                .storeDurably()
                .build();
    }

    public Trigger getTrigger()
    {
        return TriggerBuilder.newTrigger()
                .withIdentity("yellow")
                .withDescription("Trigger for sending team yellow roll call")
                .startNow()
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 12 ? * THU"))// Every Thursday at 12pm
                .build();
    }

    public void addToScheduler(Scheduler scheduler) throws SchedulerException
    {
        scheduler.scheduleJob(getJobDetail(), getTrigger());
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext)
    {
        Config config = BeeBot.getConfig();
        JDA jda = BeeBot.getJDA();

        // Text Channels
        long scrimChannel = config.getLong("channels.yellowScrim");
        long vodChannel = config.getLong("channels.yellowVOD");
        long tryoutsChannel = config.getLong("channels.yellowTryouts");

        // Roles
        long teamRole = config.getLong("roles.yellowTeam");
        long tryoutsRole = config.getLong("roles.yellowTryouts");

        boolean tryoutsOpen = config.getBoolean("tryouts.yellow");

        // Ping relavent roles
        jda.getTextChannelById(scrimChannel)
                .sendMessage(String.format("""
                                📣 %s
                                Please respond to the polls to help us capture player availability for this week’s activities. Your input helps us plan sessions more effectively.""",
                        jda.getRoleById(teamRole).getAsMention())).queue();
        jda.getTextChannelById(vodChannel)
                .sendMessage(String.format("""
                                📣 %s
                                Please respond to the polls to help us capture player availability for this week’s activities. Your input helps us plan sessions more effectively.""",
                        jda.getRoleById(teamRole).getAsMention())).queue();
        if(tryoutsOpen)
        {
            jda.getTextChannelById(tryoutsChannel)
                    .sendMessage(String.format("""
                                    📣 %s
                                    Please respond to the polls to help us capture player availability for this week’s activities. Your input helps us plan sessions more effectively.""",
                            jda.getRoleById(tryoutsRole).getAsMention())).queue();
        }

        for(int i = 0; i < 5; i++)
        {
            // VOD reviews on Wednesdays at 7:45pm
            int startHour = i == 2 ? 7 : 6;
            String eventType = i == 2 ? "VOD review" : tryoutsOpen ? "tryout scrims" : "scrims";

            sendPoll(startHour, 45, 10, 0, Calendar.MONDAY + i, eventType, i == 2 ? vodChannel : scrimChannel);

            //Tryouts
            if(tryoutsOpen && i != 2)
            {
                sendPoll(6, 45, 8, 0, Calendar.MONDAY + i, "tryouts", tryoutsChannel);
            }
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
}

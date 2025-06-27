package com.nullXer0.beebot.scheduling.rollcall;

import com.nullXer0.beebot.BeeBot;
import com.nullXer0.beebot.RollCallSender;
import com.nullXer0.beebot.scheduling.BaseJob;
import com.typesafe.config.Config;
import org.quartz.*;

import java.util.Calendar;

public class BlackRollCallJob extends BaseJob
{
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
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 12 ? * THU"))// Every Saturday at 12pm
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

        // Text Channels
        long scrimChannel = config.getLong("channels.blackScrim");
        long vodChannel = config.getLong("channels.blackVOD");
        long tryoutsChannel = config.getLong("channels.blackTryouts");

        boolean tryoutsOpen = config.getBoolean("tryouts.black");

        // scrims and vod reviews
        for(int i = 0; i < 5; i++)
        {
            // VOD reviews on Wednesdays
            String eventType = i == 2 ? "VOD review" : "scrims";
            sendPoll(i == 2 ? 7 : 6, 45, 10, 0, Calendar.MONDAY + i, eventType, i == 2 ? vodChannel : scrimChannel);

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

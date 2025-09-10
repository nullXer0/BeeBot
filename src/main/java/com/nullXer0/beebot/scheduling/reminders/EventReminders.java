package com.nullXer0.beebot.scheduling.reminders;

import com.nullXer0.beebot.BeeBot;
import com.nullXer0.beebot.scheduling.BaseJob;
import com.nullXer0.beebot.scheduling.rollcall.BlackRollCallJob;
import com.nullXer0.beebot.scheduling.rollcall.YellowRollCallJob;
import net.dv8tion.jda.api.JDA;
import org.quartz.*;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EventReminders extends BaseJob
{
    private static boolean skipYellowEvent = false;
    private static boolean skipBlackEvent = false;

    private static final String REMINDER_SCRIM_1_HOUR = """
            %s — ⚔️ Scrim at 8p ET!
            """;
    private static final String REMINDER_SCRIM_30_MINUTES = """
            %s — ⚔️ 30 minutes until scrim!
            📌 No more ranked — time to focus, hydrate, and get ready.
            """;
    private static final String REMINDER_SCRIM_15_MINUTES = """
            %s — ⚔️ 15 minutes until scrim!
            📌 Join VC and launch your game. Get in the room early.
            """;
    private static final String REMINDER_VOD_1_HOUR = """
            %s — 🎥 VOD review starts in 1 hour!
            📌 Use the time to decompress, take notes, or rewatch key rounds.
            """;
    private static final String REMINDER_VOD_30_MINUTES = """
            %s — 🎥 VOD review in 30 minutes!
            📌 Grab snacks, hydrate, and no more ranked.
            """;
    private static final String REMINDER_VOD_15_MINUTES = """
            %s — 🎥 VOD review in 15 minutes!
            📌 Time to load into VC. We'll begin shortly.
            """;

    @Override
    public JobDetail getJobDetail()
    {
        return JobBuilder.newJob(EventReminders.class)
                .withIdentity("scrimReminders")
                .withDescription("Sends 1h, 30m, and 15m reminders for scrims and VOD reviews")
                .storeDurably()
                .build();
    }

    @Override
    public Trigger getTrigger()
    {
        return TriggerBuilder.newTrigger()
                .withIdentity("reminderYellow1h")
                .withDescription("1 hour reminder for team yellow roll call")
                .startNow()
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0,30,45 23 ? * *"))
                .build();
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException
    {
        JDA jda = BeeBot.getJDA();
        Date scheduledFireTime = jobExecutionContext.getScheduledFireTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(scheduledFireTime);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        int minute = calendar.get(Calendar.MINUTE);

        long yellowScrimChannel = BeeBot.getConfig().getLong("channels.yellowScrim");
        long yellowVodChannel = BeeBot.getConfig().getLong("channels.yellowVOD");
        long yellowTeamRole = BeeBot.getConfig().getLong("roles.yellowTeam");

        long blackScrimChannel = BeeBot.getConfig().getLong("channels.blackScrim");
        long blackVodChannel = BeeBot.getConfig().getLong("channels.blackVOD");
        long blackTeamRole = BeeBot.getConfig().getLong("roles.blackTeam");

        List<Integer> yellowScrimDays = YellowRollCallJob.getScrimDays();
        List<Integer> yellowVodDays = YellowRollCallJob.getVodDays();
        List<Integer> blackScrimDays = BlackRollCallJob.getScrimDays();
        List<Integer> blackVodDays = BlackRollCallJob.getVodDays();

        switch(minute)
        {
            case 0 ->
            {
                if(!skipYellowEvent)
                {
                    if(yellowScrimDays.contains(day))
                        jda.getTextChannelById(yellowScrimChannel)
                                .sendMessage(String.format(REMINDER_SCRIM_1_HOUR, jda.getRoleById(yellowTeamRole).getAsMention()))
                                .setSuppressedNotifications(true).queue();
                    if(yellowVodDays.contains(day))
                        jda.getTextChannelById(yellowVodChannel)
                                .sendMessage(String.format(REMINDER_VOD_1_HOUR, jda.getRoleById(yellowTeamRole).getAsMention()))
                                .setSuppressedNotifications(true).queue();
                }
                if(!skipBlackEvent)
                {
                    if(blackScrimDays.contains(day))
                        jda.getTextChannelById(blackScrimChannel)
                                .sendMessage(String.format(REMINDER_SCRIM_1_HOUR, jda.getRoleById(blackTeamRole).getAsMention()))
                                .setSuppressedNotifications(true).queue();
                    if(blackVodDays.contains(day))
                        jda.getTextChannelById(blackVodChannel)
                                .sendMessage(String.format(REMINDER_VOD_1_HOUR, jda.getRoleById(blackTeamRole).getAsMention()))
                                .setSuppressedNotifications(true).queue();
                }
            }
            case 30 ->
            {
                if(!skipYellowEvent)
                {
                    if(yellowScrimDays.contains(day))
                        jda.getTextChannelById(yellowScrimChannel)
                                .sendMessage(String.format(REMINDER_SCRIM_30_MINUTES, jda.getRoleById(yellowTeamRole).getAsMention()))
                                .queue();
                    if(yellowVodDays.contains(day))
                        jda.getTextChannelById(yellowVodChannel)
                                .sendMessage(String.format(REMINDER_VOD_30_MINUTES, jda.getRoleById(yellowTeamRole).getAsMention()))
                                .queue();
                }
                if(!skipBlackEvent)
                {
                    if(blackScrimDays.contains(day))
                        jda.getTextChannelById(blackScrimChannel)
                                .sendMessage(String.format(REMINDER_SCRIM_30_MINUTES, jda.getRoleById(blackTeamRole).getAsMention()))
                                .setSuppressedNotifications(true).queue();
                    if(blackVodDays.contains(day))
                        jda.getTextChannelById(blackVodChannel)
                                .sendMessage(String.format(REMINDER_VOD_30_MINUTES, jda.getRoleById(blackTeamRole).getAsMention()))
                                .setSuppressedNotifications(true).queue();
                }
            }
            case 45 ->
            {
                if(!skipYellowEvent)
                {
                    if(yellowScrimDays.contains(day))
                        jda.getTextChannelById(yellowScrimChannel)
                                .sendMessage(String.format(REMINDER_SCRIM_15_MINUTES, jda.getRoleById(yellowTeamRole).getAsMention()))
                                .queue();
                    if(yellowVodDays.contains(day))
                        jda.getTextChannelById(yellowVodChannel)
                                .sendMessage(String.format(REMINDER_VOD_15_MINUTES, jda.getRoleById(yellowTeamRole).getAsMention()))
                                .queue();
                    skipYellowEvent = false; // reset for next week
                }
                if(!skipBlackEvent)
                {
                    if(blackScrimDays.contains(day))
                        jda.getTextChannelById(blackScrimChannel)
                                .sendMessage(String.format(REMINDER_SCRIM_15_MINUTES, jda.getRoleById(blackTeamRole).getAsMention()))
                                .setSuppressedNotifications(true).queue();
                    if(blackVodDays.contains(day))
                        jda.getTextChannelById(blackVodChannel)
                                .sendMessage(String.format(REMINDER_VOD_15_MINUTES, jda.getRoleById(blackTeamRole).getAsMention()))
                                .setSuppressedNotifications(true).queue();
                    skipBlackEvent = false; // reset for next week
                }
            }
        }
    }

    /**
     * @return the new value of yellowEventToday. True if yellow events are NOT skipped for today, false if they are.
     */
    public static boolean toggleYellowEventToday()
    {
        return skipYellowEvent = !skipYellowEvent;
    }

    /**
     * @return the new value of blackEventToday. True if black events are NOT skipped for today, false if they are.
     */
    public static boolean toggleBlackEventToday()
    {
        return skipBlackEvent = !skipBlackEvent;
    }
}

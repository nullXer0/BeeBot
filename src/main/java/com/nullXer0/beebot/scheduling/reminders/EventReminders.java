package com.nullXer0.beebot.scheduling.reminders;

import com.nullXer0.beebot.BeeBot;
import com.nullXer0.beebot.scheduling.BaseJob;
import net.dv8tion.jda.api.JDA;
import org.quartz.*;

import java.util.Calendar;
import java.util.Date;

public class EventReminders extends BaseJob
{
    private static boolean yellowEventToday = true;
    private static boolean blackEventToday = true;

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
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0,30,45 23 ? * MON-FRI"))// Every Thursday at 12pm
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

        boolean yellowVodDay = day == Calendar.TUESDAY;
        boolean blackVodDay = day == Calendar.WEDNESDAY;

        switch(minute)
        {
            case 0 ->
            {
                if(yellowEventToday)
                    jda.getTextChannelById(yellowVodDay ? yellowVodChannel : yellowScrimChannel).sendMessage(String.format(yellowVodDay ?
                            REMINDER_VOD_1_HOUR : REMINDER_SCRIM_1_HOUR, jda.getRoleById(yellowTeamRole).getAsMention())).setSuppressedNotifications(true).queue();
                if(blackEventToday)
                    jda.getTextChannelById(blackVodDay ? blackVodChannel : blackScrimChannel).sendMessage(String.format(blackVodDay ?
                            REMINDER_VOD_1_HOUR : REMINDER_SCRIM_1_HOUR, jda.getRoleById(blackTeamRole).getAsMention())).setSuppressedNotifications(true).queue();
            }
            case 30 ->
            {
                if(yellowEventToday)
                    jda.getTextChannelById(yellowVodDay ? yellowVodChannel : yellowScrimChannel).sendMessage(String.format(yellowVodDay ?
                            REMINDER_VOD_30_MINUTES : REMINDER_SCRIM_30_MINUTES, jda.getRoleById(yellowTeamRole).getAsMention())).queue();
                if(blackEventToday)
                    jda.getTextChannelById(blackVodDay ? blackVodChannel : blackScrimChannel).sendMessage(String.format(blackVodDay ?
                            REMINDER_VOD_30_MINUTES : REMINDER_SCRIM_30_MINUTES, jda.getRoleById(blackTeamRole).getAsMention())).queue();
            }
            case 45 ->
            {
                if(yellowEventToday)
                    jda.getTextChannelById(yellowVodDay ? yellowVodChannel : yellowScrimChannel).sendMessage(String.format(yellowVodDay ?
                            REMINDER_VOD_15_MINUTES : REMINDER_SCRIM_15_MINUTES, jda.getRoleById(yellowTeamRole).getAsMention())).queue();
                else
                    yellowEventToday = true; // reset for next week
                if(blackEventToday)
                    jda.getTextChannelById(blackVodDay ? blackVodChannel : blackScrimChannel).sendMessage(String.format(blackVodDay ?
                            REMINDER_VOD_15_MINUTES : REMINDER_SCRIM_15_MINUTES, jda.getRoleById(blackTeamRole).getAsMention())).queue();
                else
                    blackEventToday = true; // reset for next week
            }
        }
    }

    /**
     * @return the new value of yellowEventToday. True if yellow events are NOT skipped for today, false if they are.
     */
    public static boolean toggleYellowEventToday()
    {
        return yellowEventToday = !yellowEventToday;
    }

    /**
     * @return the new value of blackEventToday. True if black events are NOT skipped for today, false if they are.
     */
    public static boolean toggleBlackEventToday()
    {
        return blackEventToday = !blackEventToday;
    }
}

package com.nullXer0.beebot.commands;

import com.nullXer0.beebot.BeeBot;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.quartz.JobKey;
import org.quartz.SchedulerException;

public class TriggerJobCommand extends BaseSlashCommand
{
    public TriggerJobCommand()
    {
        name = "triggerjob";
        description = "Trigger a job manually";
        permissions = DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR);
        options = new OptionData[]{
                new OptionData(OptionType.STRING, "job_key", "The key of the job to trigger", true)
        };
    }

    @Override
    protected void run(SlashCommandInteractionEvent event)
    {
        String jobKey = event.getOption("job_key", OptionMapping::getAsString);

        // Trigger the job with the given key
        try
        {
            BeeBot.getScheduler().triggerJob(JobKey.jobKey(jobKey));
            event.reply("Job with key `" + jobKey + "` has been triggered successfully.").setEphemeral(true).queue();
        }
        catch(SchedulerException e)
        {
            event.reply("Failed to trigger job with key `" + jobKey + "`. Please check if the job exists.").setEphemeral(true).queue();
            System.err.println("Error triggering job: " + e);
        }
    }
}

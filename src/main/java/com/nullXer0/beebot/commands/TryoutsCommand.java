package com.nullXer0.beebot.commands;

import com.nullXer0.beebot.BeeBot;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigValueFactory;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class TryoutsCommand extends BaseSlashCommand
{
    private static final Logger LOGGER = LoggerFactory.getLogger(TryoutsCommand.class);

    public TryoutsCommand()
    {
        name = "tryouts";
        description = "Set the state of tryouts for the team";
        options = new OptionData[]{
                new OptionData(OptionType.STRING, "team", "The team to view/set tryouts for", true)
                        .addChoice("Yellow", "yellow")
                        .addChoice("Black", "black"),
                new OptionData(OptionType.STRING, "state", "Open or close tryouts for the team", false)
                        .addChoice("Open", "open")
                        .addChoice("Close", "close")
        };
    }

    @Override
    protected void run(SlashCommandInteractionEvent event)
    {
        Config config = BeeBot.getConfig();

        String team = event.getOption("team", OptionMapping::getAsString);

        if(event.getOption("state") == null)
        {
            boolean tryoutsOpen = config.getBoolean("tryouts." + team);
            event.reply("Team " + team + " tryouts are currently " + (tryoutsOpen ? "open" : "closed") + ".").setEphemeral(true).queue();
        }
        else
        {
            String state = event.getOption("state", OptionMapping::getAsString);

            Config newConfig = config
                    .withValue("tryouts." + team, ConfigValueFactory.fromAnyRef(state.equals("open")));
            try
            {
                BeeBot.saveConfig(newConfig);

                // Reply to the user
                event.reply(String.format("Tryouts for team %s are now set to %s.", team, state)).queue();
            }
            catch(IOException e)
            {
                LOGGER.error("Failed to save tryouts state for team {}: {}", team, e.getMessage());
                event.reply("Failed to set tryouts state. Please try again later.").setEphemeral(true).queue();
            }
        }
    }
}

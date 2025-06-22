package com.nullXer0.beebot;

import com.nullXer0.beebot.commands.CommandHandler;
import com.nullXer0.beebot.commands.RollCallCommand;
import com.nullXer0.beebot.listeners.CommandListener;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class BeeBot
{
    private static Config config;
    private static final Logger logger = LoggerFactory.getLogger(BeeBot.class);
    private static JDA jda;
    private static CommandHandler commandHandler;

    public static void main(String[] ignoredArgs) throws InterruptedException, IOException
    {
        config = loadConfig();

        JDABuilder builder = JDABuilder.createDefault(config.getString("token"));

        // Add Commands
        commandHandler = new CommandHandler();
        commandHandler.registerCommand(new RollCallCommand());

        // Register listeners
        CommandListener commandListener = new CommandListener(commandHandler);
        builder.addEventListeners(commandListener);

        // Build bot
        jda = builder.build();

        // Wait for ready
        jda.awaitReady();
        logger.info("Bot is ready.");
        logger.info("Bot name: {}", jda.getSelfUser().getName());
        commandHandler.updateCommands();
    }

    public static JDA getJDA()
    {
        return jda;
    }

    public static CommandHandler getCommandHandler()
    {
        return commandHandler;
    }

    public static Config getConfig()
    {
        return config;
    }

    private static Config loadConfig() throws IOException
    {
        File configFile = new File("./application.conf");
        if(!configFile.exists())
        {
            Files.copy(BeeBot.class.getResourceAsStream("/application.conf"), configFile.toPath());

        }
        return ConfigFactory.parseFile(configFile);
    }
}

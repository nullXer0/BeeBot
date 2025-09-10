package com.nullXer0.beebot;

import com.nullXer0.dbee.tables.*;

import com.nullXer0.beebot.commands.*;
import com.nullXer0.beebot.database.Database;
import com.nullXer0.beebot.listeners.CommandListener;
import com.nullXer0.beebot.scheduling.reminders.EventReminders;
import com.nullXer0.beebot.scheduling.rollcall.BlackRollCallJob;
import com.nullXer0.beebot.scheduling.rollcall.YellowRollCallJob;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BeeBot
{
    private static final File CONFIG_FILE = new File("./application.conf");
    private static Config config;
    private static final Logger logger = LoggerFactory.getLogger(BeeBot.class);
    private static JDA jda;
    private static CommandHandler commandHandler;
    private static Scheduler scheduler;

    public static void main(String[] ignoredArgs) throws InterruptedException, IOException, SchedulerException, SQLException
    {
        config = loadConfig();

        //TODO: Remove test code
        Database.initialize(config.getString("database.jdbcUrl"), config.getString("database.username"), config.getString("database.password"));
        Connection connection = Database.getConnection();
        DSLContext context = DSL.using(connection, SQLDialect.POSTGRES);
        Result<Record> result = context.select().from(Teams.TEAMS).fetch();
        for(Record record : result)
        {
            String teamName = record.get(Teams.TEAMS.NAME);
            logger.info("Found team: {}", teamName);
        }

        JDABuilder builder = JDABuilder.createDefault(config.getString("token"));

        // Add Commands
        commandHandler = new CommandHandler(
                new RollCallCommand(),
                new TriggerJobCommand(),
                new TryoutsCommand(),
                new ToggleEventCommand());

        // Register listeners
        CommandListener commandListener = new CommandListener(commandHandler);
        builder.addEventListeners(commandListener);

        // Build bot
        jda = builder.build();

        // Wait for ready
        jda.awaitReady();
        logger.info("Bot is ready");
        logger.info("Bot name: {}", jda.getSelfUser().getName());
        commandHandler.updateCommands();

        // Initialize the scheduler
        scheduler = new StdSchedulerFactory().getScheduler();

        // Register jobs
        new YellowRollCallJob().addToScheduler(scheduler);
        new BlackRollCallJob().addToScheduler(scheduler);
        new EventReminders().addToScheduler(scheduler);

        // Start the scheduler
        scheduler.start();
    }

    public static JDA getJDA()
    {
        return jda;
    }

    public static CommandHandler getCommandHandler()
    {
        return commandHandler;
    }

    public static Scheduler getScheduler()
    {
        return scheduler;
    }

    public static Config getConfig()
    {
        return config;
    }

    private static Config loadConfig() throws IOException
    {
        if(!CONFIG_FILE.exists())
        {
            Files.copy(BeeBot.class.getResourceAsStream("/application.conf"), CONFIG_FILE.toPath());
        }
        return ConfigFactory.parseFile(CONFIG_FILE);
    }

    public static void saveConfig(Config config) throws IOException
    {
        String configString = config.root().render(ConfigRenderOptions.defaults().setOriginComments(false));
        Files.write(CONFIG_FILE.toPath(), configString.getBytes());
        BeeBot.config = config;
        logger.info("Configuration saved to {}", CONFIG_FILE.getAbsolutePath());
    }
}
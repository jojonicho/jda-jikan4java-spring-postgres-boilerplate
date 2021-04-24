package com.jojonicho.examplebot;

import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.examples.command.*;

import java.awt.Color;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.jojonicho.examplebot.commands.BobCommand;
import com.jojonicho.examplebot.commands.CatCommand;
import com.jojonicho.examplebot.commands.ChooseCommand;
import com.jojonicho.examplebot.commands.HelloCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ExampleBot {

    private static String TOKEN;

    private static String OWNER_ID;

    @Autowired
    public ExampleBot(@Value("${discord.token}") String token,
                      @Value("${discord.owner_id}") String ownerId) {
        TOKEN = token;
        OWNER_ID = ownerId;

//        // config.txt contains two lines (alternative way)
//        List<String> list = Files.readAllLines(Paths.get("config.txt"));
//
//        // the first is the bot token
//        TOKEN = list.get(0);
//
//        // the second is the bot's owner's id
//        OWNER_ID = list.get(1);
    }

    public static void main(String[] args) throws IllegalArgumentException {
        SpringApplication app = new SpringApplication(ExampleBot.class);
        app.run();
    }

    @PostConstruct
    public void run() throws IOException, LoginException,
            IllegalArgumentException, RateLimitedException {

        // define an eventwaiter, dont forget to add this to the JDABuilder!
        EventWaiter waiter = new EventWaiter();
        // define a command client
        CommandClientBuilder client = new CommandClientBuilder();
        // The default is "Type -help" (or whatver prefix you set)
        client.useDefaultGame();
        // sets the owner of the bot
        client.setOwnerId(OWNER_ID);
        // sets emojis used throughout the bot on successes, warnings, and failures
        client.setEmojis("\uD83D\uDE03", "\uD83D\uDE2E", "\uD83D\uDE26");
        // sets the bot prefix
        client.setPrefix("-");

        // adds commands
        client.addCommands(
                // command to show information about the bot
                new AboutCommand(Color.BLUE, "an example bot",
                        new String[]{"Cool commands","Nice examples","Lots of fun!"},
                        Permission.ADMINISTRATOR),
                // command to show a random cat
                new CatCommand(),
                // command to make a random choice
                new ChooseCommand(),
                // command to say hello
                new HelloCommand(waiter),
                // command to check bot latency
                new PingCommand(),
                new BobCommand(waiter),
                // command to shut off the bot
                new ShutdownCommand()
        );


        // start getting a bot account set up
        JDABuilder.createDefault(TOKEN)
                // set the game for when the bot is loading
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                .setActivity(Activity.playing("loading..."))
                // add the listeners
                .addEventListeners(waiter, client.build())
                // start it up!
                .build();
    }
}

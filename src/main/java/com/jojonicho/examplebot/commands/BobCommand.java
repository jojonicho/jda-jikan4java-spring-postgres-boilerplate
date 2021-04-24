package com.jojonicho.examplebot.commands;

import com.github.doomsdayrs.jikan4java.core.Connector;
import com.github.doomsdayrs.jikan4java.data.model.main.season.SeasonSearch;
import com.github.doomsdayrs.jikan4java.data.model.main.season.SeasonSearchAnime;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Value;

import java.awt.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class BobCommand extends Command {

    private final EventWaiter waiter;

    public BobCommand(EventWaiter waiter)
    {
        this.name = "bob";
        this.help = "shows a random bob";
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        this.guildOnly = false;
        this.waiter = waiter;
    }

    @SneakyThrows
    @Override
    protected void execute(CommandEvent event) {
//        waiter.waitForEvent(MessageReceivedEvent.class,
//                // make sure it's by the same user, and in the same channel, and for safety, a different message
//                e -> e.getAuthor().equals(event.getAuthor())
//                        && e.getChannel().equals(event.getChannel())
//                        && !e.getMessage().equals(event.getMessage()),
//                // respond, inserting the name they listed into the response
//                e -> event.reply("Hello, `"+e.getMessage().getContentRaw()+"`! I'm `"+e.getJDA().getSelfUser().getName()+"`!"),
////                e -> setAuthorizationCode(e.getMessage().getContentRaw()),
//                // if the user takes more than a minute, time out
//                1, TimeUnit.MINUTES, () -> event.reply("Sorry, you took too long."));


        CompletableFuture<SeasonSearch> seasonSearchCompletableFuture = new Connector().seasonLater();
        SeasonSearch seasonSearch = seasonSearchCompletableFuture.get();
        List<SeasonSearchAnime> animes = seasonSearch.getAnimes().subList(0, 5);
        for(SeasonSearchAnime anime: animes) {
            event.reply(new EmbedBuilder()
                    .setColor(event.isFromType(ChannelType.TEXT) ? event.getSelfMember().getColor() : Color.GREEN)
                    .setDescription(anime.getSynopsis())
                    .setImage(anime.getImageURL())
                    .build());
        }

    }
}

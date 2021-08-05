package com.KingFightStudios.KFS_Bot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.user.GenericUserEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.swing.*;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Bot extends ListenerAdapter {
    public static JDA bot;
    public static final RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
    public static void init() throws Exception{
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalTime now = LocalTime.now();
        File token = new File("src/main//java/com/KingFightStudios/KFS_Bot/TOKEN.txt");
        Scanner sc = new Scanner(token);
        bot = JDABuilder.createLight(sc.next(), Collections.emptyList())
                .addEventListeners(new Bot())
                .setActivity(Activity.playing("seit " + dtf.format(now) + " Uhr"))
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .build()
                .awaitReady();

        System.out.println("-----------------------------------------------------");
        System.out.println("|                                                   |");
        System.out.println("|              Started Bot as KFS#3110              |");
        System.out.println("|                   In Development                  |");
        System.out.println("|                    Version: 2.0                   |");
        System.out.println("|             Creator: KingFightStudios             |");
        System.out.println("|                   Zeit: "+dtf.format(now)+"                  |");
        System.out.println("|                                                   |");
        System.out.println("-----------------------------------------------------");

        bot.updateCommands();

        Guild guild = bot.getGuildById("705831662734540850");

        assert guild != null;
        guild.upsertCommand(new CommandData("uptime", "Frage die Onlinezeit des Bots ab."));
        guild.upsertCommand(new CommandData("ping", "Frage den Ping des Bots ab."));
        guild.upsertCommand(new CommandData("clear", "Lösche eine bestimmte Anzahl an Nachrichten.")
                .addOption(OptionType.INTEGER, "anzahl", "Die anzahl an zu löschenden Nachrichten", true)
                .setDefaultEnabled(false)
        ).flatMap(command -> command.updatePrivileges(guild, CommandPrivilege.enableRole("705831662734540857"))).queue();
        guild.upsertCommand(new CommandData("news", "Sende eine Nachricht in #news.")
                .addOption(OptionType.STRING, "nachricht", "Die Nachricht die gesendet werden soll.", true)
                .setDefaultEnabled(false)
        ).flatMap(command -> command.updatePrivileges(guild, CommandPrivilege.enableRole("705831662734540857"))).queue();
        guild.upsertCommand(new CommandData("abstimmung", "Erstelle eine Abstimmung.")
                .addOption(OptionType.STRING, "abstimmung", "Die Frage die gestellt werden soll.", true)
                .addOption(OptionType.INTEGER, "anzahl", "Die Anzahl an Antwortmöglichkeiten.", true)
                .setDefaultEnabled(false)
        ).flatMap(command -> command.updatePrivileges(guild, CommandPrivilege.enableRole("705831662734540857"))).queue();
        guild.upsertCommand(new CommandData("meeting", "Erstelle ein Meeting.")
                .addOption(OptionType.INTEGER, "zeit", "Die Uhrzeit des Meetings.", true)
                .setDefaultEnabled(false)
        ).flatMap(command -> command.updatePrivileges(guild, CommandPrivilege.enableRole("705831662734540857"))).queue();
        guild.upsertCommand(new CommandData("restart", "Starte den Bot neu.")
                .setDefaultEnabled(false)
        ).flatMap(command -> command.updatePrivileges(guild, CommandPrivilege.enableRole("705831662734540857"))).queue();
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        switch (event.getName()) {
            case "uptime":
                long uptimeInSeconds = getUptime() / 1000;
                long numberOfHours = uptimeInSeconds / (60 * 60);
                long numberOfMinutes = (uptimeInSeconds / 60) - (numberOfHours * 60);
                long numberOfSeconds = uptimeInSeconds % 60;
                event.replyFormat(
                        event.getGuild().getSelfMember().getEffectiveName() + " ist online seit %s:%s:%s",
                        numberOfHours, numberOfMinutes, numberOfSeconds
                ).setEphemeral(true).queue();
            case "ping":
                long time = System.currentTimeMillis();
                event.reply("Pong!").setEphemeral(true)
                        .flatMap(v ->
                                event.getHook().editOriginalFormat("Pong: %d ms", System.currentTimeMillis() - time)
                        ).queue();
            case "clear":
                MessageChannel channel = event.getChannel();
                int amount = (int)event.getOption("anzahl").getAsLong();
                List<Message> messages = event.getChannel().getHistory().retrievePast(amount).complete();
                channel.purgeMessages(messages);
                event.reply(amount + " Nachrichten gelöscht.").setEphemeral(true).queue();
            case "news":
                event.getGuild().getTextChannelsByName("news", true).get(0).sendMessage(event.getOption("nachricht").getAsString()).queue();
                event.reply("Nachricht `" + event.getOption("nachricht").getAsString() + "` in `#news` gesendet." ).setEphemeral(true).queue();
            case "abstimmung":
                event.reply("WIP").setEphemeral(true).queue();
            case "meeting":
                event.reply("WIP").setEphemeral(true).queue();
            case "restart":
                event.reply("Ne... lass ma").setEphemeral(true).queue();
            /*try {
                Runtime.getRuntime().exec("shutdown -r -t 1");
            } catch (Exception e) {
                e.printStackTrace();
            }*/
        }
    }

    @Override
    public void onReady(ReadyEvent event) {
        event.getJDA().getGuildById("705831662734540850").loadMembers().onSuccess(members -> {
            Main.gui.members(members);
        });
    }

    @Override
    public void onGenericUser(GenericUserEvent event) {
        event.getJDA().getGuildById("705831662734540850").loadMembers().onSuccess(members -> {
            Main.gui.members(members);
        });
    }

    public void shutdown() {
        bot.shutdown();
    }

    public long getUptime() {
        return runtimeMXBean.getUptime();
    }
}
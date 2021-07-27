package com.KingFightStudios.KFS_Bot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.user.GenericUserEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.swing.*;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

public class Bot extends ListenerAdapter {
    public static final RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
    public static void main(String[] args) throws Exception{
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalTime now = LocalTime.now();
        final JDA bot = JDABuilder.createLight("TOKEN", Collections.emptyList())
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
        ).flatMap(command -> command.updatePrivileges(guild, CommandPrivilege.enableRole("705831662734540857"))).queue();;
        guild.upsertCommand(new CommandData("abstimmung", "Erstelle eine Abstimmung.")
                .addOption(OptionType.STRING, "abstimmung", "Die Frage die gestellt werden soll.", true)
                .addOption(OptionType.INTEGER, "anzahl", "Die Anzahl an Antwortmöglichkeiten.", true)
                .setDefaultEnabled(false)
        ).flatMap(command -> command.updatePrivileges(guild, CommandPrivilege.enableRole("705831662734540857"))).queue();;
        guild.upsertCommand(new CommandData("meeting", "Erstelle ein Meeting.")
                .addOption(OptionType.INTEGER, "zeit", "Die Uhrzeit des Meetings.", true)
                .setDefaultEnabled(false)
        ).flatMap(command -> command.updatePrivileges(guild, CommandPrivilege.enableRole("705831662734540857"))).queue();;
        guild.upsertCommand(new CommandData("restart", "Starte den Bot neu.")
                .setDefaultEnabled(false)
        ).flatMap(command -> command.updatePrivileges(guild, CommandPrivilege.enableRole("705831662734540857"))).queue();;
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        if (event.getName().equals("uptime")) {
            long uptime = runtimeMXBean.getUptime();
            long uptimeInSeconds = uptime / 1000;
            long numberOfHours = uptimeInSeconds / (60 * 60);
            long numberOfMinutes = (uptimeInSeconds / 60) - (numberOfHours * 60);
            long numberOfSeconds = uptimeInSeconds % 60;

            event.replyFormat(
                     event.getGuild().getSelfMember().getNickname() + " ist online seit %s:%s:%s",
                    numberOfHours, numberOfMinutes, numberOfSeconds
            ).setEphemeral(true).queue();
        }
        else if (event.getName().equals("ping")) {
            long time = System.currentTimeMillis();
            event.reply("Pong!").setEphemeral(true)
                    .flatMap(v ->
                            event.getHook().editOriginalFormat("Pong: %d ms", System.currentTimeMillis() - time)
                    ).queue();
        } else if (event.getName().equals("clear")) {
            MessageChannel channel = event.getChannel();
            int amount = (int)event.getOption("anzahl").getAsLong();
            List<Message> messages = event.getChannel().getHistory().retrievePast(amount).complete();
            channel.purgeMessages(messages);
            event.reply(amount + " Nachrichten gelöscht.").setEphemeral(true).queue();
        } else if (event.getName().equals("news")) {
            event.reply("WIP").setEphemeral(true).queue();
        } else if (event.getName().equals("abstimmung")) {
            event.reply("WIP").setEphemeral(true).queue();
        } else if (event.getName().equals("meeting")) {
            event.reply("WIP").setEphemeral(true).queue();
        } else if (event.getName().equals("restart")) {
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
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    Main.gui.members(members);
                }
            });
        });
    }

    @Override
    public void onGenericUser(GenericUserEvent event) {
        event.getJDA().getGuildById("705831662734540850").loadMembers().onSuccess(members -> {
            Main.gui.members(members);
        });
    }
}
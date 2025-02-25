package com.casko1.wheelbarrow.bot.lib.handler;

import com.casko1.wheelbarrow.bot.lib.command.*;
import com.casko1.wheelbarrow.bot.lib.event.ContextMenuEvent;
import com.casko1.wheelbarrow.bot.lib.event.SlashCommandEvent;
import com.casko1.wheelbarrow.bot.lib.event.TextCommandEvent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class EventHandler extends ListenerAdapter {
    private final String prefix;
    private final int prefixLength;
    private final String developmentGuildId;
    private final OnlineStatus status;
    private final HashMap<String, TextCommand> textCommands;
    private final HashMap<String, SlashCommand> slashCommands;
    private final HashMap<String, ContextMenuCommand> contextMenuCommands;
    private final HashMap<String, HybridCommand> hybridCommands;

    private static final Logger logger = LoggerFactory.getLogger(EventHandler.class);

    public EventHandler(String prefix, String developmentGuildId, OnlineStatus status, HashMap<String, TextCommand> textCommands, HashMap<String, SlashCommand> slashCommands, HashMap<String, ContextMenuCommand> contextMenuCommands, HashMap<String, HybridCommand> hybridCommands) {
        this.prefix = prefix;
        prefixLength = prefix.length();
        this.developmentGuildId = developmentGuildId;
        this.status = status;
        this.textCommands = textCommands;
        this.slashCommands = slashCommands;
        this.contextMenuCommands = contextMenuCommands;
        this.hybridCommands = hybridCommands;
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        if (!event.getJDA().getSelfUser().isBot()) {
            logger.error("Wheelbarrow cannot be used as self-bot");
            event.getJDA().shutdown();
            return;
        }

        JDA jdaInstance = event.getJDA();

        List<CommandData> commandData = new ArrayList<>();

        for (TextCommand command : textCommands.values()) {
            logger.info("Registered text command: {}", command.getName());
        }

        for (HybridCommand command : hybridCommands.values()) {
            logger.info("Registered hybrid command: {}", command.getName());
            commandData.add(command.buildCommandData());
        }

        for (SlashCommand command : slashCommands.values()) {
            logger.info("Registered slash command: {}", command.getName());
            commandData.add(command.buildCommandData());
        }

        for (ContextMenuCommand command : contextMenuCommands.values()) {
            logger.info("Registered context menu command: {}", command.getName());
            commandData.add(command.buildCommandData());
        }

        if (!developmentGuildId.equals("")) {
            logger.info("Development server detected, propagating commands to {}", developmentGuildId);
            Guild server = jdaInstance.getGuildById(developmentGuildId);

            if (server != null) {
                server.updateCommands().addCommands(commandData).queue();
            }
        } else {
            logger.info("No development server detected, propagating commands to all servers");
            jdaInstance.updateCommands().addCommands(commandData).queue();
        }

        jdaInstance.getPresence().setPresence(this.status, Activity.playing(String.format("%s is my prefix", prefix)));
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        String commandName = event.getName();
        SlashCommand command = slashCommands.get(commandName);
        HybridCommand hybridSlashCommand = hybridCommands.get(commandName);

        if (command != null || hybridSlashCommand != null) {
            SlashCommandEvent slashCommandEvent = new SlashCommandEvent(event);

            (command != null ? command : hybridSlashCommand).execute(slashCommandEvent);
        }
    }

    @Override
    public void onMessageContextInteraction(@NotNull MessageContextInteractionEvent event) {
        ContextMenuCommand command = contextMenuCommands.get(event.getName());

        if (command != null) {
            command.execute(new ContextMenuEvent(event));
        }
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        Message message = event.getMessage();
        String content = message.getContentRaw();
        String[] split = content.split(" ");

        if (split.length > 0 && split[0].startsWith(prefix) && split[0].length() > prefixLength) {
            String commandName = split[0].substring(prefixLength);
            TextCommand command = textCommands.get(commandName);
            HybridCommand hybridTextCommand = hybridCommands.get(commandName);

            if (command != null || hybridTextCommand != null) {
                String[] args = split.length > 1 ? Arrays.copyOfRange(split, 1, split.length) : new String[]{};
                TextCommandEvent textCommandEvent = new TextCommandEvent(event, args);

                (command != null ? command : hybridTextCommand).execute(textCommandEvent);
            }
        }
    }

    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        String commandName = event.getName();
        SlashCommand command = slashCommands.get(commandName);
        HybridCommand hybridSlashCommand = hybridCommands.get(commandName);

        if (command != null || hybridSlashCommand != null) {
            (command != null ? command : hybridSlashCommand).onAutoComplete(event);
        }
    }
}

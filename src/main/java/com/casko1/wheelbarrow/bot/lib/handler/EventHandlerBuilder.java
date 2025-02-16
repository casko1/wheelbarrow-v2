package com.casko1.wheelbarrow.bot.lib.handler;

import com.casko1.wheelbarrow.bot.lib.command.ContextMenuCommand;
import com.casko1.wheelbarrow.bot.lib.command.HybridCommand;
import com.casko1.wheelbarrow.bot.lib.command.SlashCommand;
import com.casko1.wheelbarrow.bot.lib.command.TextCommand;
import net.dv8tion.jda.api.OnlineStatus;

import java.util.HashMap;

public class EventHandlerBuilder {
    private String prefix = "";
    private String developmentGuildId = "";
    private OnlineStatus status = OnlineStatus.ONLINE;
    private final HashMap<String, TextCommand> textCommands = new HashMap<>();
    private final HashMap<String, SlashCommand> slashCommands = new HashMap<>();
    private final HashMap<String, ContextMenuCommand> contextMenuCommands = new HashMap<>();
    private final HashMap<String, HybridCommand> hybridCommands = new HashMap<>();


    public EventHandlerBuilder setPrefix(String prefix) {
        this.prefix = prefix;

        return this;
    }

    public EventHandlerBuilder setDevelopmentGuildId(String guildId) {
        if (guildId != null) {
            this.developmentGuildId = guildId;
        }

        return this;
    }

    public EventHandlerBuilder addTextCommands(TextCommand... commands) {
        for (TextCommand command : commands) {
            textCommands.put(command.getName(), command);
        }

        return this;
    }

    public EventHandlerBuilder addSlashCommands(SlashCommand... commands) {
        for (SlashCommand command : commands) {
            slashCommands.put(command.getName(), command);
        }

        return this;
    }

    public EventHandlerBuilder addContextMenuCommands(ContextMenuCommand... commands) {
        for (ContextMenuCommand command : commands) {
            contextMenuCommands.put(command.getName(), command);
        }

        return this;
    }

    public EventHandlerBuilder addHybridCommands(HybridCommand... commands) {
        for (HybridCommand command : commands) {
            hybridCommands.put(command.getName(), command);
        }

        return this;
    }

    public EventHandler build() throws Exception {
        if (prefix.equals("")) {
            throw new Exception("Prefix must not be empty");
        } else {
            return new EventHandler(prefix, developmentGuildId, status, textCommands, slashCommands, contextMenuCommands, hybridCommands);
        }
    }
}

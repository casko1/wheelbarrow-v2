package com.casko1.wheelbarrow.bot.music;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.menu.Menu;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class QueuePaginator extends Menu {

    private final int numberOfPages;
    private final int itemsPerPage;
    private final List<String> items;
    private final Consumer<Message> finalAction;
    private final String currentTrack;
    private final Color color;

    public static final String LEFT = "\u25C0";
    public static final String STOP = "\u23F9";
    public static final String RIGHT = "\u25B6";

    QueuePaginator(EventWaiter waiter, Set<User> users, Set<Role> roles, long timeout,
                             TimeUnit unit, int itemsPerPage, List<String> items, Consumer<Message> finalAction,
                             String currentTrack, Color color) {
        super(waiter, users, roles, timeout, unit);
        this.itemsPerPage = itemsPerPage;
        this.items = items;
        this.finalAction = finalAction;
        numberOfPages = (int) Math.ceil((double) items.size() / itemsPerPage);
        this.currentTrack = currentTrack;
        this.color = color;
    }

    @Override
    public void display(MessageChannel channel) {
        paginate(channel, 1);
    }

    @Override
    public void display(Message message) {
        paginate(message, 1);
    }

    //for new message
    public void paginate(MessageChannel channel, int pNum){
        MessageCreateData message = createMessage(Math.min(Math.max(1, pNum), numberOfPages));
        init(channel.sendMessage(message), pNum);
    }

    //for editing existing message
    public void paginate(Message message, int pNum){
        MessageEditData msg = updateMessage(Math.min(Math.max(1, pNum), numberOfPages));
        init(message.editMessage(msg), pNum);
    }


    public void init(RestAction<Message> action, int pNum){
        if(items.size() > 10){
            action.queue(m -> {
                m.addReaction(Emoji.fromUnicode(LEFT)).queue();
                m.addReaction(Emoji.fromUnicode(STOP)).queue();
                m.addReaction(Emoji.fromUnicode(RIGHT))
                        .queue(v -> handlePagination(m, pNum),
                                t -> handlePagination(m, pNum));
            });
        }
        else{
            action.queue(m -> {
                m.addReaction(Emoji.fromUnicode(STOP)).queue(v -> handlePagination(m, pNum),
                        t -> handlePagination(m, pNum));
            });
        }
    }

    private void handlePagination(Message message, int pNum){
        waiter.waitForEvent(MessageReactionAddEvent.class,
                event -> checkReaction(event, message.getIdLong()),
                event -> handleReaction(event, message, pNum),
                timeout, unit, () -> finalAction.accept(message));
    }

    private boolean checkReaction(MessageReactionAddEvent event, long messageId){

        if(event.getMessageIdLong() != messageId) return false;

        return switch (event.getReaction().getEmoji().getName()) {
            case LEFT, STOP, RIGHT -> isValidUser(event.getUser(), event.isFromGuild() ? event.getGuild() : null);
            default -> false;
        };
    }

    private void handleReaction(MessageReactionAddEvent event, Message message, int pNum){
        int newPageNum = pNum;

        switch(event.getReaction().getEmoji().getName()){
            case LEFT:
                if(newPageNum > 1) newPageNum--;
                break;
            case RIGHT:
                if(newPageNum < numberOfPages) newPageNum++;
                break;
            case STOP:
                finalAction.accept(message);
                return;
        }

        try{
            event.getReaction().removeReaction(event.getUser()).queue();
        } catch (PermissionException ignored) {}

        int n = newPageNum;
        message.editMessage(updateMessage(newPageNum)).queue(m -> handlePagination(m, n));
    }


    private MessageEditData updateMessage(int pNum) {
        EmbedBuilder eb = new EmbedBuilder();

        MessageEditBuilder mb = new MessageEditBuilder();

        int start = Math.max(0, (pNum - 1) * itemsPerPage);
        int end = Math.min(items.size(), pNum * itemsPerPage);

        StringBuilder sb = new StringBuilder();

        for(int i = start; i < end; i++){
            sb.append(String.format("%d. %s\n", i+1, items.get(i)));
        }

        eb.addField("Currently playing:", currentTrack, false);
        eb.addField("**Tracks in queue:**", sb.toString(), false);
        eb.setFooter(String.format("%s \n %s %d/%d",
                "Use $$remove <number> to remove song from queue",
                "Page", pNum, numberOfPages));

        eb.setColor(color);

        mb.setEmbeds(eb.build());

        return mb.build();
    }

    private MessageCreateData createMessage(int pNum) {

        EmbedBuilder eb = new EmbedBuilder();

        MessageCreateBuilder mb = new MessageCreateBuilder();

        int start = Math.max(0, (pNum - 1) * itemsPerPage);
        int end = Math.min(items.size(), pNum * itemsPerPage);

        StringBuilder sb = new StringBuilder();

        for(int i = start; i < end; i++){
            sb.append(String.format("%d. %s\n", i+1, items.get(i)));
        }

        eb.addField("Currently playing:", currentTrack, false);
        eb.addField("**Tracks in queue:**", sb.toString(), false);
        eb.setFooter(String.format("%s \n %s %d/%d",
                "Use $$remove <number> to remove song from queue",
                "Page", pNum, numberOfPages));

        eb.setColor(color);

        mb.setEmbeds(eb.build());

        return mb.build();
    }


    public static class Builder extends Menu.Builder<Builder, QueuePaginator>{

        private String currentTrack;
        private final Consumer<Message> finalAction = m -> m.delete().queue();
        private int itemsPerPage = 10;
        private final List<String> items = new ArrayList<>();
        private Color color = Color.BLUE;

        @Override
        public QueuePaginator build() {
            return new QueuePaginator(waiter, users, roles, timeout, unit,
                    itemsPerPage, items, finalAction, currentTrack, color);
        }

        public Builder setCurrentTrack(String track){
            currentTrack = track;
            return this;
        }

        public Builder setItemsPerPage(int count){
            itemsPerPage = count;
            return this;
        }

        public Builder clearItems(){
            items.clear();
            return this;
        }

        public Builder setItems(List<String> tracks){
            items.clear();
            items.addAll(tracks);
            return this;
        }

        public Builder setColor(Color color){
            this.color = color;
            return this;
        }
    }
}

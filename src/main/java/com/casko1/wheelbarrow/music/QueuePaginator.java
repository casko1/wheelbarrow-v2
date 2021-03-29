package com.casko1.wheelbarrow.music;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.menu.Menu;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public class QueuePaginator extends Menu {

    protected QueuePaginator(EventWaiter waiter, Set<User> users, Set<Role> roles, long timeout, TimeUnit unit) {
        super(waiter, users, roles, timeout, unit);
    }

    @Override
    public void display(MessageChannel channel) {

    }

    @Override
    public void display(Message message) {

    }
}

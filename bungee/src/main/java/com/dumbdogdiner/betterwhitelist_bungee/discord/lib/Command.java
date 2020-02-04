package com.dumbdogdiner.betterwhitelist_bungee.discord.lib;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user-runnable command.
 */
public abstract class Command {
    protected String name;

    public String getName() {
        return this.name;
    }

    private List<CommandInhibitor> inhibitors = new ArrayList<>();
    public List<CommandInhibitor> getInibitors() {
        return inhibitors;
    }

    public abstract boolean run(MessageReceivedEvent e, String... args);

    /**
     * Trigger a command to be run.
     * @param e
     * @return
     */
    public boolean execute(MessageReceivedEvent e, String... args) {
        for (var inhibitor : inhibitors) {
            if (!inhibitor.inhibit(this, e)) {
                return true;
            }
        }

        return this.run(e, args);
    }


    /**
     * Add inhibitors to the command.
     * @param inhibitorsToAdd
     */
    protected void useInhibitor(CommandInhibitor... inhibitorsToAdd) {
        for (CommandInhibitor inhibitor : inhibitorsToAdd) {
            inhibitors.add(inhibitor);
        }
    }
}

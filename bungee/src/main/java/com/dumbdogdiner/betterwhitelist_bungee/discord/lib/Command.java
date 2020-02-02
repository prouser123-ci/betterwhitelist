package com.dumbdogdiner.betterwhitelist_bungee.discord.lib;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user-runnable command.
 */
public abstract  class Command {
    private static String name = "Command";

    private List<CommandInhibitor> inhibitors = new ArrayList<>();

    public abstract boolean run(Context ctx, String... args);

    /**
     * Trigger a command to be run.
     * @param ctx
     * @return
     */
    public boolean execute(Context ctx, String... args) {
        for (var inhibitor : inhibitors) {
            if (!inhibitor.inhibit(this, ctx)) {
                return true;
            }
        }

        return this.run(ctx, args);
    }

    /**
     * Fetch the name of the command.
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Add inhibitors to the command.
     * @param inhibitors
     */
    protected void useInhibitor(CommandInhibitor... inhibitors) {
        for (CommandInhibitor inhibitor : inhibitors) {
            this.inhibitors.add(inhibitor);
        }
    }
}

package kokumaji.betterwhitelist.commands;

import kokumaji.betterwhitelist.BetterWhitelist;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BetterWhitelistCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            if (args.length == 0) {
                return false;
            } else if (args[0].equals("help")) {
                sender.sendMessage();
            } else if (args[0].equals("reload")) {
                sender.sendMessage(ChatColor.GREEN + "Reloading plugin config.");
                BetterWhitelist.getPlugin().reloadConfig();
            }
        }

        return true;
    }
}

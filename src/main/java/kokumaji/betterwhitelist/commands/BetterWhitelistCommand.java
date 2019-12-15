package kokumaji.betterwhitelist.commands;

import kokumaji.betterwhitelist.BetterWhitelist;
import lombok.SneakyThrows;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class BetterWhitelistCommand implements CommandExecutor {

    FileConfiguration plConf = BetterWhitelist.getPlugin().getConfig();

    @SneakyThrows
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            if (args.length == 0) {
                return false;
            } else if (args[0].equals("help")) {
                sender.sendMessage();
            } else if (args[0].equals("reload")) {

                BetterWhitelist.getPlugin().reloadConfig();
                sender.sendMessage(ChatColor.GREEN + "Config reloaded!");
            }
        }

        return true;
    }
}

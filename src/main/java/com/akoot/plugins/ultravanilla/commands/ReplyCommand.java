package com.akoot.plugins.ultravanilla.commands;

import com.akoot.plugins.ultravanilla.UltraVanilla;
import com.akoot.plugins.ultravanilla.reference.Users;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ReplyCommand extends UltraCommand implements CommandExecutor, TabExecutor {

    public static final ChatColor COLOR = ChatColor.WHITE;

    public ReplyCommand(UltraVanilla instance) {
        super(instance);
        this.color = COLOR;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (Users.REPLIES.containsKey(sender.getName())) {
            String to = Users.REPLIES.get(sender.getName());
            if (to.equalsIgnoreCase("console")) {
                MsgCommand.msg(sender, Bukkit.getConsoleSender(), getArg(args));
            } else {
                Player player = plugin.getServer().getPlayer(to);
                if (player == null) {
                    sender.sendMessage(plugin.getString("player-offline", "{player}", to));
                } else {
                    MsgCommand.msg(sender, player, getArg(args));
                }
            }
        } else {
            sender.sendMessage(format(command, "error.no-replies"));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }
}

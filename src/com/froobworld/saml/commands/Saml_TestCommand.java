package com.froobworld.saml.commands;

import com.froobworld.saml.Saml;
import joptsimple.internal.Strings;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class Saml_TestCommand implements CommandExecutor {
    private Saml saml;

    public Saml_TestCommand(Saml saml) {
        this.saml = saml;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String cl, String[] args) {
        if(args.length < 2) {
            sender.sendMessage("/" + cl + " test <group definition>");
            return false;
        }
        String groupDefinition = Strings.join(Arrays.copyOfRange(args, 1, args.length), " ");
        try {
            if(saml.getGroupStore().getGroup(groupDefinition, true) == null) {
                sender.sendMessage(ChatColor.RED + "That group does not seem to exist.");
            } else {
                sender.sendMessage(ChatColor.YELLOW + "Everything seems to be in order.");
            }
        } catch (Exception e) {
            if(sender instanceof Player) {
                sender.sendMessage(ChatColor.RED + "There was an exception when trying to retrieve that group. See the console for more information.");
            } else {
                sender.sendMessage(ChatColor.RED + "An issue occured when trying to retrieve that group:");
            }
            e.printStackTrace();
        }
        return true;
    }
}

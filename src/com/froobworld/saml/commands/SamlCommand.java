package com.froobworld.saml.commands;

import com.froobworld.saml.Saml;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class SamlCommand implements CommandExecutor {
    private Saml saml;

    public SamlCommand(Saml saml) {
        this.saml = saml;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String cl, String[] args) {
        if(!sender.hasPermission("saml.saml")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return false;
        }
        if(args.length > 0) {
            if(args[0].equalsIgnoreCase("reload")) {
                return new Saml_ReloadCommand(saml).onCommand(sender, command, cl, args);
            }
        }
        sender.sendMessage(ChatColor.YELLOW + "SAML " + saml.getDescription().getVersion());
        sender.sendMessage("/" + cl + " reload - Reload SAML configuration.");
        return true;
    }

    public static TabCompleter tabCompleter = new TabCompleter() {
        @Override
        public List<String> onTabComplete(CommandSender sender, Command command, String cl, String[] args) {
            List<String> completions = new ArrayList<String>();
            if(args.length == 1) {
                completions.add("reload");
            }

            return StringUtil.copyPartialMatches(args[args.length - 1], completions, new ArrayList<String>());
        }
    };
}

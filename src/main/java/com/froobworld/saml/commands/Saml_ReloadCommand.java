package com.froobworld.saml.commands;

import com.froobworld.saml.Saml;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Saml_ReloadCommand implements CommandExecutor {
    private Saml saml;

    public Saml_ReloadCommand(Saml saml) {
       this.saml = saml;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String cl, String[] args) {
        saml.reloadSamlConfiguration();
        sender.sendMessage(ChatColor.YELLOW + "Configuration successfully reloaded.");
        return true;
    }
}

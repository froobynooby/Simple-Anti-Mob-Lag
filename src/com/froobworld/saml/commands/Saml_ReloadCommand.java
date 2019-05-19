package com.froobworld.saml.commands;

import com.froobworld.saml.Config;
import com.froobworld.saml.Saml;
import com.froobworld.saml.Messages;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Saml_ReloadCommand implements CommandExecutor {
    private Config config;
    private Messages messages;

    public Saml_ReloadCommand(Saml saml) {
        this.config = saml.getLwalConfig();
        this.messages = saml.getLwalMessages();
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String cl, String[] args) {
        config.loadFromFile();
        messages.loadFromFile();
        sender.sendMessage(ChatColor.YELLOW + "Configuration successfully reloaded.");
        return true;
    }
}

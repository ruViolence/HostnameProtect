package ru.violence.hostnameprotect.bungee.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import ru.violence.hostnameprotect.bungee.HostnameProtectBungee;

public class HostnameProtectCommand extends Command {
    private final HostnameProtectBungee plugin;

    public HostnameProtectCommand(HostnameProtectBungee plugin) {
        super("hostnameprotect", "hostnameprotect.command.use");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        plugin.reloadConfig();
        commandSender.sendMessage(new TextComponent("Config reloaded"));
    }
}

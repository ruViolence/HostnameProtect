package ru.violence.hostnameprotect.velocity.command;

import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.text.Component;
import ru.violence.hostnameprotect.velocity.HostnameProtectVelocity;

public class HostnameProtectCommand implements SimpleCommand {
    private final HostnameProtectVelocity plugin;

    public HostnameProtectCommand(HostnameProtectVelocity plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Invocation invocation) {
        plugin.reloadConfig();
        invocation.source().sendMessage(Component.text("Config reloaded"));
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("hostnameprotect.command.use");
    }
}

package ru.violence.hostnameprotect.bungee.listener;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import ru.violence.hostnameprotect.bungee.HostnameProtectBungee;

public class LoginListener implements Listener {
    private final HostnameProtectBungee plugin;

    public LoginListener(HostnameProtectBungee plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPreLogin(PreLoginEvent event) {
        if (event.isCancelled()) return;

        String playerName = event.getConnection().getName();
        String hostname = event.getConnection().getVirtualHost().getHostName().toLowerCase();

        String special = plugin.getSpecialHostnames().get(playerName.toLowerCase());
        if (special != null) {
            if (!special.equals(hostname)) {
                event.setCancelled(true);
                event.setCancelReason(TextComponent.fromLegacyText(plugin.getKickMessageSpecial()));
                plugin.getLogger().warning(playerName + " tried to log in not through the special hostname \"" + hostname + "\"");
            }
            return;
        }

        if (!plugin.getGlobalHostnames().contains(hostname)) {
            event.setCancelled(true);
            event.setCancelReason(TextComponent.fromLegacyText(plugin.getKickMessageGlobal()));
            plugin.getLogger().warning(playerName + " tried to log in through the third-party hostname \"" + hostname + "\"");
        }
    }
}

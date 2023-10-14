package ru.violence.hostnameprotect.bungee.listener;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import ru.violence.hostnameprotect.bungee.HostnameProtectBungee;

import java.util.Map;
import java.util.Set;

public class LoginListener implements Listener {
    private final HostnameProtectBungee plugin;
    private final String kickMessageGlobal;
    private final String kickMessageSpecial;
    private final Set<String> globalHostnames;
    private final Map<String, String> specialHostnames;

    public LoginListener(HostnameProtectBungee plugin, String kickMessageGlobal, String kickMessageSpecial, Set<String> globalHostnames, Map<String, String> specialHostnames) {
        this.plugin = plugin;
        this.kickMessageGlobal = kickMessageGlobal;
        this.kickMessageSpecial = kickMessageSpecial;
        this.globalHostnames = globalHostnames;
        this.specialHostnames = specialHostnames;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPreLogin(PreLoginEvent event) {
        if (event.isCancelled()) return;

        String playerName = event.getConnection().getName();
        String hostname = event.getConnection().getVirtualHost().getHostName().toLowerCase();

        String special = specialHostnames.get(playerName.toLowerCase());
        if (special != null) {
            if (!special.equals(hostname)) {
                event.setCancelled(true);
                event.setCancelReason(TextComponent.fromLegacyText(kickMessageSpecial));
                plugin.getLogger().warning(playerName + " tried to log in not through the special hostname \"" + hostname + "\"");
            }
            return;
        }

        if (!globalHostnames.contains(hostname)) {
            event.setCancelled(true);
            event.setCancelReason(TextComponent.fromLegacyText(kickMessageGlobal));
            plugin.getLogger().warning(playerName + " tried to log in through the third-party hostname \"" + hostname + "\"");
        }
    }
}

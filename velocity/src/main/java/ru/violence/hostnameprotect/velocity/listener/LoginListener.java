package ru.violence.hostnameprotect.velocity.listener;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import ru.violence.hostnameprotect.velocity.HostnameProtectVelocity;

import java.util.Map;
import java.util.Set;

public class LoginListener {
    private final HostnameProtectVelocity plugin;
    private final String kickMessageGlobal;
    private final String kickMessageSpecial;
    private final Set<String> globalHostnames;
    private final Map<String, String> specialHostnames;

    public LoginListener(HostnameProtectVelocity plugin, String kickMessageGlobal, String kickMessageSpecial, Set<String> globalHostnames, Map<String, String> specialHostnames) {
        this.plugin = plugin;
        this.kickMessageGlobal = kickMessageGlobal;
        this.kickMessageSpecial = kickMessageSpecial;
        this.globalHostnames = globalHostnames;
        this.specialHostnames = specialHostnames;
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onPreLogin(PreLoginEvent event) {
        if (!event.getResult().isAllowed()) return;

        String playerName = event.getUsername();
        String hostname = event.getConnection().getRemoteAddress().getHostName().toLowerCase();

        String special = specialHostnames.get(playerName.toLowerCase());
        if (special != null) {
            if (!special.equals(hostname)) {
                event.setResult(PreLoginEvent.PreLoginComponentResult.denied(MiniMessage.miniMessage().deserialize(kickMessageSpecial)));
                plugin.getLogger().warn(playerName + " tried to log in not through the special hostname \"" + hostname + "\"");
            }
            return;
        }

        if (!globalHostnames.contains(hostname)) {
            event.setResult(PreLoginEvent.PreLoginComponentResult.denied(MiniMessage.miniMessage().deserialize(kickMessageGlobal)));
            plugin.getLogger().warn(playerName + " tried to log in through the third-party hostname \"" + hostname + "\"");
        }
    }
}

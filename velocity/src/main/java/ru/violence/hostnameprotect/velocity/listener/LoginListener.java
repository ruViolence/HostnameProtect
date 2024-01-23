package ru.violence.hostnameprotect.velocity.listener;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import ru.violence.hostnameprotect.velocity.HostnameProtectVelocity;

public class LoginListener {
    private final HostnameProtectVelocity plugin;

    public LoginListener(HostnameProtectVelocity plugin) {
        this.plugin = plugin;
    }

    @Subscribe(order = PostOrder.NORMAL)
    public void onPreLogin(PreLoginEvent event) {
        if (!event.getResult().isAllowed()) return;

        String playerName = event.getUsername();
        String hostname = event.getConnection().getRemoteAddress().getHostName().toLowerCase();

        String special = plugin.getSpecialHostnames().get(playerName.toLowerCase());
        if (special != null) {
            if (!special.equals(hostname)) {
                event.setResult(PreLoginEvent.PreLoginComponentResult.denied(MiniMessage.miniMessage().deserialize(plugin.getKickMessageSpecial())));
                plugin.getLogger().warn(playerName + " tried to log in not through the special hostname \"" + hostname + "\"");
            }
            return;
        }

        if (!plugin.getGlobalHostnames().contains(hostname)) {
            event.setResult(PreLoginEvent.PreLoginComponentResult.denied(MiniMessage.miniMessage().deserialize(plugin.getKickMessageGlobal())));
            plugin.getLogger().warn(playerName + " tried to log in through the third-party hostname \"" + hostname + "\"");
        }
    }
}

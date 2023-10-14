package ru.violence.hostnameprotect.bungee;

import lombok.SneakyThrows;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import ru.violence.hostnameprotect.bungee.listener.LoginListener;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HostnameProtectBungee extends Plugin {
    @SneakyThrows
    @Override
    public void onEnable() {
        extractDefaultConfig();
        Configuration config = YamlConfiguration.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));

        String kickMessageGlobal = config.getString("kick-message.global");
        String kickMessageSpecial = config.getString("kick-message.special");
        Set<String> globalHostnames = new HashSet<>(config.getStringList("hostname.global"));
        Map<String, String> specialHostnames = loadSpecialHostnames(config);

        getProxy().getPluginManager().registerListener(this, new LoginListener(this, kickMessageGlobal, kickMessageSpecial, globalHostnames, specialHostnames));
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored", "ConstantConditions"})
    @SneakyThrows(IOException.class)
    private void extractDefaultConfig() {
        getDataFolder().mkdirs();
        File configFile = new File(getDataFolder(), "config.yml");
        if (configFile.exists()) return;
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("config.yml")) {
            Files.copy(is, configFile.toPath());
        }
    }

    private @NotNull Map<String, String> loadSpecialHostnames(@NotNull Configuration config) {
        Map<String, String> map = new HashMap<>();

        for (String playerName : config.getSection("hostname.special").getKeys()) {
            map.put(playerName.toLowerCase(), config.getString("hostname.special." + playerName));
        }

        return map;
    }
}

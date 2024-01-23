package ru.violence.hostnameprotect.bungee;

import lombok.Getter;
import lombok.SneakyThrows;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import ru.violence.hostnameprotect.bungee.command.HostnameProtectCommand;
import ru.violence.hostnameprotect.bungee.listener.LoginListener;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HostnameProtectBungee extends Plugin {
    private @Getter String kickMessageGlobal;
    private @Getter String kickMessageSpecial;
    private @Getter Set<String> globalHostnames;
    private @Getter Map<String, String> specialHostnames;

    @Override
    public void onEnable() {
        reloadConfig();

        getProxy().getPluginManager().registerCommand(this, new HostnameProtectCommand(this));
        getProxy().getPluginManager().registerListener(this, new LoginListener(this));
    }

    @SneakyThrows
    public void reloadConfig() {
        extractDefaultConfig();
        Configuration config = YamlConfiguration.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));

        kickMessageGlobal = config.getString("kick-message.global");
        kickMessageSpecial = config.getString("kick-message.special");
        globalHostnames = new HashSet<>(config.getStringList("hostname.global"));
        specialHostnames = loadSpecialHostnames(config);
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

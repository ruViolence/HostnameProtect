package ru.violence.hostnameprotect.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import io.leangen.geantyref.TypeToken;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;
import ru.violence.hostnameprotect.velocity.command.HostnameProtectCommand;
import ru.violence.hostnameprotect.velocity.listener.LoginListener;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Plugin(id = "hostnameprotect", name = "HostnameProtect", version = BuildConstants.VERSION)
public class HostnameProtectVelocity {
    private final ProxyServer proxy;
    private final @Getter Logger logger;
    private final File dataFolder;

    private @Getter String kickMessageGlobal;
    private @Getter String kickMessageSpecial;
    private @Getter Set<String> globalHostnames;
    private @Getter Map<String, String> specialHostnames;

    @Inject
    public HostnameProtectVelocity(ProxyServer proxy, Logger logger, @DataDirectory Path dataFolder) {
        this.proxy = proxy;
        this.logger = logger;
        this.dataFolder = dataFolder.toFile();
    }

    @Subscribe
    public void onProxyInit(ProxyInitializeEvent event) {
        reloadConfig();

        proxy.getCommandManager().register("hostnameprotect", new HostnameProtectCommand(this));
        proxy.getEventManager().register(this, new LoginListener(this));
    }

    @SneakyThrows
    public void reloadConfig() {
        extractDefaultConfig();
        CommentedConfigurationNode config = YamlConfigurationLoader.builder().file(new File(dataFolder, "config.yml")).build().load();

        kickMessageGlobal = config.node("kick-message", "global").getString();
        kickMessageSpecial = config.node("kick-message", "special").getString();
        globalHostnames = new HashSet<>(config.node("hostname", "global").getList(TypeToken.get(String.class)));
        specialHostnames = loadSpecialHostnames(config);
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored", "ConstantConditions"})
    @SneakyThrows(IOException.class)
    private void extractDefaultConfig() {
        dataFolder.mkdirs();
        File configFile = new File(dataFolder, "config.yml");
        if (!configFile.exists()) {
            try (InputStream cfgStream = getClass().getClassLoader().getResourceAsStream("config.yml")) {
                Files.copy(cfgStream, configFile.toPath());
            }
        }
    }

    private @NotNull Map<String, String> loadSpecialHostnames(@NotNull CommentedConfigurationNode config) {
        Map<String, String> map = new HashMap<>();

        for (Map.Entry<Object, ? extends CommentedConfigurationNode> entry : config.node("hostname", "special").childrenMap().entrySet()) {
            map.put(entry.getKey().toString().toLowerCase(), entry.getValue().getString());
        }

        return map;
    }
}

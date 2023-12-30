package io.stealingdapenta.mc2048;

import io.stealingdapenta.mc2048.commands.GameCommand;
import java.util.Objects;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class MC2048 extends JavaPlugin {

    private static final String PLUGIN_ENABLED = "MC2048 plugin enabled.";
    private static final String PLUGIN_DISABLED = "MC2048 plugin disabled.";
    public static Logger logger;
    private static MC2048 instance = null;

    private final GameCommand gameCommand = new GameCommand();

    @Override
    public void onEnable() {
        instance = this;
        logger = this.getLogger();

        Objects.requireNonNull(this.getCommand("2048"))
               .setExecutor(gameCommand);

        logger.info(ChatColor.GOLD + PLUGIN_ENABLED);
    }

    @Override
    public void onDisable() {
        logger.info(ChatColor.GOLD + PLUGIN_DISABLED);
    }

    public static MC2048 getInstance() {
        return instance;
    }
}
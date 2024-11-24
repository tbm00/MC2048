package io.stealingdapenta.mc2048.config;


import static io.stealingdapenta.mc2048.MC2048.logger;

import io.stealingdapenta.mc2048.MC2048;
import io.stealingdapenta.mc2048.utils.TextUtil;
import java.util.Objects;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.plugin.java.JavaPlugin;

public enum ConfigKey {

    NO_PERMISSION("You don't have the required %s to execute this command."),
    RELOADED("Successfully reloaded the DamageIndicator configuration file."),
    PARSING_ERROR("Error parsing the value in the config file for "),
    NUMBER_OF_UNDO("1");

    private static final TextUtil textUtil = TextUtil.getInstance();
    private final String defaultValue;

    ConfigKey(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getDefaultValue() {
        return defaultValue;
    }


    public boolean getBooleanValue() {
        return getBooleanValue(name().toLowerCase());
    }

    public boolean getBooleanValue(String key) {
        JavaPlugin plugin = MC2048.getInstance();
        String valueAsString = plugin.getConfig().getString(key);

        return Boolean.parseBoolean(valueAsString);
    }

    public String getStringValue() {
        return MC2048.getInstance().getConfig().getString(name().toLowerCase());
    }

    public TextComponent getFormattedStringValue() {
        return textUtil.parseFormattedString(getStringValue());
    }

    public double getDoubleValue() {
        return getDoubleValue(name().toLowerCase());
    }

    private double getDoubleValue(String key) {
        JavaPlugin plugin = MC2048.getInstance();
        String valueAsString = plugin.getConfig().getString(key);
        double result;
        if (Objects.isNull(valueAsString)) {
            valueAsString = "0";
        }
        try {
            result = Double.parseDouble(valueAsString);
        } catch (NumberFormatException numberFormatException) {
            logger.warning(PARSING_ERROR.getStringValue() + key);
            result = 0;
        }
        return result;
    }

    public int getIntValue() {
        return getIntValue(name().toLowerCase());
    }

    private int getIntValue(String key) {
        JavaPlugin plugin = MC2048.getInstance();
        String valueAsString = plugin.getConfig().getString(key);
        int result;
        if (Objects.isNull(valueAsString)) {
            valueAsString = "0";
        }
        try {
            result = Integer.parseInt(valueAsString);
        } catch (NumberFormatException numberFormatException) {
            logger.warning(PARSING_ERROR.getStringValue() + key);
            result = 0;
        }
        return result;
    }

    public TextColor getTextColor() {
        return textUtil.parseRGB(getStringValue());
    }

}

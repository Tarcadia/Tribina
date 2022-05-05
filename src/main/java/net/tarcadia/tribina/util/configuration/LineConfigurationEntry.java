package net.tarcadia.tribina.util.configuration;

import org.jetbrains.annotations.NotNull;

public class LineConfigurationEntry implements ConfigurationEntry {

    private final String key;
    private String val;

    public LineConfigurationEntry(@NotNull String key, @NotNull String val) {
        this.key = key;
        this.val = val;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getValue() {
        return val;
    }

    @Override
    public String serialize() {
        return key + " = " + val + "\n";
    }

    @Override
    public void modify(@NotNull String val) {
        this.val = val;
    }
}

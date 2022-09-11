package net.tarcadia.tribina.util.configuration;

import org.jetbrains.annotations.NotNull;

public class UnrecognizedConfigurationEntry implements ConfigurationEntry {

    private final String line;

    public UnrecognizedConfigurationEntry() {
        this.line = null;
    }

    public UnrecognizedConfigurationEntry(String line) {
        this.line = line;
    }

    @Override
    public String getKey() {
        return null;
    }

    @Override
    public String getValue() {
        return null;
    }

    @Override
    public String serialize() {
        if (this.line != null) return line + "\n";
        else return "";
    }

    @Override
    public void modify(@NotNull String val) {}
}

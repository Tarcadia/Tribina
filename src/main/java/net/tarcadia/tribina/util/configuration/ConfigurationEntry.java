package net.tarcadia.tribina.util.configuration;

import org.jetbrains.annotations.NotNull;

public interface ConfigurationEntry {
    String getKey();
    String getValue();
    String serialize();
    void modify(@NotNull String val);
}

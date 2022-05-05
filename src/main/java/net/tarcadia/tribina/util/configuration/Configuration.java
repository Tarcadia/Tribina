package net.tarcadia.tribina.util.configuration;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Configuration {

    private final File file;
    private final List<ConfigurationEntry> entries;
    private final Map<String, ConfigurationEntry> config;

    public Configuration(@NotNull File file) {
        this.entries = new CopyOnWriteArrayList<>();
        this.config = new ConcurrentHashMap<>();
        this.file = file;
        this.load();
    }

    public void load() {
        List<String> lines;
        try {
            lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            lines = new LinkedList<>();
        }

        Iterator<String> i = lines.iterator();
        while (i.hasNext()) {
            String line = i.next().trim();
            String[] split = line.split("=", 2);
            if (split.length == 2 && !split[0].isBlank() && split[1].trim().equals("[")) {
                String key = split[0].trim();
                ListConfigurationEntry lce = new ListConfigurationEntry(key, i);
                this.entries.add(lce);
                this.config.put(key.toLowerCase(), lce);
            } else if (split.length == 2 && !split[0].isBlank()) {
                String key = split[0].trim();
                LineConfigurationEntry lce = new LineConfigurationEntry(key, split[1].trim());
                this.entries.add(lce);
                this.config.put(key.toLowerCase(), lce);
            } else {
                UnrecognizedConfigurationEntry uce = new UnrecognizedConfigurationEntry(line);
                this.entries.add(uce);
            }
        }
    }

    public void save() throws IOException {
        FileWriter writer = new FileWriter(file);
        for (ConfigurationEntry ce : entries) {
            writer.append(ce.serialize());
        }
        writer.close();
    }

    public int getInteger(@NotNull String key, int def) {
        ConfigurationEntry ce = config.get(key);
        if (ce == null) return def;
        Integer val;
        try {
            val = Integer.parseInt(ce.getValue().trim());
        } catch (NumberFormatException e) {
            val = null;
        }
        if (val == null) return def;
        else return val;
    }

    public long getLong(@NotNull String key, long def) {
        ConfigurationEntry ce = config.get(key);
        if (ce == null) return def;
        Long val;
        try {
            val = Long.parseLong(ce.getValue().trim());
        } catch (NumberFormatException e) {
            val = null;
        }
        if (val == null) return def;
        else return val;
    }

    public float getDouble(@NotNull String key, float def) {
        ConfigurationEntry ce = config.get(key);
        if (ce == null) return def;
        Float val;
        try {
            val = Float.parseFloat(ce.getValue().trim());
        } catch (NumberFormatException e) {
            val = null;
        }
        if (val == null) return def;
        else return val;
    }

    public double getDouble(@NotNull String key, double def) {
        ConfigurationEntry ce = config.get(key);
        if (ce == null) return def;
        Double val;
        try {
            val = Double.parseDouble(ce.getValue().trim());
        } catch (NumberFormatException e) {
            val = null;
        }
        if (val == null) return def;
        else return val;
    }

    public boolean getBoolean(@NotNull String key) {
        return getBoolean(key, false);
    }

    public boolean getBoolean(@NotNull String key, boolean def) {
        ConfigurationEntry ce = config.get(key);
        if (ce == null) return def;
        String val = ce.getValue().trim().toLowerCase();
        if (val.equals("true") || val.equals("yes") || val.equals("on") || val.equals("1")) return true;
        else if (val.equals("false") || val.equals("no") || val.equals("off") || val.equals("0")) return false;
        else return def;
    }

    @Nullable
    public String getString(@NotNull String key) {
        ConfigurationEntry ce = config.get(key);
        if (ce == null) return null;
        else return ce.getValue();
    }

    @NotNull
    public String getString(@NotNull String key, @NotNull String def) {
        ConfigurationEntry ce = config.get(key);
        if (ce == null) return def;
        else return ce.getValue();
    }

    public boolean modify(@NotNull String key, @NotNull String val) {
        ConfigurationEntry ce = config.get(key);
        if (ce instanceof LineConfigurationEntry) {
            ce.modify(val);
            return true;
        } else {
            return false;
        }
    }

    public boolean modifyAdd(@NotNull String key, @NotNull String val) {
        ConfigurationEntry ce = config.get(key);
        if (ce instanceof ListConfigurationEntry) {
            ((ListConfigurationEntry) ce).add(val);
            return true;
        } else {
            return false;
        }
    }

    public boolean modifyRemove(@NotNull String key, @NotNull String val) {
        ConfigurationEntry ce = config.get(key);
        if (ce instanceof ListConfigurationEntry) {
            ((ListConfigurationEntry) ce).remove(val);
            return true;
        } else {
            return false;
        }
    }

}

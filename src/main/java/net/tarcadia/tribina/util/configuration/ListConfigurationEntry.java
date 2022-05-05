package net.tarcadia.tribina.util.configuration;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ListConfigurationEntry implements ConfigurationEntry {

    private final String key;
    private final CopyOnWriteArrayList<String> list;

    public ListConfigurationEntry(@NotNull String key, @NotNull Iterator<String> i) {
        this.key = key;
        this.list = new CopyOnWriteArrayList<>(readList(i));
    }

    public ListConfigurationEntry(@NotNull String key, @NotNull String val0) {
        this.key = key;
        this.list = new CopyOnWriteArrayList<>();
        this.list.add(val0);
    }

    private List<String> readList(@NotNull Iterator<String> i) {
        List<String> list = new LinkedList<>();
        while (i.hasNext()) {
            String line = i.next().trim();
            if (line.equals("]")) return list;
            if (line.isEmpty()) continue;
            list.add(line);
        }
        return list;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getValue() {
        return null;
    }

    @Override
    public String serialize() {
        StringBuilder builder = new StringBuilder();
        builder.append(key).append(" = [").append("\n");
        for (String line : list) {
            builder.append("    ").append(line).append("\n");
        }
        builder.append("]").append("\n");
        return builder.toString();
    }

    @Override
    public void modify(@NotNull String val) {}

    public void clear() {
        this.list.clear();
    }

    public void add(@NotNull String val) {
        this.list.add(val);
    }

    public void remove(@NotNull String val) {
        this.list.remove(val);
    }

}

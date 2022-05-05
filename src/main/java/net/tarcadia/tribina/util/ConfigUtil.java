package net.tarcadia.tribina.util;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

public class ConfigUtil {

    public static Collection<String> fromFileList(@NotNull File file) {
        try {
            return Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static void toFileList(@NotNull File file, @NotNull Collection<String> list) {
        try {
            Files.write(file.toPath(), list, StandardCharsets.UTF_8);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String, String> fromFileMap(@NotNull File file) {
        try {
            List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
            Map<String, String> map = new HashMap<>();
            for (String line : lines) {
                String[] split = line.split("=", 2);
                map.put(split[0].trim().toLowerCase(), split[1].trim());
            }
            return map;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static void toFileMap(@NotNull File file, @NotNull Map<String, String> map) {
        try {
            List<String> lines = new LinkedList<>();
            for (var e : map.entrySet()) {
                lines.add(e.getKey() + " = " + e.getValue());
            }
            Files.write(file.toPath(), lines, StandardCharsets.UTF_8);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

}

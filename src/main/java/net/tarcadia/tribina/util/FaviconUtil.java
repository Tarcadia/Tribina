package net.tarcadia.tribina.util;

import net.tarcadia.tribina.main.Main;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Base64;

public class FaviconUtil {

    @Nullable
    public static String fromFile(@NotNull File file) {
        String favicon = null;
        try {
            InputStream stream = new FileInputStream(file);
            String[] filename = file.getName().split("\\.");
            String suffix = filename[filename.length - 1].toUpperCase();
            Type type = Type.valueOf(suffix);
            favicon = "data:image/" + type.getType() + ";base64," +
                    Base64.getEncoder().encodeToString(stream.readAllBytes());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return favicon;
    }

    @Nullable
    public static String fromResource(@NotNull String resource) {
        String favicon = null;
        try {
            InputStream stream = Main.class.getResourceAsStream(resource);
            String[] filename = resource.split("\\.");
            String suffix = filename[filename.length - 1].toUpperCase();
            Type type = Type.valueOf(suffix);
            if (stream != null)
                favicon = "data:image/" + type.getType() + ";base64," +
                        Base64.getEncoder().encodeToString(stream.readAllBytes());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return favicon;
    }

    public enum Type {

        PNG("png"),
        JPG("jpg"),
        JPEG("jpg"),
        BMP("bmp");

        private final String type;

        Type(@NotNull String type) {
            this.type = type;
        }
        public String getType() {
            return this.type;
        }

    }

}


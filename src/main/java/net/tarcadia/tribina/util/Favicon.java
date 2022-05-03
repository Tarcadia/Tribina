package net.tarcadia.tribina.util;

import net.tarcadia.tribina.Main;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Base64;

public class Favicon {

    public static String fromFile(File filename, Type type) {
        String favicon = null;
        try (InputStream stream = new FileInputStream(filename)) {
            favicon = "data:image/" + type.getType() + ";base64," +
                    Base64.getEncoder().encodeToString(stream.readAllBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return favicon;
    }

    public static String fromResource(String resource, Type type) {
        String favicon = null;
        try (InputStream stream = Main.class.getResourceAsStream(resource)) {
            if (stream != null)
                favicon = "data:image/" + type.getType() + ";base64," +
                        Base64.getEncoder().encodeToString(stream.readAllBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return favicon;
    }

    public enum Type {

        PNG("png"),
        JPG("jpg");

        private final String type;

        Type(String type) {
            this.type = type;
        }
        public String getType() {
            return this.type;
        }

    }

}


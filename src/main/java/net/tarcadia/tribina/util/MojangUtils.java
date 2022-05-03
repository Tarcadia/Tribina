package net.tarcadia.tribina.util;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class MojangUtils {

    public static @NotNull UUID fetchUUID(@NotNull String username) {
        final JsonObject jsonObject = net.minestom.server.utils.mojang.MojangUtils.fromUsername(username);
        System.out.println(jsonObject);
        try {
            final String uuid = jsonObject.get("id").getAsString();
            System.out.println(uuid);
            return UUID.fromString(uuid);
        } catch (Throwable e) {
            return UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(StandardCharsets.UTF_8));
        }
    }

}

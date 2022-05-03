package net.tarcadia.tribina;

import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandManager;
import net.tarcadia.tribina.command.StopCommand;

public class ServerCommand {

    public static void initServerCommand() {
        CommandManager manager = MinecraftServer.getCommandManager();
        manager.setUnknownCommandCallback(((sender, command) -> {
        }));
        manager.register(new StopCommand());
    }

}

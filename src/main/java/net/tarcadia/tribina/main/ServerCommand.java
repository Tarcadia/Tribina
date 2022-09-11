package net.tarcadia.tribina.main;

import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandManager;
import net.tarcadia.tribina.command.ServerInfoCommand;
import net.tarcadia.tribina.command.StopCommand;

public class ServerCommand {

    public static void initServerCommand() {
        CommandManager manager = MinecraftServer.getCommandManager();
        manager.setUnknownCommandCallback(((sender, command) -> sender.sendMessage("Syntax Error: Command not found.")));
        manager.register(new StopCommand());
        manager.register(new ServerInfoCommand());
    }

}

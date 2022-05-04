package net.tarcadia.tribina.command;

import net.minestom.server.MinecraftServer;
import net.minestom.server.command.ConsoleSender;
import net.minestom.server.command.builder.Command;

public final class StopCommand extends Command {
    public StopCommand() {
        super("stop");
        this.setCondition((sender, commandString) -> sender instanceof ConsoleSender);
        this.setDefaultExecutor((sender, context) -> MinecraftServer.stopCleanly());
    }
}

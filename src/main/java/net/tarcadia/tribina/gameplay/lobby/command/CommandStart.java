package net.tarcadia.tribina.gameplay.lobby.command;

import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.Player;
import net.tarcadia.tribina.gameplay.lobby.LobbyGamePlay;
import net.tarcadia.tribina.gameplay.tribina.TribinaGamePlay;
import net.tarcadia.tribina.main.ServerGamePlay;

public class CommandStart extends Command {

    public CommandStart () {
        super("start");
        this.setCondition((sender, commandString) -> (sender instanceof Player) && ServerGamePlay.atGamePlay((Player) sender, LobbyGamePlay.class));
        this.setDefaultExecutor((sender, context) -> {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                sender.sendMessage(LobbyGamePlay.HELLO_WORDS);
                ServerGamePlay.setGamePlay(player, new TribinaGamePlay(player));
            }
        });
    }

}

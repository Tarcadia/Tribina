package net.tarcadia.tribina.command;

import net.minestom.server.command.ConsoleSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.tarcadia.tribina.ServerInfo;

public class ServerInfoCommand extends Command {

    private static class FaviconCommand extends Command {
        public FaviconCommand() {
            super("favicon");
            this.setCondition((sender, commandString) -> sender instanceof ConsoleSender);
            this.setDefaultExecutor((sender, context) -> System.out.println("Syntax: \"server favicon file|res <path>\""));
            this.addSyntax((sender, context) -> {
                String path = context.get("path");
                boolean ret = false;
                if (context.get("from").equals("file")) {
                    ret = ServerInfo.setFaviconFromFile(path);
                } else if (context.get("from").equals("res")) {
                    ret = ServerInfo.setFaviconFromResource(path);
                }
                if (ret) sender.sendMessage("Server favicon set successfully.");
                else sender.sendMessage("Server favicon set unsuccessfully.");
                },
                    ArgumentType.Word("from").from("file", "res"),
                    ArgumentType.String("path").setDefaultValue("favicon.png")
            );
        }
    }

    private static class MOTDCommand extends Command {
        public MOTDCommand() {
            super("motd");
            this.setCondition((sender, commandString) -> sender instanceof ConsoleSender);
            this.setDefaultExecutor((sender, context) -> System.out.println("Syntax: \"server motd <motd>\""));
            this.addSyntax((sender, context) -> {
                boolean ret = ServerInfo.setMOTDPlainText(context.get("motd"));
                if (ret) sender.sendMessage("Server MOTD set successfully.");
                else sender.sendMessage("Server MOTD set unsuccessfully.");
                },
                    ArgumentType.String("motd")
            );
        }
    }

    private static class MaxPlayerCountCommand extends Command {
        public MaxPlayerCountCommand() {
            super("max-player-count");
            this.setCondition((sender, commandString) -> sender instanceof ConsoleSender);
            this.setDefaultExecutor((sender, context) -> System.out.println("Syntax: \"server max-player-count <count>\""));
            this.addSyntax((sender, context) -> {
                boolean ret = ServerInfo.setMaxPlayerCount(context.get("count"));
                if (ret) sender.sendMessage("Server max player count set successfully.");
                else sender.sendMessage("Server max player count set unsuccessfully.");
                },
                    ArgumentType.Integer("count")
            );
        }
    }

    private static class MaxPlayerMessageCommand extends Command {
        public MaxPlayerMessageCommand() {
            super("max-player-message");
            this.setCondition((sender, commandString) -> sender instanceof ConsoleSender);
            this.setDefaultExecutor((sender, context) -> System.out.println("Syntax: \"server max-player-message translatable|text <message>\""));
            this.addSyntax((sender, context) -> {
                String message = context.get("message");
                boolean ret = false;
                if (context.get("type").equals("translatable")) {
                    ret = ServerInfo.setMaxPlayerMessageTranslatable(message);
                } else if (context.get("type").equals("text")) {
                    ret = ServerInfo.setMaxPlayerMessageText(message);
                }
                if (ret) sender.sendMessage("Server max player message set successfully.");
                else sender.sendMessage("Server max player message set unsuccessfully.");
                },
                    ArgumentType.Word("type").from("translatable", "text"),
                    ArgumentType.String("message")
            );
        }
    }

    public ServerInfoCommand() {
        super("server");
        this.setCondition((sender, commandString) -> sender instanceof ConsoleSender);
        this.addSubcommand(new FaviconCommand());
        this.addSubcommand(new MOTDCommand());
        this.addSubcommand(new MaxPlayerCountCommand());
        this.addSubcommand(new MaxPlayerMessageCommand());
        this.setDefaultExecutor((sender, context) -> System.out.println("Syntax: \"server favicon|motd|max-player-count|max-player-message ...\""));
    }
}

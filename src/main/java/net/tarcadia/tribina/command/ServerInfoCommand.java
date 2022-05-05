package net.tarcadia.tribina.command;

import net.minestom.server.command.ConsoleSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.tarcadia.tribina.main.ServerInfo;

public class ServerInfoCommand extends Command {

    private static class FaviconCommand extends Command {
        public FaviconCommand() {
            super("favicon");
            this.setCondition((sender, commandString) -> sender instanceof ConsoleSender);
            this.setDefaultExecutor((sender, context) -> sender.sendMessage("Syntax: \"server favicon <path>\""));
            this.addSyntax((sender, context) -> {
                boolean ret = ServerInfo.setFavicon(context.get("path"));
                if (ret) sender.sendMessage("Server favicon set successfully.");
                else sender.sendMessage("Server favicon set unsuccessfully.");
                },
                    ArgumentType.String("path").setDefaultValue("./favicon.png").filter(s -> !s.isBlank())
            );
        }
    }

    private static class MOTDCommand extends Command {
        public MOTDCommand() {
            super("motd");
            this.setCondition((sender, commandString) -> sender instanceof ConsoleSender);
            this.setDefaultExecutor((sender, context) -> sender.sendMessage("Syntax: \"server motd <motd>\""));
            this.addSyntax((sender, context) -> {
                boolean ret = ServerInfo.setMOTD(context.get("motd"));
                if (ret) sender.sendMessage("Server MOTD set successfully.");
                else sender.sendMessage("Server MOTD set unsuccessfully.");
                },
                    ArgumentType.String("motd").filter(s -> !s.isBlank())
            );
        }
    }

    private static class MaxPlayerCountCommand extends Command {
        public MaxPlayerCountCommand() {
            super("max-player-count");
            this.setCondition((sender, commandString) -> sender instanceof ConsoleSender);
            this.setDefaultExecutor((sender, context) -> sender.sendMessage("Syntax: \"server max-player-count <count>\""));
            this.addSyntax((sender, context) -> {
                boolean ret = ServerInfo.setMaxPlayerCount(context.get("count"));
                if (ret) sender.sendMessage("Server max player count set successfully.");
                else sender.sendMessage("Server max player count set unsuccessfully.");
                },
                    ArgumentType.Integer("count")
            );
        }
    }

    private static class BanCommand extends Command {
        public BanCommand() {
            super("ban");
            this.setCondition((sender, commandString) -> sender instanceof ConsoleSender);
            this.setDefaultExecutor((sender, context) -> sender.sendMessage("Syntax: \"server ban {player <username>}|{ip <user-ip>}\""));
            this.addSyntax((sender, context) -> {
                String username = context.get("username");
                boolean ret = ServerInfo.banPlayer(username);
                if (ret) sender.sendMessage("Server ban player " + username + " successfully.");
                else sender.sendMessage("Server ban player " + username + " unsuccessfully.");
                },
                    ArgumentType.Literal("player"),
                    ArgumentType.String("username").filter(s -> !s.isBlank())
            );
            this.addSyntax((sender, context) -> {
                String ip = context.get("user-ip");
                boolean ret = ServerInfo.banIP(ip);
                if (ret) sender.sendMessage("Server ban ip " + ip + " successfully.");
                else sender.sendMessage("Server ban ip " + ip + " unsuccessfully.");
                },
                    ArgumentType.Literal("ip"),
                    ArgumentType.String("user-ip").filter(s -> !s.isBlank())
            );
        }
    }

    private static class UnbanCommand extends Command {
        public UnbanCommand() {
            super("unban");
            this.setCondition((sender, commandString) -> sender instanceof ConsoleSender);
            this.setDefaultExecutor((sender, context) -> sender.sendMessage("Syntax: \"server unban {player <player>}|{ip <ip>}\""));
            this.addSyntax((sender, context) -> {
                String username = context.get("username");
                boolean ret = ServerInfo.unbanPlayer(username);
                if (ret) sender.sendMessage("Server unban player " + username + " successfully.");
                else sender.sendMessage("Server unban player " + username + " unsuccessfully.");
                },
                    ArgumentType.Literal("player"),
                    ArgumentType.String("username").filter(s -> !s.isBlank())
            );
            this.addSyntax((sender, context) -> {
                String ip = context.get("user-ip");
                boolean ret = ServerInfo.unbanIP(ip);
                if (ret) sender.sendMessage("Server unban ip " + ip + " successfully.");
                else sender.sendMessage("Server unban ip " + ip + " unsuccessfully.");
                },
                    ArgumentType.Literal("ip"),
                    ArgumentType.String("user-ip").filter(s -> !s.isBlank())
            );
        }
    }

    private static class InfoCommand extends Command {
        public InfoCommand() {
            super("info");
            this.setCondition((sender, commandString) -> sender instanceof ConsoleSender);
            this.setDefaultExecutor((sender, context) -> sender.sendMessage("Syntax: \"server info\""));
            this.addSyntax((sender, context) -> {
                sender.sendMessage("Favicon: " + ServerInfo.getFavicon());
                sender.sendMessage("MOTD: " + ServerInfo.getMOTD());
                sender.sendMessage("Max Player Count: " + ServerInfo.getMaxPlayerCount());
                sender.sendMessage("Ban List: " + ServerInfo.getBanList());
            }
            );
        }
    }

    public ServerInfoCommand() {
        super("server");
        this.setCondition((sender, commandString) -> sender instanceof ConsoleSender);
        this.addSubcommand(new FaviconCommand());
        this.addSubcommand(new MOTDCommand());
        this.addSubcommand(new MaxPlayerCountCommand());
        this.addSubcommand(new BanCommand());
        this.addSubcommand(new UnbanCommand());
        this.addSubcommand(new InfoCommand());
        this.setDefaultExecutor((sender, context) -> sender.sendMessage("Syntax: \"server favicon|motd|max-player-count|ban|unban ...\""));
    }
}

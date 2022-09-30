package it.albemiglio.seniorparkour.commands;

import it.albemiglio.seniorparkour.SeniorParkour;
import it.albemiglio.seniorparkour.commands.subcommands.*;
import it.albemiglio.seniorparkour.commands.subcommands.Subcommand;
import it.mycraft.powerlib.common.chat.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ParkourCommand implements CommandExecutor, TabCompleter {

    private SeniorParkour main;

    public ParkourCommand(SeniorParkour main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(args.length < 1) {
            new Message("&bSeniorParkour - By AlbeMiglio").send(sender);
            return false;
        }
        Subcommand subcommand = getSubcommand(args[0]);
        if(subcommand == null) {
            getMessage("invalid-syntax").hex().send(sender);
            return false;
        }
        if(!sender.hasPermission(subcommand.getPermission())) {
            getMessage("not-enough-permissions").hex().send(sender);
            return false;
        }
        return subcommand.onCommand(sender, cmd, s, args);
    }

    @Nullable
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {
        if(args.length == 0) {
            return Arrays.asList("checkpoint", "create", "delete", "deltop", "end", "info", "leave", "settop", "stats",
                    "teleport", "top");
        }
        else if (args.length == 1) {
            if(args[0].equalsIgnoreCase("stats")) {
                return Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList());
            }
            else if(args[0].equalsIgnoreCase("create")) {
                return Collections.singletonList("parkourName");
            }
            else if(args[0].equalsIgnoreCase("leave")) {
                return Collections.emptyList();
            }
            else return new ArrayList<>(this.main.getParkourService().getParkours().keySet());
        }
        else return Collections.singletonList("checkpointNumber");
    }

    private Subcommand getSubcommand(String input) {
        switch(input) {
            default:
                return null;
            case "checkpoint":
                return new Checkpoint(this.main);
            case "create":
                return new Create(this.main);
            case "delete":
                return new Delete(this.main);
            case "deltop":
                return new DelTop(this.main);
            case "end":
                return new End(this.main);
            case "info":
                return new Info(this.main);
            case "leave":
                return new Leave(this.main);
            case "settop":
                return new SetTop(this.main);
            case "stats":
                return new Stats(this.main);
            case "teleport":
                return new Teleport(this.main);
            case "top":
                return new Top(this.main);
        }
    }

    private Message getMessage(String path) {
        return new Message(this.main.getFileService().getMessages().getString(path)).hex();
    }
}

package it.albemiglio.seniorparkour.commands.subcommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public interface Subcommand {

    boolean onCommand(CommandSender sender, Command cmd, String s, String[] args);

    String getPermission();
}

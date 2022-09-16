package it.albemiglio.seniorparkour.commands.subcommands;

import it.albemiglio.seniorparkour.SeniorParkour;
import it.albemiglio.seniorparkour.objects.Subcommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class SetTop implements Subcommand {

    private SeniorParkour main;

    public SetTop(SeniorParkour main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        return false;
    }

    @Override
    public String getPermission() {
        return "parkour.admin.settop";
    }
}

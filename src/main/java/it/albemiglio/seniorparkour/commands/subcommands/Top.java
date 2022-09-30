package it.albemiglio.seniorparkour.commands.subcommands;

import it.albemiglio.seniorparkour.SeniorParkour;
import it.albemiglio.seniorparkour.guis.TopGUI;
import it.albemiglio.seniorparkour.objects.Parkour;
import it.mycraft.powerlib.common.chat.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Top implements Subcommand {

    private SeniorParkour main;

    public Top(SeniorParkour main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(args.length < 2) {
            getMessage("correct-usage-create").send(sender);
            return false;
        }
        String parkourName = args[1];
        if(!this.main.getParkourService().getParkours().containsKey(parkourName)) {
            getMessage("parkour-does-not-exist").send(sender);
            return false;
        }
        Parkour parkour = this.main.getParkourService().getParkour(parkourName);
        new TopGUI(this.main, parkour).showGUI((Player) sender);
        return false;
    }

    @Override
    public String getPermission() {
        return null;
    }

    private Message getMessage(String path) {
        return new Message(this.main.getFileService().getMessages().getString(path)).hex();
    }
}

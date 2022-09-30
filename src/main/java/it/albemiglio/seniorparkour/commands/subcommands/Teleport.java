package it.albemiglio.seniorparkour.commands.subcommands;

import it.albemiglio.seniorparkour.SeniorParkour;
import it.albemiglio.seniorparkour.objects.Parkour;
import it.albemiglio.seniorparkour.utils.LocUtils;
import it.mycraft.powerlib.common.chat.Message;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Teleport implements Subcommand {

    private final SeniorParkour main;

    public Teleport(SeniorParkour main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(args.length < 2) {
            getMessage("correct-usage-teleport").send(sender);
            return false;
        }
        String parkourName = args[1];
        if(!this.main.getParkourService().getParkours().containsKey(parkourName)) {
            getMessage("parkour-does-not-exist").send(sender);
            return false;
        }
        Parkour parkour = this.main.getParkourService().getParkour(parkourName);
        Player p = (Player) sender;
        if(args.length > 2) {
            int checkpoint;
            try {
                checkpoint = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                getMessage("correct-usage-teleport").send(sender);
                return false;
            }
            if(checkpoint < 1 || checkpoint > parkour.getCheckpoints().size()) {
                getMessage("invalid-checkpoint").send(sender);
                return false;
            }
            Location loc = parkour.getCheckpoints().get(checkpoint-1);
            p.teleport(loc);
            getMessage("teleport-success").send(sender);
            return false;
        }
        p.teleport(parkour.getStart());
        getMessage("teleport-success").send(sender);
        return false;
    }

    @Override
    public String getPermission() {
        return "parkour.admin.teleport";
    }

    private Message getMessage(String path) {
        return new Message(this.main.getFileService().getMessages().getString(path)).hex();
    }
}

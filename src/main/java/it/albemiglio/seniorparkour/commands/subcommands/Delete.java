package it.albemiglio.seniorparkour.commands.subcommands;

import it.albemiglio.seniorparkour.SeniorParkour;
import it.albemiglio.seniorparkour.objects.Parkour;
import it.albemiglio.seniorparkour.utils.LocUtils;
import it.mycraft.powerlib.common.chat.Message;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class Delete implements Subcommand {

    private final SeniorParkour main;

    public Delete(SeniorParkour main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(args.length < 2) {
            getMessage("correct-usage-delete").send(sender);
            return false;
        }
        String parkourName = args[1];
        if(!this.main.getParkourService().getParkours().containsKey(parkourName)) {
            getMessage("parkour-does-not-exist").send(sender);
            return false;
        }
        Parkour parkour = this.main.getParkourService().getParkour(parkourName);
        if(args.length > 2) {
            int checkpoint;
            try {
                checkpoint = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                getMessage("correct-usage-delete").send(sender);
                return false;
            }
            if(checkpoint < 1 || checkpoint > parkour.getCheckpoints().size()) {
                getMessage("invalid-checkpoint").send(sender);
                return false;
            }
            Location loc = parkour.getCheckpoints().get(checkpoint-1);
            loc.getBlock().setType(Material.AIR);
            parkour.removeCheckpoint(checkpoint-1);
            this.main.getParkourService().getParkours().put(parkourName, parkour);
            this.main.getFileService().getParkour(parkourName).set("checkpoints", LocUtils.serializeList(parkour.getCheckpoints()));
            this.main.getFileService().saveParkour(parkourName);
            getMessage("delete-checkpoint-success").send(sender);
            return false;
        }
        this.main.getParkourService().removeParkour(parkourName);
        getMessage("delete-success").send(sender);
        return false;
    }

    @Override
    public String getPermission() {
        return "parkour.admin.delete";
    }

    private Message getMessage(String path) {
        return new Message(this.main.getFileService().getMessages().getString(path)).hex();
    }
}

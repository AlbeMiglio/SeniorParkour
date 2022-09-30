package it.albemiglio.seniorparkour.commands.subcommands;

import it.albemiglio.seniorparkour.SeniorParkour;
import it.albemiglio.seniorparkour.objects.Hologram;
import it.albemiglio.seniorparkour.objects.Parkour;
import it.albemiglio.seniorparkour.utils.LocUtils;
import it.mycraft.powerlib.common.chat.Message;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Checkpoint implements Subcommand {

    private SeniorParkour main;

    public Checkpoint(SeniorParkour main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(args.length < 2) {
            getMessage("correct-usage-checkpoint").send(sender);
            return false;
        }
        String parkourName = args[1];
        if(!this.main.getParkourService().getParkours().containsKey(parkourName)) {
            getMessage("parkour-does-not-exist").send(sender);
            return false;
        }
        Parkour parkour = this.main.getParkourService().getParkour(parkourName);
        Location loc = ((Player) sender).getLocation();
        if(parkour.getCheckpoints().contains(loc)) {
            return false;
        }
        loc.getBlock().setType(Material.STONE_PRESSURE_PLATE);
        parkour.addCheckpoint(loc);
        this.main.getParkourService().getParkours().put(parkourName, parkour);
        this.main.getFileService().getParkour(parkourName).set("checkpoints", LocUtils.serializeList(parkour.getCheckpoints()));
        this.main.getFileService().saveParkour(parkourName);
        getMessage("checkpoint-created").send(sender);
        for(Player p : Bukkit.getOnlinePlayers()) {
            new Hologram(parkour.getCheckpoints().get(parkour.getCheckpoints().size()-1).add(0, this.main.getMisc().getOffset(), 0),
                    this.main.getMisc().format(this.main.getMisc().getCheckpointHologram(), parkourName, p)).show(p);
        }
        return true;
    }

    @Override
    public String getPermission() {
        return "parkour.admin.checkpoint";
    }

    private Message getMessage(String path) {
        return new Message(this.main.getFileService().getConfig().getString(path)).hex();
    }
}

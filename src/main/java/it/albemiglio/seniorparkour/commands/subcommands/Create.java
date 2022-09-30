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

public class Create implements Subcommand {
    private SeniorParkour main;

    public Create(SeniorParkour main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(args.length < 2) {
            getMessage("correct-usage-create").hex().send(sender);
            return false;
        }
        String parkourName = args[1];
        if(this.main.getParkourService().getParkours().containsKey(parkourName)) {
            getMessage("parkour-already-exists").hex().send(sender);
            return false;
        }
        Parkour parkour = new Parkour(parkourName);
        Location loc = ((Player) sender).getLocation();
        loc.getBlock().setType(Material.STONE_PRESSURE_PLATE);
        parkour.setStart(loc);
        this.main.getParkourService().getParkours().put(parkourName, parkour);
        this.main.getFileService().createParkour(parkourName);
        this.main.getFileService().getParkour(parkourName).set("start", LocUtils.serialize(loc));
        this.main.getFileService().saveParkour(parkourName);
        getMessage("parkour-created").send(sender);
        for(Player p : Bukkit.getOnlinePlayers()) {
            new Hologram(parkour.getStart().add(0, this.main.getMisc().getOffset(), 0),
                    this.main.getMisc().format(this.main.getMisc().getStartHologram(), parkourName, p)).show(p);
        }
        return false;
    }

    @Override
    public String getPermission() {
        return "parkour.admin.create";
    }

    private Message getMessage(String path) {
        return new Message(this.main.getFileService().getMessages().getString(path)).hex();
    }
}

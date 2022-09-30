package it.albemiglio.seniorparkour.commands.subcommands;

import it.albemiglio.seniorparkour.SeniorParkour;
import it.albemiglio.seniorparkour.objects.Hologram;
import it.albemiglio.seniorparkour.objects.Parkour;
import it.albemiglio.seniorparkour.utils.LocUtils;
import it.mycraft.powerlib.common.chat.Message;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetTop implements Subcommand {

    private SeniorParkour main;

    public SetTop(SeniorParkour main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(args.length < 2) {
            getMessage("correct-usage-settop").send(sender);
            return false;
        }
        String parkourName = args[1];
        if(!this.main.getParkourService().getParkours().containsKey(parkourName)) {
            getMessage("parkour-does-not-exist").send(sender);
            return false;
        }
        Parkour parkour = this.main.getParkourService().getParkour(parkourName);
        Location loc = ((Player) sender).getLocation();
        parkour.setTopHologram(loc);
        this.main.getParkourService().getParkours().put(parkourName, parkour);
        this.main.getFileService().getParkour(parkourName).set("top", LocUtils.serialize(parkour.getTopHologram()));
        this.main.getFileService().saveParkour(parkourName);
        getMessage("settop-success").send(sender);
        for(Player p : Bukkit.getOnlinePlayers()) {
            new Hologram(parkour.getTopHologram().add(0, this.main.getMisc().getOffset(), 0),
                    this.main.getMisc().format(this.main.getMisc().getTopHologram(), parkourName, p)).show(p);
        }
        return false;
    }

    @Override
    public String getPermission() {
        return "parkour.admin.settop";
    }

    private Message getMessage(String path) {
        return new Message(this.main.getFileService().getMessages().getString(path)).hex();
    }
}

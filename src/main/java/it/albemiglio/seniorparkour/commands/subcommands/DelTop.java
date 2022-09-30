package it.albemiglio.seniorparkour.commands.subcommands;

import it.albemiglio.seniorparkour.SeniorParkour;
import it.albemiglio.seniorparkour.objects.Parkour;
import it.mycraft.powerlib.common.chat.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class DelTop implements Subcommand {

    private SeniorParkour main;

    public DelTop(SeniorParkour main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(args.length < 2) {
            getMessage("correct-usage-deltop").send(sender);
            return false;
        }
        String parkourName = args[1];
        if(!this.main.getParkourService().getParkours().containsKey(parkourName)) {
            getMessage("parkour-does-not-exist").send(sender);
            return false;
        }
        Parkour parkour = this.main.getParkourService().getParkour(parkourName);
        if(parkour.getTopHologram() == null) {
            return false;
        }
        parkour.setTopHologram(null);
        this.main.getParkourService().getParkours().put(parkourName, parkour);
        this.main.getFileService().getParkour(parkourName).set("top", "");
        this.main.getFileService().saveParkour(parkourName);
        getMessage("deltop-success").send(sender);
        return false;
    }

    @Override
    public String getPermission() {
        return "parkour.admin.deltop";
    }

    private Message getMessage(String path) {
        return new Message(this.main.getFileService().getMessages().getString(path)).hex();
    }
}


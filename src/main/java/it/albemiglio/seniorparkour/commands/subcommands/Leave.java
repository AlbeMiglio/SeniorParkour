package it.albemiglio.seniorparkour.commands.subcommands;

import it.albemiglio.seniorparkour.SeniorParkour;
import it.mycraft.powerlib.common.chat.Message;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Leave implements Subcommand {

    private final SeniorParkour main;

    public Leave(SeniorParkour main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        Player p = (Player) sender;
        if(!this.main.getParkourService().getCurrentPlayers().containsKey(p.getUniqueId())) {
            getMessage("you-are-not-in-a-parkour").send(sender);
            return false;
        }
        this.main.getParkourService().getCurrentPlayers().remove(p.getUniqueId());
        Location spawn = this.main.getMisc().getSpawn();
        p.teleport(spawn);
        getMessage("leave-success").send(sender);
        return false;
    }

    @Override
    public String getPermission() {
        return "parkour.leave";
    }

    private Message getMessage(String path) {
        return new Message(this.main.getFileService().getMessages().getString(path)).hex();
    }
}

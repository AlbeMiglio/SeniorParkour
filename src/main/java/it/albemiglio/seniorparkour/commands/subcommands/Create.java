package it.albemiglio.seniorparkour.commands.subcommands;

import it.albemiglio.seniorparkour.SeniorParkour;
import it.albemiglio.seniorparkour.objects.Hologram;
import it.albemiglio.seniorparkour.objects.Parkour;
import it.albemiglio.seniorparkour.objects.Subcommand;
import it.albemiglio.seniorparkour.utils.LocUtils;
import it.albemiglio.seniorparkour.utils.TimeUtils;
import it.mycraft.powerlib.common.chat.Message;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
        parkour.setStart(loc);
        this.main.getParkourService().getParkours().put(parkourName, parkour);
        this.main.getFileService().createParkour(parkourName);
        this.main.getFileService().getParkour(parkourName).set("start", LocUtils.serialize(loc));
        this.main.getFileService().saveParkour(parkourName);
        getMessage("parkour-created").hex().send(sender);
        for(Player p : Bukkit.getOnlinePlayers()) {
            new Hologram(parkour.getStart().add(0, this.main.getMisc().getOffset(), 0),
                    format(this.main.getMisc().getStartHologram(), parkourName, p)).show(p);
        }
        return false;
    }

    private List<String> format(List<String> list, String parkour, Player p) {
        List<String> l = new ArrayList<>();
        String position = this.main.getStatsService().getPositionOf(p.getUniqueId(), parkour)+"";
        String time = "None";
        if(this.main.getStatsService().getStats().get(p.getUniqueId()).containsKey(parkour)) {
            time = TimeUtils.formattedTime(this.main.getStatsService()
                    .getStats().get(p.getUniqueId()).get(parkour));
        }
        for(String line : list) {
            Message msg = new Message(line);
            msg = msg
                    .addPlaceHolder("%name", parkour)
                    .addPlaceHolder("%player", p.getName())
                    .addPlaceHolder("%time", time)
                    .addPlaceHolder("%position", position);
            int pos = 0;
            for (AbstractMap.SimpleEntry<UUID, Long> entry :
                    this.main.getStatsService().getTopStatsOf(10, parkour)) {
                pos++;
                msg = msg.addPlaceHolder("%"+pos+"_name", Bukkit.getOfflinePlayer(entry.getKey()).getName())
                        .addPlaceHolder("%"+pos+"_time", TimeUtils.formattedTime(entry.getValue()));
            }
            if(msg.getText().matches("%\\d+(_name)")) {
                msg.set(msg.hex().getText().replaceAll("%\\d+(_name)", "Empty"));
            }
            if(msg.getText().matches("%\\d+(_time)")) {
                msg.set(msg.hex().getText().replaceAll("%\\d+(_time)", "00:00"));
            }
            l.add(msg.getText());
        }
        return l;
    }

    @Override
    public String getPermission() {
        return "parkour.admin.create";
    }

    private Message getMessage(String path) {
        return new Message(this.main.getFileService().getMessages().getString(path));
    }
}

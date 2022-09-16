package it.albemiglio.seniorparkour.listeners;

import com.mysql.jdbc.TimeUtil;
import it.albemiglio.seniorparkour.SeniorParkour;
import it.albemiglio.seniorparkour.objects.Hologram;
import it.albemiglio.seniorparkour.objects.Parkour;
import it.albemiglio.seniorparkour.utils.TimeUtils;
import it.mycraft.powerlib.common.chat.Message;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JoinListener implements Listener {

    private final SeniorParkour main;

    public JoinListener(SeniorParkour main) {
        this.main = main;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        Bukkit.getScheduler().runTaskAsynchronously(this.main, () -> {
            for (String parkourName : this.main.getParkourService().getParkours().keySet()) {
                Parkour parkour = this.main.getParkourService().getParkour(parkourName);
                updateParkourHolograms(parkour, p);
            }
        });
    }

    private void updateParkourHolograms(Parkour parkour, Player p) {
        String parkourName = parkour.getName();
        if (parkour.getStart() != null) {
            new Hologram(parkour.getStart().add(0, this.main.getMisc().getOffset(), 0),
                    format(this.main.getMisc().getStartHologram(), parkourName, p)).show(p);
        }
        if(parkour.getEnd() != null) {
            new Hologram(parkour.getEnd().add(0, this.main.getMisc().getOffset(), 0),
                    format(this.main.getMisc().getEndHologram(), parkourName, p)).show(p);
        }
        if(parkour.getTopHologram() != null) {
            new Hologram(parkour.getTopHologram().add(0, this.main.getMisc().getOffset(), 0),
                    format(this.main.getMisc().getTopHologram(), parkourName, p)).show(p);
        }
        for(int i = 0; i < parkour.getCheckpoints().size(); i++) {
            Location loc = parkour.getCheckpoints().get(i).add(0, this.main.getMisc().getOffset(), 0);
            new Hologram(loc, new Message(this.main.getMisc().getCheckpointHologram())
                    .addPlaceHolder("%n", (i+1))
                    .hex().getTextList()).show(p);
        }
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
}

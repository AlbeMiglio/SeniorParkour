package it.albemiglio.seniorparkour.utils;

import it.albemiglio.seniorparkour.SeniorParkour;
import it.mycraft.powerlib.common.chat.Message;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Misc {

    private SeniorParkour main;
    @Getter
    private Location spawn;
    @Getter
    private List<String> startHologram;
    @Getter
    private List<String> endHologram;
    @Getter
    private List<String> checkpointHologram;
    @Getter
    private List<String> topHologram;
    @Getter
    private double offset;

    public Misc(SeniorParkour main) {
        this.main = main;
        this.load();
    }

    public void load() {
        this.spawn = LocUtils.deserialize(this.main.getFileService().getConfig().getString("spawn"));
        this.startHologram = this.main.getFileService().getConfig().getStringList("start-hologram");
        this.endHologram = this.main.getFileService().getConfig().getStringList("end-hologram");
        this.checkpointHologram = this.main.getFileService().getConfig().getStringList("checkpoint-hologram");
        this.topHologram = this.main.getFileService().getConfig().getStringList("top-hologram");
        this.offset = this.main.getFileService().getConfig().getDouble("hologram-height");
    }

    public List<String> format(List<String> list, String parkour, Player p) {
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

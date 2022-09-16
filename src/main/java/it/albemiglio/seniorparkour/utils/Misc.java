package it.albemiglio.seniorparkour.utils;

import it.albemiglio.seniorparkour.SeniorParkour;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.util.List;

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
}

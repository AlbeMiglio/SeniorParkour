package it.albemiglio.seniorparkour.objects;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class Parkour {

    @Getter
    private String name;
    @Getter
    @Setter
    private Location start;
    @Getter
    @Setter
    private Location end;
    @Getter
    @Setter
    private Location topHologram;
    @Getter
    @Setter
    private List<Location> checkpoints;

    public Parkour(String name) {
        this.name = name;
        this.checkpoints = new ArrayList<>();
    }

    public void removeCheckpoint(int index) {
        this.checkpoints.remove(index);
    }

    public void addCheckpoint(Location location) {
        this.checkpoints.add(location);
    }
}

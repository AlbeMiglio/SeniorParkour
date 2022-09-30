package it.albemiglio.seniorparkour.services;

import com.google.common.io.Files;
import it.albemiglio.seniorparkour.SeniorParkour;
import it.albemiglio.seniorparkour.objects.CurrentParkour;
import it.albemiglio.seniorparkour.objects.Parkour;
import it.albemiglio.seniorparkour.utils.LocUtils;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ParkourService extends Service {

    private final SeniorParkour main;
    @Getter
    private ConcurrentHashMap<UUID, CurrentParkour> currentPlayers;
    @Getter
    private HashMap<String, Parkour> parkours;

    public ParkourService(SeniorParkour main) {
        this.main = main;
        this.start();
    }

    @Override
    protected void start() {
        this.parkours = new HashMap<>();
        File parkoursFolder = new File(this.main.getDataFolder() + "/SeniorParkour/parkours");
        File[] files = parkoursFolder.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (!files[i].isFile()) {
                continue;
            }
            String parkourName = Files.getNameWithoutExtension(files[i].getName());
            FileConfiguration config = this.main.getFileService().getParkour(parkourName);
            Parkour parkour = new Parkour(parkourName);
            if (LocUtils.isDeserializable(config.getString("start"))) {
                parkour.setStart(LocUtils.deserialize(config.getString("start")));
            }
            if (LocUtils.isDeserializable(config.getString("end"))) {
                parkour.setEnd(LocUtils.deserialize(config.getString("end")));
            }
            if (LocUtils.isDeserializable(config.getString("top"))) {
                parkour.setTopHologram(LocUtils.deserialize(config.getString("top")));
            }
            for (String checkpoint : config.getStringList("checkpoints")) {
                if (LocUtils.isDeserializable(checkpoint)) {
                    parkour.addCheckpoint(LocUtils.deserialize(checkpoint));
                }
            }
            this.parkours.put(parkourName, parkour);
        }
    }

    public Parkour getParkour(String parkourName) {
        return this.getParkours().get(parkourName);
    }

    protected boolean addParkour(String parkour) {
        if(this.parkours.containsKey(parkour)) {
            return false;
        }
        this.main.getFileService().createParkour(parkour);
        return true;
    }

    protected void updateStart(String parkour, Location start) {
        if(this.parkours.containsKey(parkour)) {
            Parkour p = this.parkours.get(parkour);
            p.setStart(start);
            this.parkours.put(parkour, p);
        }
    }

    protected void updateEnd(String parkour, Location end) {
        if(this.parkours.containsKey(parkour)) {
            Parkour p = this.parkours.get(parkour);
            p.setEnd(end);
            this.parkours.put(parkour, p);
        }
    }

    protected void updateTopHologram(String parkour, Location top) {
        if(this.parkours.containsKey(parkour)) {
            Parkour p = this.parkours.get(parkour);
            p.setTopHologram(top);
            this.parkours.put(parkour, p);
        }
    }

    protected void updateCheckpoints(String parkour, List<Location> checkpoints) {
        if(this.parkours.containsKey(parkour)) {
            Parkour p = this.parkours.get(parkour);
            p.setCheckpoints(checkpoints);
            this.parkours.put(parkour, p);
        }
    }

    protected void addCheckpoint(String parkour, Location checkpoint) {
        if(this.parkours.containsKey(parkour)) {
            Parkour p = this.parkours.get(parkour);
            List<Location> checkpoints = p.getCheckpoints();
            checkpoints.add(checkpoint);
            p.setCheckpoints(checkpoints);
            this.parkours.put(parkour, p);
        }
    }

    protected void setCheckpoint(String parkour, Location checkpoint, int index) {
        if(this.parkours.containsKey(parkour)) {
            Parkour p = this.parkours.get(parkour);
            List<Location> checkpoints = p.getCheckpoints();
            checkpoints.set(index, checkpoint);
            p.setCheckpoints(checkpoints);
            this.parkours.put(parkour, p);
        }
    }

    protected void removeCheckpoint(String parkour, int index) {
        if(this.parkours.containsKey(parkour)) {
            Parkour p = this.parkours.get(parkour);
            List<Location> checkpoints = p.getCheckpoints();
            checkpoints.remove(index);
            p.setCheckpoints(checkpoints);
            this.parkours.put(parkour, p);
        }
    }

    public void removeParkour(String parkour) {
        this.parkours.remove(parkour);
        File parkourFile = new File(this.main.getDataFolder() + "/SeniorParkour/parkours", parkour+".yml");
        if(parkourFile.exists()) parkourFile.delete();
    }

    protected void removeParkour(Parkour parkour) {
        removeParkour(parkour.getName());
    }

    @Override
    protected void stop() {

    }
}

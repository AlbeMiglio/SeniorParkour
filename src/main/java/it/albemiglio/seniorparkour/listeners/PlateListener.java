package it.albemiglio.seniorparkour.listeners;

import it.albemiglio.seniorparkour.SeniorParkour;
import it.albemiglio.seniorparkour.objects.CurrentParkour;
import it.albemiglio.seniorparkour.objects.Parkour;
import it.albemiglio.seniorparkour.utils.TimeUtils;
import it.mycraft.powerlib.common.chat.Message;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.time.Instant;
import java.util.List;

public class PlateListener implements Listener {

    private final SeniorParkour main;

    public PlateListener(SeniorParkour main) {
        this.main = main;
    }

    @EventHandler
    public void onPressurePlateInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        Location plateLocation = event.getClickedBlock().getLocation();
        String parkour;
        if(getParkourOfStart(plateLocation) != null) {
             parkour = getParkourOfStart(plateLocation);
             if(this.main.getParkourService().getParkour(parkour).getEnd() != null) {
                 if (!this.main.getParkourService().getCurrentPlayers().containsKey(p.getUniqueId())) {
                     CurrentParkour info = new CurrentParkour(parkour, Instant.now().getEpochSecond());
                     info.setCheckpoint(0);
                     this.main.getParkourService().getCurrentPlayers().put(p.getUniqueId(), info);
                     this.main.getScoreboardService().sendScoreboard(p);
                 }
             }
        }
        if(getParkourOfEnd(plateLocation) != null) {
            parkour = getParkourOfEnd(plateLocation);
            if(this.main.getParkourService().getCurrentPlayers().containsKey(p.getUniqueId())) {
                CurrentParkour info = this.main.getParkourService().getCurrentPlayers().get(p.getUniqueId());
                if(info.getParkourName().equalsIgnoreCase(parkour)
                && info.getCheckpoint() == this.main.getParkourService()
                        .getParkour(parkour).getCheckpoints().size()) {
                    this.main.getParkourService().getCurrentPlayers().remove(p.getUniqueId());
                    this.main.getScoreboardService().sendScoreboard(p);
                    Instant end = Instant.now();
                    long time = end.getEpochSecond() - info.getStartingTime();
                    getMessage("parkour-ended")
                            .addPlaceHolder("%time", TimeUtils.formattedTime(time))
                            .send(p);
                    if(this.main.getStatsService().getStats().get(p.getUniqueId())
                            .getOrDefault(parkour, Long.MAX_VALUE) > time) {
                        getMessage("new-record").send(p);
                        this.main.getStatsService().updateTime(p.getUniqueId(), parkour, time, true);
                    }
                }
            }
        }
        if(getParkourOfCheckpoint(plateLocation) != null) {
            parkour = getParkourOfCheckpoint(plateLocation);
            if(this.main.getParkourService().getCurrentPlayers().containsKey(p.getUniqueId())) {
                CurrentParkour info = this.main.getParkourService().getCurrentPlayers().get(p.getUniqueId());
                if(info.getParkourName().equalsIgnoreCase(parkour)) {
                    if(getCheckpointNumber(plateLocation, parkour) == info.getCheckpoint() + 1) {
                        info.setCheckpoint(info.getCheckpoint()+1);
                        this.main.getParkourService().getCurrentPlayers().put(p.getUniqueId(), info);
                        getMessage("checkpoint-reached")
                                .addPlaceHolder("%n", info.getCheckpoint())
                                .send(p);
                    }
                }
            }
        }
    }

    public String getParkourOfStart(Location loc) {
        for(Parkour parkour : this.main.getParkourService().getParkours().values()) {
            if(parkour.getStart() != null && parkour.getStart().equals(loc)) {
                return parkour.getName();
            }
        }
        return null;
    }

    public String getParkourOfEnd(Location loc) {
        for(Parkour parkour : this.main.getParkourService().getParkours().values()) {
            if(parkour.getEnd() != null && parkour.getEnd().equals(loc)) {
                return parkour.getName();
            }
        }
        return null;
    }

    public String getParkourOfCheckpoint(Location loc) {
        for(Parkour parkour : this.main.getParkourService().getParkours().values()) {
            for(Location checkpoint : parkour.getCheckpoints()) {
                if(checkpoint != null && checkpoint.equals(loc)) {
                    return parkour.getName();
                }
            }
        }
        return null;
    }

    public int getCheckpointNumber(Location loc, String parkourName) {
        Parkour parkour = this.main.getParkourService().getParkour(parkourName);
        for(int i = 0; i < parkour.getCheckpoints().size(); i++) {
            if(parkour.getCheckpoints().get(i).equals(loc)) {
                return i+1;
            }
        }
        return 0;
    }

    private Message getMessage(String path) {
        return new Message(this.main.getFileService().getMessages().getString(path)).hex();
    }
}

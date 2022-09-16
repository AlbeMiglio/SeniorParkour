package it.albemiglio.seniorparkour.listeners;

import it.albemiglio.seniorparkour.SeniorParkour;
import it.albemiglio.seniorparkour.utils.LocUtils;
import it.mycraft.powerlib.common.chat.Message;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;

public class FlyListener implements Listener {

    private final SeniorParkour main;

    public FlyListener(SeniorParkour main) {
        this.main = main;
    }

    @EventHandler
    public void onFlightEvent(PlayerToggleFlightEvent event) {
        if(!event.isFlying())
            return;
        if(!this.main.getParkourService().getCurrentPlayers().containsKey(event.getPlayer().getUniqueId())) {
            return;
        }
        Location spawn = this.main.getMisc().getSpawn();
        event.getPlayer().teleport(spawn);
        event.getPlayer().setFlying(false);
        getMessage("cant-fly-during-parkour").hex().send(event.getPlayer());
    }

    private Message getMessage(String path) {
        return new Message(this.main.getFileService().getMessages().getString(path));
    }
}

package it.albemiglio.seniorparkour.guis;

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import it.albemiglio.seniorparkour.SeniorParkour;
import it.albemiglio.seniorparkour.objects.GUI;
import it.albemiglio.seniorparkour.objects.Parkour;
import org.bukkit.entity.Player;

public class TopGUI extends GUI {

    private ChestGui gui;
    private Parkour parkour;
    private SeniorParkour main;

    public TopGUI(SeniorParkour main, Parkour parkour) {
        this.main = main;
        this.parkour = parkour;
        loadGUI();
    }

    public void loadGUI() {

    }

    public void showGUI(Player p) {

    }
}

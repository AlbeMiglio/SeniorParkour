package it.albemiglio.seniorparkour.guis;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import it.albemiglio.seniorparkour.SeniorParkour;
import it.albemiglio.seniorparkour.objects.Parkour;
import it.mycraft.powerlib.bukkit.item.ItemBuilder;
import it.mycraft.powerlib.common.chat.Message;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class InfoGUI extends GUI {

    private ChestGui gui;
    private Parkour parkour;
    private SeniorParkour main;

    public InfoGUI(SeniorParkour main, Parkour parkour) {
        this.main = main;
        this.parkour = parkour;
        loadGUI();
    }

    public void loadGUI() {
        String title = getMessage("gui.info.title").getText();
        this.gui = new ChestGui(5, title);
        StaticPane pane = new StaticPane(0, 0, 9, 5);
        FileConfiguration config = this.main.getFileService().getConfig();
        pane.addItem(new GuiItem(new ItemBuilder().fromConfig(config, "gui.info.start-button").build(), e ->
                e.getWhoClicked().teleport(this.parkour.getStart())), 1, 1);
        pane.addItem(new GuiItem(new ItemBuilder().fromConfig(config, "gui.info.end-button").build(), e ->
                e.getWhoClicked().teleport(this.parkour.getEnd())), 2, 1);
        for (int i = 0; i < this.parkour.getCheckpoints().size(); i++) {
            int finalI = i;
            pane.addItem(new GuiItem(new ItemBuilder().fromConfig(config, "gui.info.checkpoint-button")
                    .addPlaceHolder("%n", i+1)
                    .build(), e ->
                    e.getWhoClicked().teleport(this.parkour.getCheckpoints().get(finalI))), 1 + (i % 9), 2 + (i / 9));
        }
        this.gui.addPane(pane);
        this.gui.setOnGlobalClick((event) -> {
            if(this.gui.getItems().stream().map(GuiItem::getItem).noneMatch((i) -> i == event.getCurrentItem())) {
                event.setCancelled(true);
            }
        });
    }

    public void showGUI(Player p) {
        this.gui.show(p);
    }

    private Message getMessage(String path) {
        return new Message(this.main.getFileService().getConfig().getString(path)).hex();
    }
}
